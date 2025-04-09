package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityEditPostBinding
import com.google.firebase.firestore.FirebaseFirestore

class EditPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditPostBinding
    private val firestore = FirebaseFirestore.getInstance()
    private var postId: String? = null
    private var isEditMode = false // 수정 모드 플래그

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 전달받은 데이터
        postId = intent.getStringExtra("postId")
        val postTitle = intent.getStringExtra("postTitle") ?: ""
        val postContent = intent.getStringExtra("postContent") ?: ""

        // 초기 UI 설정
        binding.tvPostTitle.text = postTitle
        binding.tvPostContent.text = postContent

        binding.btnEditSave.setOnClickListener {
            if (isEditMode) {
                saveChanges()
            } else {
                enterEditMode()
            }
        }
    }

    private fun enterEditMode() {
        isEditMode = true

        // 읽기 모드를 숨기고 수정 모드 활성화
        binding.tvPostTitle.visibility = View.GONE
        binding.tvPostContent.visibility = View.GONE
        binding.etPostTitle.visibility = View.VISIBLE
        binding.etPostContent.visibility = View.VISIBLE

        // 기존 데이터 표시
        binding.etPostTitle.setText(binding.tvPostTitle.text)
        binding.etPostContent.setText(binding.tvPostContent.text)

        // 버튼 텍스트 변경
        binding.btnEditSave.text = "저장"
    }

    private fun saveChanges() {
        val updatedTitle = binding.etPostTitle.text.toString()
        val updatedContent = binding.etPostContent.text.toString()

        if (updatedTitle.isNotBlank() && updatedContent.isNotBlank()) {
            postId?.let { id ->
                firestore.collection("posts").document(id)
                    .update("title", updatedTitle, "content", updatedContent)
                    .addOnSuccessListener {
                        Toast.makeText(this, "게시글이 수정되었습니다.", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "수정 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            Toast.makeText(this, "모든 내용을 입력하세요.", Toast.LENGTH_SHORT).show()
        }
    }
}
