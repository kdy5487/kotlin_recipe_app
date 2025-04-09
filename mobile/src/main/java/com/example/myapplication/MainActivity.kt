package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.app.Notification
import java.util.concurrent.TimeUnit
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf



class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var googleSignInClient: GoogleSignInClient

    private val RC_SIGN_IN = 9001 // Google Sign-In 요청 코드

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Firebase 인증 객체 초기화
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // 알림 채널 생성 - 앱 시작 시 한 번만 생성됩니다.
        createNotificationChannel()

        // 이미 로그인된 사용자인지 확인 후 자동 로그인 처리
        val currentUser = auth.currentUser
        if (currentUser != null) {
            goToHomeActivity()
            scheduleExpirationCheck(currentUser.uid)  // 현재 사용자 UID로 알림 확인 작업 예약
        }

        // 구글 로그인 옵션 설정
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Firebase Console에서 제공한 클라이언트 ID
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // 구글 로그인 버튼 클릭 이벤트 처리
        findViewById<Button>(R.id.googleSignInButton).setOnClickListener {
            signInWithGoogle()
        }
    }

    // WorkManager로 알림 확인 작업 예약
    private fun scheduleExpirationCheck(userId: String) {
        val workRequest = PeriodicWorkRequestBuilder<ExpirationReminderWorker>(1, TimeUnit.HOURS)
            .setInputData(workDataOf("userId" to userId))
            .build()

        WorkManager.getInstance(this).enqueue(workRequest)
    }

    // 알림 채널 생성 코드
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "expiration_reminder_channel"
            val channelName = "Expiration Notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "알림 설정을 위한 채널"
                setLockscreenVisibility(Notification.VISIBILITY_PUBLIC) // 잠금 화면에서의 가시성 설정
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    // 구글 로그인 요청
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    // 구글 로그인 결과 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d("GoogleSignIn", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w("GoogleSignIn", "Google sign in failed", e)
                Toast.makeText(this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Firebase에 구글 계정 인증
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("GoogleSignIn", "signInWithCredential:success")
                    val user = auth.currentUser

                    // Firestore에 사용자 정보가 있는지 확인
                    checkIfUserExistsInFirestore(user)
                } else {
                    Log.w("GoogleSignIn", "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Firestore에 사용자가 이미 있는지 확인하는 함수
    private fun checkIfUserExistsInFirestore(user: FirebaseUser?) {
        if (user != null) {
            val userDocRef = firestore.collection("users").document(user.uid)

            userDocRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // 이미 Firestore에 사용자 정보가 있으면 로그인만 처리
                        Log.d("Firestore", "User already exists, proceed to login.")
                        onLoginSuccess()
                    } else {
                        // Firestore에 사용자 정보가 없으면 새로 저장 (회원가입)
                        Log.d("Firestore", "New user, saving to Firestore.")
                        saveUserToFirestore(user)
                        onLoginSuccess()
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error checking user existence", e)
                }
        }
    }

    // Firestore에 사용자 정보 저장
    private fun saveUserToFirestore(user: FirebaseUser) {
        // Firestore에 저장할 사용자 데이터
        val userData = hashMapOf(
            "uid" to user.uid,
            "email" to user.email,
            "displayName" to user.displayName, // 구글 계정의 사용자 이름
            "photoUrl" to user.photoUrl.toString() // 구글 계정의 프로필 사진 URL
        )

        // Firestore의 "users" 컬렉션에 사용자 UID를 기반으로 문서 생성
        firestore.collection("users").document(user.uid)
            .set(userData)
            .addOnSuccessListener {
                Log.d("Firestore", "User data successfully written!")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error writing user data", e)
            }
    }

    // 이메일 로그인 처리
    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "Authentication Successful.", Toast.LENGTH_SHORT).show()
                    onLoginSuccess()
                } else {
                    Toast.makeText(baseContext, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // 이메일 회원가입 처리
    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "Account Creation Successful.", Toast.LENGTH_SHORT).show()
                    saveUserToFirestore(auth.currentUser!!) // Firestore에 사용자 정보 저장
                    onLoginSuccess()
                } else {
                    Toast.makeText(baseContext, "Account Creation Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // 로그인 클릭 이벤트
    fun onLoginClicked(view: View) {
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)

        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        signIn(email, password)
    }

    // 회원가입 클릭 이벤트
    fun onRegisterClicked(view: View) {
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)

        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        createAccount(email, password)
    }

    // 로그인 성공 시 홈 화면으로 이동
    private fun onLoginSuccess() {
        val intent = Intent(this, MainHomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    // 자동 로그인 시 홈 화면으로 이동
    private fun goToHomeActivity() {
        val intent = Intent(this, MainHomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
