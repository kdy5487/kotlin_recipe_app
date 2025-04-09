package com.example.myapplication.models

data class Comment(
    val id: String = "",
    val postId: String = "",  // 댓글이 속한 게시글 ID
    val content: String = "",  // 댓글 내용
    val timestamp: Long = System.currentTimeMillis() // 댓글 작성 시간
)
