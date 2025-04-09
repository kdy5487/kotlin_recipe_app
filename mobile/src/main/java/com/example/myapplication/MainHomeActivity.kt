package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ExperimentalGetImage
import com.google.firebase.auth.FirebaseAuth

class MainHomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    @ExperimentalGetImage
    @OptIn(ExperimentalGetImage::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mainhome)

        // FirebaseAuth 인스턴스 초기화
        auth = FirebaseAuth.getInstance()

        // 재료 관리 클릭
        val ingredientLayout = findViewById<LinearLayout>(R.id.layout_ingredient)
        ingredientLayout.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            Toast.makeText(this, "재료 관리로 이동", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }

        // 레시피 추천 클릭
        val recipeLayout = findViewById<LinearLayout>(R.id.layout_recipe)
        recipeLayout.setOnClickListener {
            val intent = Intent(this, RecipeHomeActivity::class.java)
            Toast.makeText(this, "레시피 홈으로 이동", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }

        // 커뮤니티 클릭
        val communityLayout = findViewById<LinearLayout>(R.id.layout_community)
        communityLayout.setOnClickListener {
            val intent = Intent(this, CommunityBoardActivity::class.java)
            Toast.makeText(this, "커뮤니티로 이동", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }

        // 로그아웃 클릭
        val logoutButton = findViewById<Button>(R.id.button_logout)
        logoutButton.setOnClickListener {
            // Firebase 로그아웃
            auth.signOut()
            Toast.makeText(this, "로그아웃", Toast.LENGTH_SHORT).show()
            // 로그인 화면으로 이동
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // 하단 네비게이션 - 홈 클릭
        val homeButton = findViewById<LinearLayout>(R.id.button_home)
        homeButton.setOnClickListener {
            val intent = Intent(this, MainHomeActivity::class.java)
            Toast.makeText(this, "홈으로 이동", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }

        // 하단 네비게이션 - 마이페이지 클릭
        val myPageButton = findViewById<LinearLayout>(R.id.button_my_page)
        myPageButton.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            Toast.makeText(this, "마이페이지로 이동", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }
    }
}
