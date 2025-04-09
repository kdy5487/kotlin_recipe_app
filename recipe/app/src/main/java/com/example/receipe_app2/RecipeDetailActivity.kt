package com.example.receipe_app2

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class RecipeDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        val recipeName = intent.getStringExtra("recipe_name")
        val recipeImage = intent.getStringExtra("recipe_image")
        val recipeIngredients = intent.getStringExtra("recipe_ingredients")
        val recipeInstructions = intent.getStringArrayExtra("recipe_instructions")
        val recipeImages = intent.getStringArrayExtra("recipe_images")

        val nameTextView: TextView = findViewById(R.id.recipe_name)
        val imageView: ImageView = findViewById(R.id.recipe_image)
        val ingredientsTextView: TextView = findViewById(R.id.recipe_ingredients)
        val instructionsLayout: LinearLayout = findViewById(R.id.instructions_layout)

        nameTextView.text = recipeName
        Glide.with(this).load(recipeImage).into(imageView)
        ingredientsTextView.text = recipeIngredients

        recipeInstructions?.forEachIndexed { index, instruction ->
            if (instruction.isNotBlank()) {
                val instructionTextView = TextView(this)
                instructionTextView.text = instruction
                instructionsLayout.addView(instructionTextView)

                val imageUrl = recipeImages?.get(index)
                if (!imageUrl.isNullOrBlank()) {
                    val instructionImageView = ImageView(this)
                    Glide.with(this).load(imageUrl).into(instructionImageView)
                    instructionsLayout.addView(instructionImageView)
                }
            }
        }
    }
}
