package com.example.myapplication

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import android.Manifest
import androidx.camera.view.PreviewView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import com.google.firebase.Timestamp
import android.os.Handler
import android.os.Looper
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import java.text.SimpleDateFormat

@androidx.camera.core.ExperimentalGetImage

class CameraActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private val barcodeScanner = BarcodeScanning.getClient()
    private lateinit var previewView: PreviewView
    private val CAMERA_REQUEST_CODE = 100
    private var isProcessing = false // 스캔 처리 상태를 나타내는 플래그

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        auth = FirebaseAuth.getInstance()
        previewView = findViewById(R.id.previewView)

        requestCameraPermission() // 카메라 권한 요청
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        } else {
            startCamera() // 권한이 이미 있을 경우 카메라 시작
        }
    }

    private fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(previewView.surfaceProvider)

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            val imageAnalysis = ImageAnalysis.Builder().build().also {
                it.setAnalyzer(ContextCompat.getMainExecutor(this), { imageProxy ->
                    processImage(imageProxy)
                })
            }

            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun processImage(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            barcodeScanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        val qrData = barcode.rawValue ?: ""
                        processQrData(qrData)
                    }
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }

    private fun processQrData(qrData: String) {
        if (isProcessing) return // 이미 처리 중이면 무시

        // QR 코드 데이터에서 필요한 정보 추출
        val keyValuePairs = qrData.split(";").associate {
            val (key, value) = it.split(":").map { it.trim() }
            key to value
        }

        // URL 추출
        val url = keyValuePairs["URL"] ?: ""

        // 제품명 처리
        val productNameKeys = listOf("상품명", "상품", "제품명", "제품")
        val productName = productNameKeys.mapNotNull { key -> keyValuePairs[key] }.firstOrNull() ?: ""

        // 소비기한 처리
        val expirationDateKeys = listOf("소비기한", "유통기한")
        val expirationDateString = expirationDateKeys.mapNotNull { key -> keyValuePairs[key] }.firstOrNull() ?: ""

        // 유효성 검사
        if (productName.isNotBlank() && expirationDateString.isNotBlank()) {
            isProcessing = true // 처리 시작

            // 소비기한 문자열을 Date 객체로 변환
            val expirationDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(expirationDateString)
            if (expirationDate == null) {
                Toast.makeText(this, "소비기한 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show()
                isProcessing = false
                return
            }

            // 소비기한을 Timestamp로 변환
            val expirationTimestamp = Timestamp(expirationDate)

            // Firestore에 데이터 추가
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val uid = currentUser.uid
                val db = FirebaseFirestore.getInstance()
                val ingredientId = UUID.randomUUID().toString() // 고유 ID 생성
                val ingredient = IngredientInformation(ingredientId, expirationTimestamp, productName, "", 24) // 기본 알림 시간 24시간

                // Firestore에 데이터 추가
                db.collection("users").document(uid).collection("ingredients")
                    .add(ingredient)
                    .addOnSuccessListener { documentReference ->
                        // 데이터가 Firestore에 성공적으로 저장된 경우
                        Toast.makeText(this, "제품 정보가 저장되었습니다.", Toast.LENGTH_SHORT).show()
                        // 처리 완료 후 2초 후에 플래그 초기화
                        Handler(Looper.getMainLooper()).postDelayed({
                            isProcessing = false
                        }, 2000) // 2000ms = 2초
                        finish() // Activity 종료하여 이전 화면으로 돌아감
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Firestore에 데이터 추가 중 오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                        isProcessing = false // 오류 발생 시 처리 상태 초기화
                    }
            } else {
                Toast.makeText(this, "사용자 인증 실패", Toast.LENGTH_SHORT).show()
                isProcessing = false // 인증 실패 시 처리 상태 초기화
            }
        } else {
            Toast.makeText(this, "유효한 제품명과 소비기한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setNotification(ingredientId: String, notificationTime: Int?) {
        // 알림 설정 로직 구현
        Toast.makeText(this, "알림이 설정되었습니다.", Toast.LENGTH_SHORT).show()
    }
}
