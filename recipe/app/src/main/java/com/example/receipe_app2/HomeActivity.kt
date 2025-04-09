package com.example.receipe_app2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var btnSearch: Button
    private lateinit var btnRecommend: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        btnSearch = findViewById(R.id.btn_recipe_search)
        btnRecommend = findViewById(R.id.btn_recipe_recommend)

        btnSearch.setOnClickListener {
            val intent = Intent(this, RecipeActivity::class.java)
            startActivity(intent)
        }

        btnRecommend.setOnClickListener {
            val intent = Intent(this, RecipeRecommendActivity::class.java)
            startActivity(intent)
        }
    }
}
