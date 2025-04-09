package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.PostAdapter
import com.example.myapplication.databinding.ActivityFavoritesBinding
import com.example.myapplication.models.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding
    private lateinit var postAdapter: PostAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val allFavorites = mutableListOf<Post>()
    private val favoritePosts = mutableListOf<Post>()
    private val favoriteRecipes = mutableListOf<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupBottomNavigation()

        if (currentUser != null) {
            loadFavoritePostsAndRecipes()
        } else {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupRecyclerView() {
        postAdapter = PostAdapter(
            onPostClick = { post ->
                navigateToDetail(post)
            },
            onFavoriteClick = { post ->
                toggleFavorite(post) // 즐겨찾기 상태 변경
            }
        )
        binding.recyclerViewFavorites.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewFavorites.adapter = postAdapter
    }

    private fun setupBottomNavigation() {
        binding.btnPosts.setOnClickListener {
            postAdapter.updatePosts(favoritePosts)
        }
        binding.btnRecipes.setOnClickListener {
            postAdapter.updatePosts(favoriteRecipes)
        }
        binding.btnAll.setOnClickListener {
            postAdapter.updatePosts(allFavorites)
        }
    }

    private fun loadFavoritePostsAndRecipes() {
        val userId = currentUser?.uid ?: return
        allFavorites.clear()
        favoritePosts.clear()
        favoriteRecipes.clear()

        // 게시글 즐겨찾기 불러오기
        firestore.collection("posts")
            .whereArrayContains("favoritedBy", userId)
            .get()
            .addOnSuccessListener { documents ->
                val posts = documents.toObjects(Post::class.java).map { post ->
                    post.apply {
                        isFavorite = true
                        source = "community" // 게시글의 출처를 명확히 설정
                    }
                }
                favoritePosts.addAll(posts)
                allFavorites.addAll(posts)
                postAdapter.updatePosts(allFavorites)
            }
            .addOnFailureListener {
                Toast.makeText(this, "게시글 즐겨찾기를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }

        // 레시피 즐겨찾기 불러오기
        firestore.collection("users")
            .document(userId)
            .collection("favorites")
            .get()
            .addOnSuccessListener { result ->
                val recipes = result.toObjects(Post::class.java).map { recipe ->
                    recipe.apply {
                        isFavorite = true
                        source = "api" // 레시피의 출처를 명확히 설정
                    }
                }
                favoriteRecipes.addAll(recipes)
                allFavorites.addAll(recipes)
                postAdapter.updatePosts(allFavorites)
            }
            .addOnFailureListener {
                Toast.makeText(this, "레시피 즐겨찾기를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun toggleFavorite(post: Post) {
        val userId = currentUser?.uid ?: return

        if (post.source == "community") {
            val docRef = firestore.collection("posts").document(post.id)
            if (post.isFavorite) {
                // 즐겨찾기 제거
                docRef.update("favoritedBy", FieldValue.arrayRemove(userId))
                    .addOnSuccessListener {
                        post.isFavorite = false
                        removeFromLocalFavorites(post)
                        Toast.makeText(this, "즐겨찾기에서 제거되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "즐겨찾기 제거에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // 즐겨찾기 추가
                docRef.update("favoritedBy", FieldValue.arrayUnion(userId))
                    .addOnSuccessListener {
                        post.isFavorite = true
                        addToLocalFavorites(post)
                        Toast.makeText(this, "즐겨찾기에 추가되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "즐겨찾기 추가에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
            }
        } else if (post.source == "api") {
            val favoritesRef = firestore.collection("users").document(userId).collection("favorites")
            if (post.isFavorite) {
                // 즐겨찾기 제거
                favoritesRef.document(post.id).delete()
                    .addOnSuccessListener {
                        post.isFavorite = false
                        removeFromLocalFavorites(post)
                        Toast.makeText(this, "즐겨찾기에서 제거되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "즐겨찾기 제거에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // 즐겨찾기 추가
                favoritesRef.document(post.id).set(post)
                    .addOnSuccessListener {
                        post.isFavorite = true
                        addToLocalFavorites(post)
                        Toast.makeText(this, "즐겨찾기에 추가되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "즐겨찾기 추가에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun addToLocalFavorites(post: Post) {
        if (post.source == "community" && !favoritePosts.any { it.id == post.id }) {
            favoritePosts.add(post)
        } else if (post.source == "api" && !favoriteRecipes.any { it.id == post.id }) {
            favoriteRecipes.add(post)
        }
        if (!allFavorites.any { it.id == post.id }) {
            allFavorites.add(post)
        }
        postAdapter.updatePosts(allFavorites)
    }

    private fun removeFromLocalFavorites(post: Post) {
        favoritePosts.removeAll { it.id == post.id }
        favoriteRecipes.removeAll { it.id == post.id }
        allFavorites.removeAll { it.id == post.id }
        postAdapter.updatePosts(allFavorites)
    }

    private fun navigateToDetail(post: Post) {
        if (post.source == "community") {
            val intent = Intent(this, CommunityPostDetailActivity::class.java).apply {
                putExtra("postId", post.id)
            }
            startActivity(intent)
        } else if (post.source == "api") {
            val intent = Intent(this, RecipeDetailActivity::class.java).apply {
                putExtra("recipe_name", post.title)
                putExtra("recipe_image", post.imageUrl)
                putExtra("recipe_ingredients", post.ingredients.toTypedArray())
                putExtra("recipe_instructions", post.steps.toTypedArray())
                putExtra("recipe_instruction_images", post.stepImages.toTypedArray())
            }
            startActivity(intent)
        }
    }
}
