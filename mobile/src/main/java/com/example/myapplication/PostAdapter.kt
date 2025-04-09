package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.models.Post

class PostAdapter(
    private var posts: MutableList<Post> = mutableListOf(), // 기본값: 빈 리스트
    private val onPostClick: (Post) -> Unit, // 게시글 클릭 이벤트
    private val onFavoriteClick: ((Post) -> Unit)? = null // 즐겨찾기 클릭 이벤트, 기본값 null
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int = posts.size

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvPostTitle)
        private val tvContentPreview: TextView = itemView.findViewById(R.id.tvPostContentPreview)
        private val tvRecommendations: TextView = itemView.findViewById(R.id.tvPostRecommendations)
        private val btnFavorite: ImageButton = itemView.findViewById(R.id.btnFavorite)

        fun bind(post: Post) {
            tvTitle.text = post.title
            tvContentPreview.text = post.content.take(100) + "..."
            tvRecommendations.text = "추천: ${post.recommendations}"

            // 게시글 클릭 이벤트
            itemView.setOnClickListener {
                onPostClick(post)
            }

            // 즐겨찾기 버튼 상태 업데이트
            btnFavorite.setImageResource(
                if (post.isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
            )

            // 즐겨찾기 버튼 클릭 이벤트
            btnFavorite.setOnClickListener {
                post.isFavorite = !post.isFavorite // 상태 토글
                notifyItemChanged(adapterPosition) // UI 즉시 반영
                onFavoriteClick?.invoke(post) // 외부 콜백 호출 (Firestore 업데이트 처리)
            }


        }
    }


    // 데이터 갱신 메서드
    fun updatePosts(newPosts: List<Post>) {
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
    }


    // 추가 메서드: 데이터에 단일 Post 추가
    fun addPost(post: Post) {
        posts.add(post)
        notifyItemInserted(posts.size - 1)
    }

    // 추가 메서드: 데이터에 여러 Post 추가
    fun addPosts(newPosts: List<Post>) {
        val startPosition = posts.size
        posts.addAll(newPosts)
        notifyItemRangeInserted(startPosition, newPosts.size)
    }
}
