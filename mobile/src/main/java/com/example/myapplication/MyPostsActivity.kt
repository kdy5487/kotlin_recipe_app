package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.PostAdapter
import com.example.myapplication.databinding.ActivityMyPostsBinding
import com.example.myapplication.models.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyPostsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyPostsBinding
    private lateinit var postAdapter: PostAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val myPosts = mutableListOf<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPostsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        if (currentUser != null) {
            loadMyPosts()
        } else {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupRecyclerView() {
        postAdapter = PostAdapter(
            onPostClick = { post ->
                // 게시글 상세보기로 이동
                val intent = Intent(this, CommunityPostDetailActivity::class.java)
                intent.putExtra("postId", post.id)
                intent.putExtra("source", "my_posts") // 마이페이지에서 접근 표시
                startActivity(intent)
            }
        )
        binding.recyclerViewMyPosts.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewMyPosts.adapter = postAdapter
    }

    private fun loadMyPosts() {
        firestore.collection("posts")
            .whereEqualTo("userId", currentUser?.uid)
            .get()
            .addOnSuccessListener { documents ->
                myPosts.clear()
                for (document in documents) {
                    val post = document.toObject(Post::class.java)
                    myPosts.add(post)
                }
                postAdapter.updatePosts(myPosts) // submitList 대신 updatePosts 사용
            }
            .addOnFailureListener {
                Toast.makeText(this, "내 게시글을 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
    }
}
