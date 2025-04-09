package com.example.myapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import android.app.Notification
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import java.util.concurrent.TimeUnit
import java.util.Date

class ExpirationReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val userId = inputData.getString("userId") ?: return Result.failure()

        // Firestore 인스턴스 초기화
        val firestore = FirebaseFirestore.getInstance()

        // Firestore에서 사용자 재료 정보 가져오기
        firestore.collection("users").document(userId).collection("ingredients")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val ingredientName = document.getString("name") ?: continue
                    val expirationDate = document.getTimestamp("date")?.toDate() ?: continue

                    // 유통기한 계산
                    val remainingTime = calculateRemainingTime(expirationDate)

                    // 유통기한이 임박한 경우 알림 전송
                    if (remainingTime.first < THRESHOLD_HOURS) {
                        sendNotification(ingredientName, remainingTime.second)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error getting documents: ", e)
                Result.failure()
            }

        return Result.success()
    }

    private fun sendNotification(ingredientName: String, remainingTime: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        // 알림 빌드 및 전송
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("유통기한 알림")
            .setContentText("$ingredientName 의 유통기한이 $remainingTime 남았습니다.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun calculateRemainingTime(expirationDate: Date): Pair<Int, String> {
        val now = Date()
        val diff = expirationDate.time - now.time
        val hoursRemaining = TimeUnit.MILLISECONDS.toHours(diff).toInt()

        val remainingTimeString = if (hoursRemaining > 0) {
            "$hoursRemaining 시간"
        } else {
            "지났습니다."
        }

        return Pair(hoursRemaining, remainingTimeString)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Expiration Reminder"
            val descriptionText = "유통기한 알림을 위한 채널"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setLockscreenVisibility(Notification.VISIBILITY_PUBLIC)
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "expiration_reminder_channel"
        private const val NOTIFICATION_ID = 1
        private const val THRESHOLD_HOURS = 24 // 24시간 이내의 유통기한 알림
    }
}

