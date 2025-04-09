package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class RecipeHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipehome)

        // 레시피 검색 버튼 클릭 이벤트
        val searchRecipeLayout = findViewById<LinearLayout>(R.id.layout_recipe_search)
        searchRecipeLayout.setOnClickListener {
            val intent = Intent(this, RecipeActivity::class.java)
            startActivity(intent)
        }

        // 레시피 추천 버튼 클릭 이벤트
        val recommendRecipeLayout = findViewById<LinearLayout>(R.id.layout_recipe_recommend)
        recommendRecipeLayout.setOnClickListener {
            val intent = Intent(this, RecipeRecommendActivity::class.java)
            startActivity(intent)
        }

        val homeButton = findViewById<LinearLayout>(R.id.button_home)
        val myPageButton = findViewById<LinearLayout>(R.id.button_my_page)

        // 홈으로 이동
        homeButton.setOnClickListener {
            val intent = Intent(this, MainHomeActivity::class.java)
            startActivity(intent)
        }

        // 마이페이지로 이동
        myPageButton.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }
    }

}
