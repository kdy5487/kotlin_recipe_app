package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.PostAdapter
import com.example.myapplication.databinding.ActivityCommunityBoardBinding
import com.example.myapplication.models.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class CommunityBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommunityBoardBinding
    private lateinit var postAdapter: PostAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val postsList = mutableListOf<Post>() // 전체 게시글 목록
    private var isSortedByRecommendations = false // 정렬 상태를 저장하는 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadPosts()

        // 검색 버튼 클릭 이벤트
        binding.iconSearch.setOnClickListener {
            val query = binding.searchEditText.text.toString()
            searchPosts(query)
        }

        // 추천순 정렬 버튼 클릭 이벤트
        binding.btnSortByRecommendations.setOnClickListener {
            toggleSortByRecommendations()
        }

        // 하단 네비게이션 버튼 이벤트 처리
        binding.btnHome.setOnClickListener {
            Toast.makeText(this, "홈으로 이동", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainHomeActivity::class.java))
        }

        binding.btnMyPage.setOnClickListener {
            Toast.makeText(this, "마이페이지로 이동", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MyPageActivity::class.java))
        }

        binding.btnAddPost.setOnClickListener {
            Toast.makeText(this, "글 작성 페이지로 이동", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, AddPostActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        postAdapter = PostAdapter(
            posts = mutableListOf(), // 빈 리스트로 초기화
            onPostClick = { post ->
                // 게시글 클릭 시 상세보기로 이동
                val intent = Intent(this, CommunityPostDetailActivity::class.java)
                intent.putExtra("postId", post.id)
                startActivity(intent)
            },
            onFavoriteClick = { post ->
                // 즐겨찾기 상태 변경
                toggleFavorite(post)
            }
        )
        binding.recyclerViewPosts.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewPosts.adapter = postAdapter
    }

    private fun loadPosts() {
        val currentUserId = currentUser?.uid
        firestore.collection("posts").addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) {
                Toast.makeText(this, "게시글을 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            val posts = snapshot.toObjects(Post::class.java).map { post ->
                post.apply {
                    favoritedBy = favoritedBy.toMutableList()
                    isFavorite = currentUserId in favoritedBy // 즐겨찾기 상태 동기화
                }
            }
            postsList.clear()
            postsList.addAll(posts)
            postAdapter.updatePosts(posts)
        }
    }


    private fun searchPosts(query: String) {
        val filteredList = if (query.isBlank()) {
            postsList
        } else {
            postsList.filter { it.title.contains(query, ignoreCase = true) }
        }
        postAdapter.updatePosts(filteredList) // submitList 대신 updatePosts 사용
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleSortByRecommendations() {
        if (!isSortedByRecommendations) {
            // 추천순으로 정렬
            val sortedList = postsList.sortedByDescending { it.recommendations }
            postAdapter.updatePosts(sortedList)
            Toast.makeText(this, "추천순으로 정렬되었습니다.", Toast.LENGTH_SHORT).show()
        } else {
            // 원래 순서로 복구
            postAdapter.updatePosts(postsList)
            Toast.makeText(this, "기본 순서로 돌아갔습니다.", Toast.LENGTH_SHORT).show()
        }
        isSortedByRecommendations = !isSortedByRecommendations // 상태 변경
    }

    private fun toggleFavorite(post: Post) {
        val currentUserId = currentUser?.uid
        if (currentUserId == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val postRef = firestore.collection("posts").document(post.id)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(postRef)
            val favoritedBy = snapshot.get("favoritedBy") as? MutableList<String> ?: mutableListOf()

            if (currentUserId in favoritedBy) {
                favoritedBy.remove(currentUserId) // UID 제거
            } else {
                favoritedBy.add(currentUserId) // UID 추가
            }

            transaction.update(postRef, "favoritedBy", favoritedBy) // 업데이트 실행
            favoritedBy
        }.addOnSuccessListener { updatedFavoritedBy ->
            // Firestore 트랜잭션 성공 후 UI 갱신
            post.favoritedBy = updatedFavoritedBy.toMutableList()
            post.isFavorite = currentUserId in updatedFavoritedBy
            postAdapter.notifyItemChanged(postsList.indexOf(post)) // 특정 아이템 갱신
            val message = if (post.isFavorite) "즐겨찾기에 추가되었습니다." else "즐겨찾기에서 제거되었습니다."
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "즐겨찾기 업데이트 실패: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }


}
