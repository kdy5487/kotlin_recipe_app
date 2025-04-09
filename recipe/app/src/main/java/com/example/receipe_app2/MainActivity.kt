package com.example.receipe_app2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private lateinit var btnRecipeSearch: Button
    private lateinit var btnRecipeRecommend: Button
    private lateinit var btnIngredientManage: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        btnRecipeSearch = findViewById(R.id.btn_recipe_search)
        btnRecipeRecommend = findViewById(R.id.btn_recipe_recommend)


        btnRecipeSearch.setOnClickListener {
            val intent = Intent(this, RecipeActivity::class.java)
            startActivity(intent)
        }

        btnRecipeRecommend.setOnClickListener {
            val intent = Intent(this, RecipeRecommendActivity::class.java)
            startActivity(intent)
        }

    }
}
