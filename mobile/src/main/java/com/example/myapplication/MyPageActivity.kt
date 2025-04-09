package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class MyPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        // 게시글 관리 클릭
        val managePostsLayout = findViewById<LinearLayout>(R.id.layout_manage_posts)
        managePostsLayout.setOnClickListener {
            // 게시글 관리 화면으로 이동
            val intent = Intent(this, MyPostsActivity::class.java)
            startActivity(intent)
        }

        // 즐겨찾기 관리 클릭
        val manageFavoritesLayout = findViewById<LinearLayout>(R.id.layout_manage_favorites)
        manageFavoritesLayout.setOnClickListener {
            // 즐겨찾기 관리 화면으로 이동
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent)
        }
    }
}
