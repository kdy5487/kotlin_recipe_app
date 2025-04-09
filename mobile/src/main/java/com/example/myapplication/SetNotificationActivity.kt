package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import android.util.Log
import java.util.UUID

class SetNotificationActivity : AppCompatActivity() {
    private lateinit var editTextNotificationTime: EditText
    private lateinit var setButton: Button
    private var ingredientId: String? = null // 선택한 재료의 ID
    private lateinit var auth: FirebaseAuth // FirebaseAuth 변수 선언

    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001 // 권한 요청 코드

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_notification)

        // FirebaseAuth 초기화
        auth = FirebaseAuth.getInstance()

        editTextNotificationTime = findViewById(R.id.edit_text_notification_time)
        setButton = findViewById(R.id.button_set_notification)

        // Intent로부터 재료 ID를 받아옵니다.
        ingredientId = intent.getStringExtra("ingredient_id")
        Log.d("SetNotificationActivity", "Ingredient ID: $ingredientId")

        setButton.setOnClickListener {
            val enteredTime = editTextNotificationTime.text.toString().toIntOrNull()
            if (enteredTime != null && enteredTime in 1..72) {
                // 알림 권한 확인 및 요청
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_PERMISSION_REQUEST_CODE)
                } else {
                    // 권한이 이미 부여된 경우 알림 설정
                    scheduleNotificationForIngredient(enteredTime)
                }
            } else {
                Toast.makeText(this, "1에서 72 사이의 숫자를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 사용자 권한 요청 결과 처리
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용된 경우
                val enteredTime = editTextNotificationTime.text.toString().toIntOrNull()
                if (enteredTime != null) {
                    scheduleNotificationForIngredient(enteredTime)
                }
            } else {
                // 권한이 거부된 경우
                Toast.makeText(this, "알림 권한이 거부되었습니다. 알림을 받을 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun scheduleNotificationForIngredient(timeInHours: Int) {
        if (ingredientId != null) {
            val db = FirebaseFirestore.getInstance()
            val userId = auth.currentUser?.uid
            if (userId != null) {
                db.collection("users").document(userId)
                    .collection("ingredients").document(ingredientId!!)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val timestamp = document.getTimestamp("date")
                            if (timestamp != null) {
                                val date = timestamp.toDate()
                                val now = System.currentTimeMillis()
                                val delay = date.time - now - TimeUnit.HOURS.toMillis(timeInHours.toLong())

                                Log.d("ScheduleNotification", "Calculated delay: $delay")

                                if (delay > 0) {
                                    val ingredientName = document.getString("name") ?: "Unknown Ingredient"
                                    val remainingTimeText = "${timeInHours}시간"

                                    val uniqueNotificationId = ingredientId!!.hashCode()

                                    val workRequest = OneTimeWorkRequestBuilder<ExpirationReminderWorker>()
                                        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                                        .setInputData(
                                            workDataOf(
                                                "ingredient_name" to ingredientName,
                                                "remaining_time" to remainingTimeText,
                                                "unique_notification_id" to uniqueNotificationId.toString()
                                            )
                                        )
                                        .build()

                                    WorkManager.getInstance(this).enqueue(workRequest)
                                    Toast.makeText(this, "알림이 설정되었습니다.", Toast.LENGTH_SHORT).show()
                                    finish()
                                } else {
                                    Toast.makeText(this, "유통기한이 이미 지났거나 설정 시간이 잘못되었습니다.", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this, "유통기한 정보를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this, "재료 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "재료 정보를 가져오는 중 오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "사용자 인증 실패", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "재료 ID가 설정되지 않았습니다.", Toast.LENGTH_SHORT).show()
        }
    }

}

