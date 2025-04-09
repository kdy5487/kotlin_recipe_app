package com.example.myapplication

import IngredientAdapter
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ExperimentalGetImage
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class Manage_ingredient : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: IngredientAdapter
    private val ingredients = mutableListOf<IngredientInformation>()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var imageUri: Uri
    private val PICK_IMAGE_REQUEST = 1

    @OptIn(ExperimentalGetImage::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_ingredients)

        // Firebase 초기화
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()

        recyclerView = findViewById(R.id.recyclerViewIngredients)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = IngredientAdapter(ingredients) { ingredient, isChecked -> }
        recyclerView.adapter = adapter

        loadIngredients()

        // 버튼 클릭 이벤트 처리
        val btnNavigate = findViewById<Button>(R.id.button2)
        btnNavigate.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        val btnDeleteSelected = findViewById<Button>(R.id.button3)
        btnDeleteSelected.setOnClickListener {
            deleteSelectedItems()
        }
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data!!
            // 알림 시간을 입력받는 로직을 추가해야 합니다 (예: EditText에서 값을 가져옴)
            val notificationTime = 24 // 예시: 24시간으로 설정, 사용자의 입력을 반영해야 함
            uploadImageToFirebase(notificationTime)
        }
    }

    private fun uploadImageToFirebase(notificationTime: Int) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            val fileName = "${System.currentTimeMillis()}.jpg"
            val storageRef = storage.reference.child("images/$uid/$fileName")

            storageRef.putFile(imageUri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        saveImageUrlToFirestore(downloadUrl, notificationTime) // 알림 시간 전달
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Upload Error", "이미지 업로드 실패: ${e.message}")
                    Toast.makeText(this, "이미지 업로드에 실패했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "사용자 인증 실패", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImageUrlToFirestore(imageUrl: String, notificationTime: Int) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            val data = hashMapOf(
                "name" to "Ingredient Name", // 여기에서 이름을 적절히 수정해야 합니다.
                "date" to Timestamp.now(),
                "imageUrl" to imageUrl,
                "notificationTime" to notificationTime // 알림 시간을 Firestore에 저장
            )

            db.collection("users").document(uid).collection("ingredients")
                .add(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "이미지 및 데이터가 Firestore에 저장되었습니다.", Toast.LENGTH_SHORT).show()
                    loadIngredients()  // 새로 저장된 데이터를 다시 로드
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Firestore에 데이터 저장 중 오류가 발생했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    @OptIn(ExperimentalGetImage::class)
    private fun loadIngredients() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            db.collection("users").document(uid).collection("ingredients")
                .get()
                .addOnSuccessListener { result ->
                    val ingredientList = mutableListOf<IngredientInformation>()
                    for (document in result) {
                        val id = document.id // Firestore 문서 ID를 가져옵니다
                        val date = document.getTimestamp("date") ?: Timestamp.now()
                        val name = document.getString("name") ?: ""
                        val imageUrl = document.getString("imageUrl") ?: ""
                        val notificationTime = document.getLong("notificationTime")?.toInt()

                        val ingredient = IngredientInformation(id, date, name, imageUrl, notificationTime) // ID 포함
                        ingredientList.add(ingredient)
                    }
                    ingredients.clear()
                    ingredients.addAll(ingredientList)
                    adapter.setIngredients(ingredientList)
                }
                .addOnFailureListener { e ->
                    Log.e("Manage_ingredient", "Firestore에서 데이터를 불러오는 중 오류 발생: ", e)
                }
        }
    }

    @OptIn(ExperimentalGetImage::class)
    private fun deleteSelectedItems() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            val selectedItems = adapter.getSelectedItems() // 선택된 아이템을 가져옵니다.
            if (selectedItems.isEmpty()) {
                Toast.makeText(this, "삭제할 항목이 선택되지 않았습니다.", Toast.LENGTH_SHORT).show()
                return
            }
            selectedItems.forEach { ingredient ->
                db.collection("users").document(uid).collection("ingredients")
                    .whereEqualTo("name", ingredient.name)
                    .whereEqualTo("date", ingredient.date)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            db.collection("users").document(uid).collection("ingredients").document(document.id).delete()
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Firestore에서 ${ingredient.name} 데이터가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Firestore에서 데이터 삭제 중 오류가 발생했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                        // 선택된 아이템 목록에서 삭제
                        ingredients.removeAll(selectedItems)
                        adapter.setIngredients(ingredients)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Firestore에서 데이터 조회 중 오류가 발생했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            Toast.makeText(this, "사용자 인증 실패", Toast.LENGTH_SHORT).show()
        }
    }
}
