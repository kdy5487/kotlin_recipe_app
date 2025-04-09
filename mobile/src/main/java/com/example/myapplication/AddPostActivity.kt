package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityAddPostBinding
import com.example.myapplication.models.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AddPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPostBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 레시피 체크박스 클릭 이벤트
        binding.checkboxRecipe.setOnCheckedChangeListener { _, isChecked ->
            binding.etIngredients.visibility = if (isChecked) View.VISIBLE else View.GONE
            binding.etInstructions.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        // 이미지 선택 버튼
        binding.btnSelectImage.setOnClickListener { openImagePicker() }

        // 게시글 저장 버튼
        binding.btnSavePost.setOnClickListener { savePost() }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            binding.imgSelectedImage.visibility = View.VISIBLE
            binding.imgSelectedImage.setImageURI(selectedImageUri)
        }
    }

    private fun savePost() {
        val title = binding.etTitle.text.toString().trim()
        val content = binding.etContent.text.toString().trim()
        val isRecipe = binding.checkboxRecipe.isChecked
        val ingredients = binding.etIngredients.text.toString().trim().split("\n").filter { it.isNotBlank() }
        val instructions = binding.etInstructions.text.toString().trim().split("\n").filter { it.isNotBlank() }

        if (title.isBlank() || content.isBlank()) {
            Toast.makeText(this, "제목과 내용을 입력하세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val postId = UUID.randomUUID().toString()
        val post = Post(
            id = postId,
            title = title,
            content = content,
            ingredients = if (isRecipe) ingredients else emptyList(),
            instructions = if (isRecipe) instructions else emptyList(),
            userId = currentUser.uid,
            imageUrl = selectedImageUri?.toString() ?: "noimage", // 이미지가 없으면 기본값 설정
            recommendations = 0,
            recommended = false,
            source = "community"
        )

        firestore.collection("posts").document(postId)
            .set(post)
            .addOnSuccessListener {
                Toast.makeText(this, "게시글이 저장되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "게시글 저장 실패: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
