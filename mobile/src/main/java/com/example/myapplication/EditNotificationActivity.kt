package com.example.myapplication


import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import java.util.*


class EditNotificationActivity : AppCompatActivity() {

    private lateinit var editTextNotification: EditText
    private lateinit var buttonSave: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var ingredientId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_notification)

        // Firebase 초기화
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        editTextNotification = findViewById(R.id.editTextNotification)
        buttonSave = findViewById(R.id.buttonSave)

        // Intent에서 ingredientId를 가져옴
        ingredientId = intent.getStringExtra("ingredient_id")

        // Firestore에서 현재 알림 정보를 가져옴
        loadCurrentNotification()

        // 저장 버튼 클릭 시 알림 정보를 Firestore에 저장
        buttonSave.setOnClickListener {
            saveNotification()
        }
    }

    private fun loadCurrentNotification() {
        val currentUser = auth.currentUser
        if (currentUser != null && ingredientId != null) {
            val ingredientRef = db.collection("users").document(currentUser.uid)
                .collection("ingredients").document(ingredientId!!)

            ingredientRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // notificationTime 값을 Long 타입으로 받아와서 Int로 변환
                        val notificationTime = document.getLong("notificationTime")?.toInt()
                        if (notificationTime != null) {
                            editTextNotification.setText(notificationTime.toString())
                            Log.d("EditNotificationActivity", "Notification time loaded: $notificationTime")
                        } else {
                            Log.d("EditNotificationActivity", "Notification time field is null or missing")
                            Toast.makeText(this, "알림 정보가 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.d("EditNotificationActivity", "Document does not exist: $ingredientId")
                        Toast.makeText(this, "재료 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("EditNotificationActivity", "Error loading notification: ${e.message}")
                    Toast.makeText(this, "알림 정보를 불러오는 중 오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "사용자 인증 실패 또는 재료 ID가 설정되지 않았습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveNotification() {
        val currentUser = auth.currentUser
        if (currentUser != null && ingredientId != null) {
            val notificationTime = editTextNotification.text.toString().toIntOrNull()

            if (notificationTime != null && notificationTime in 1..72) {
                val ingredientRef = db.collection("users").document(currentUser.uid)
                    .collection("ingredients").document(ingredientId!!)

                // notificationTime 필드 업데이트
                ingredientRef.update("notificationTime", notificationTime)
                    .addOnSuccessListener {
                        Toast.makeText(this, "알림 정보가 수정되었습니다.", Toast.LENGTH_SHORT).show()
                        finish() // Activity 종료
                    }
                    .addOnFailureListener { e ->
                        Log.e("EditNotificationActivity", "Error updating notification: ${e.message}")
                        Toast.makeText(this, "알림 정보 수정 중 오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "1에서 72 사이의 유효한 숫자를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "사용자 인증 실패 또는 재료 ID가 설정되지 않았습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
