package com.example.myapplication

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ExperimentalGetImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.Timestamp
import android.util.Log
@androidx.camera.core.ExperimentalGetImage


data class IngredientInformation(
    val id: String, // 고유한 식별자를 추가합니다
    val date: Timestamp,
    val name: String,
    val imageUrl: String,
    val notificationTime: Int?
)



class AddIngredientActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var editTextDate: EditText
    private lateinit var editTextName: EditText
    private lateinit var editTextNotificationTime: EditText // 알림 시간 입력을 위한 EditText 변수 추가
    private lateinit var imageView: ImageView
    private var selectedImageUri: Uri? = null
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_ingredient)

        auth = FirebaseAuth.getInstance()

        editTextDate = findViewById(R.id.textView10)
        editTextName = findViewById(R.id.textView6)
        editTextNotificationTime = findViewById(R.id.edit_text_notification_time) // XML에서 EditText 초기화
        imageView = findViewById(R.id.image_preview)
        val saveButton: Button = findViewById(R.id.button)
        val closeButton: Button = findViewById(R.id.button4)
        val naverButton: Button = findViewById(R.id.online_purchase)
        val imageButton: Button = findViewById(R.id.select_image_button)

        editTextDate.setOnClickListener {
            showDatePickerDialog()
        }

        imageButton.setOnClickListener {
            openGallery()
        }

        saveButton.setOnClickListener {
            val userInputDate = calendar.time
            val userInputName = editTextName.text.toString()
            val enteredNotificationTime = editTextNotificationTime.text.toString().toIntOrNull() // 알림 시간 입력 받기

            if (userInputName.isNotBlank() && selectedImageUri != null) {
                saveButton.isEnabled = false

                // notificationTime을 전달하도록 수정
                uploadImageAndSaveUserInputs(userInputDate, userInputName, enteredNotificationTime) {
                    saveButton.isEnabled = true
                }
            } else {
                Toast.makeText(this, "재료 이름과 이미지를 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }

        closeButton.setOnClickListener {
            finish()
        }

        naverButton.setOnClickListener {
            openNaverWebsite()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            imageView.setImageURI(selectedImageUri)
        }
    }

    private fun uploadImageAndSaveUserInputs(date: Date, name: String, notificationTime: Int?, onComplete: () -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null && selectedImageUri != null) {
            val uid = currentUser.uid
            val storage = FirebaseStorage.getInstance()
            val imageRef = storage.reference.child("users/$uid/images/${System.currentTimeMillis()}.jpg")

            imageRef.putFile(selectedImageUri!!)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        saveUserInputs(date, name, uri.toString(), notificationTime, onComplete) // notificationTime 전달
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "이미지 업로드 중 오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                    onComplete()
                }
        } else {
            Toast.makeText(this, "사용자 인증 실패 또는 이미지 선택 안됨", Toast.LENGTH_SHORT).show()
            onComplete()
        }
    }

    @OptIn(ExperimentalGetImage::class)
    private fun saveUserInputs(date: Date, name: String, imageUrl: String, notificationTime: Int?, onComplete: () -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            val db = FirebaseFirestore.getInstance()
            val ingredientId = UUID.randomUUID().toString() // 고유 ID 생성
            val ingredient = IngredientInformation(ingredientId, Timestamp(date), name, imageUrl, notificationTime) // ID 포함

            db.collection("users").document(uid).collection("ingredients")
                .whereEqualTo("name", name)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        db.collection("users").document(uid).collection("ingredients")
                            .add(ingredient)
                            .addOnSuccessListener { documentReference ->
                                val ingredientId = documentReference.id
                                setNotification(ingredientId, notificationTime)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Firestore에 데이터 추가 중 오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "이미 존재하는 재료입니다.", Toast.LENGTH_SHORT).show()
                    }
                    onComplete()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Firestore 쿼리 중 오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                    onComplete()
                }
        }
    }
    private fun setNotification(ingredientId: String, notificationTime: Int?) {
        // 여기서 알림 설정 로직을 구현합니다.
        Toast.makeText(this, "알림이 설정되었습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun showDatePickerDialog() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }

        DatePickerDialog(
            this@AddIngredientActivity,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDateInView() {
        val format = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(format, Locale.US)
        editTextDate.setText(sdf.format(calendar.time))
    }

    private fun openNaverWebsite() {
        val url = "https://shopping.naver.com/home"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}
