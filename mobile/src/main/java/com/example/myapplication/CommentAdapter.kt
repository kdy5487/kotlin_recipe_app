package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.models.Comment

class CommentAdapter(
    private val comments: MutableList<Comment>, // 댓글 리스트를 어댑터에 직접 전달
    private val onDeleteClick: (Comment) -> Unit
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.bind(comment)
    }

    override fun getItemCount(): Int = comments.size

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCommentContent: TextView = itemView.findViewById(R.id.tvCommentContent)
        private val btnDeleteComment: ImageButton = itemView.findViewById(R.id.btnDeleteComment)

        fun bind(comment: Comment) {
            tvCommentContent.text = comment.content
            btnDeleteComment.setOnClickListener {
                onDeleteClick(comment)
            }
        }
    }

    // 새로운 댓글 리스트로 어댑터 데이터 업데이트
    fun updateComments(newComments: List<Comment>) {
        comments.clear()
        comments.addAll(newComments)
        notifyDataSetChanged()
    }
}
