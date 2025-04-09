package com.example.myapplication

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myapplication.adapters.CommentAdapter
import com.example.myapplication.databinding.ActivityCommunityPostDetailBinding
import com.example.myapplication.models.Comment
import com.example.myapplication.models.Post
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class CommunityPostDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommunityPostDetailBinding
    private val firestore = FirebaseFirestore.getInstance()
    private var currentPost: Post? = null
    private var postId: String? = null
    private var source: String? = null
    private val comments = mutableListOf<Comment>()
    private lateinit var commentAdapter: CommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityPostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        postId = intent.getStringExtra("postId")
        source = intent.getStringExtra("source")

        if (postId == null) {
            Toast.makeText(this, "게시글 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            setupRecyclerView()
            loadPostDetails()
            loadComments()
        }

        binding.btnRecommend.setOnClickListener {
            recommendPost()
        }

        binding.btnAddComment.setOnClickListener {
            addComment()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_post_detail, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)
        val isMyPost = source == "my_posts"
        menu?.findItem(R.id.action_edit_post)?.isVisible = isMyPost
        menu?.findItem(R.id.action_delete_post)?.isVisible = isMyPost
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit_post -> {
                toggleEditMode()
                return true
            }
            R.id.action_delete_post -> {
                deletePost()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupRecyclerView() {
        commentAdapter = CommentAdapter(
            comments = mutableListOf(),
            onDeleteClick = { comment ->
                deleteComment(comment)
            }
        )
        binding.recyclerViewComments.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewComments.adapter = commentAdapter
    }

    private fun loadPostDetails() {
        if (postId == null) return

        firestore.collection("posts").document(postId!!)
            .get()
            .addOnSuccessListener { document ->
                val post = document.toObject(Post::class.java)
                if (post != null) {
                    currentPost = post
                    displayPostDetails(post)
                } else {
                    Toast.makeText(this, "게시글을 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "게시글 로드 실패: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun displayPostDetails(post: Post) {
        binding.tvTitle.text = post.title
        binding.tvContent.text = post.content
        binding.tvIngredients.text = post.ingredients.joinToString("\n")
        binding.tvInstructions.text = post.instructions.joinToString("\n")
        binding.tvRecommendationCount.text = "추천 수: ${post.recommendations}"

        Glide.with(this).load(post.imageUrl ?: R.drawable.no_image_icon).into(binding.imgRecipe)
    }

    private fun toggleEditMode() {
        Toast.makeText(this, "수정 모드 활성화 준비 중...", Toast.LENGTH_SHORT).show()
    }

    private fun deletePost() {
        if (postId == null) return

        firestore.collection("posts").document(postId!!)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "게시글이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "게시글 삭제 실패: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addComment() {
        val commentContent = binding.editTextComment.text.toString().trim()

        if (commentContent.isBlank()) {
            Toast.makeText(this, "댓글 내용을 입력하세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val postId = currentPost?.id ?: return
        val commentId = firestore.collection("comments").document().id
        val comment = Comment(
            id = commentId,
            postId = postId,
            content = commentContent,
            timestamp = System.currentTimeMillis()
        )

        firestore.collection("comments").document(commentId)
            .set(comment)
            .addOnSuccessListener {
                Toast.makeText(this, "댓글이 추가되었습니다.", Toast.LENGTH_SHORT).show()
                binding.editTextComment.text.clear()
                loadComments()
            }
            .addOnFailureListener {
                Toast.makeText(this, "댓글 추가 실패: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadComments() {
        if (postId == null) return

        firestore.collection("comments").whereEqualTo("postId", postId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(this, "댓글 로드 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                val newComments = snapshot?.toObjects(Comment::class.java).orEmpty()
                commentAdapter.updateComments(newComments)
            }
    }

    private fun deleteComment(comment: Comment) {
        firestore.collection("comments").document(comment.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "댓글이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "댓글 삭제 실패: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun recommendPost() {
        if (postId == null) return

        firestore.collection("posts").document(postId!!)
            .update("recommendations", FieldValue.increment(1))
            .addOnSuccessListener {
                Toast.makeText(this, "추천되었습니다!", Toast.LENGTH_SHORT).show()
                loadPostDetails()
            }
            .addOnFailureListener {
                Toast.makeText(this, "추천 실패: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
