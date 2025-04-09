package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ActivityRecipeDetailBinding

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recipeTitle = intent.getStringExtra("recipe_name")
        val recipeImage = intent.getStringExtra("recipe_image")
        val recipeIngredients = intent.getStringArrayExtra("recipe_ingredients") ?: arrayOf()
        val recipeInstructions = intent.getStringArrayExtra("recipe_instructions") ?: arrayOf()
        val recipeInstructionImages = intent.getStringArrayExtra("recipe_instruction_images") ?: arrayOf()

        displayRecipeDetails(
            title = recipeTitle ?: "Unknown Recipe",
            imageUrl = recipeImage,
            ingredients = recipeIngredients,
            instructions = recipeInstructions,
            instructionImages = recipeInstructionImages
        )
    }

    private fun displayRecipeDetails(
        title: String,
        imageUrl: String?,
        ingredients: Array<String>,
        instructions: Array<String>,
        instructionImages: Array<String>
    ) {
        binding.tvTitle.text = title
        Glide.with(this).load(imageUrl).into(binding.imgRecipe)
        binding.tvIngredients.text = ingredients.joinToString("\n") { "- $it" }

        binding.linearLayoutInstructions.removeAllViews()
        instructions.forEachIndexed { index, instruction ->
            val stepView = layoutInflater.inflate(R.layout.item_instruction, null)
            val tvStep = stepView.findViewById<TextView>(R.id.tvStep)
            val tvInstruction = stepView.findViewById<TextView>(R.id.tvInstruction)
            val imgInstruction = stepView.findViewById<ImageView>(R.id.imgInstruction)

            tvStep.text = "Step ${index + 1}"
            tvInstruction.text = instruction

            if (index < instructionImages.size && instructionImages[index].isNotBlank()) {
                imgInstruction.visibility = View.VISIBLE
                Glide.with(this).load(instructionImages[index]).into(imgInstruction)
            } else {
                imgInstruction.visibility = View.GONE
            }

            binding.linearLayoutInstructions.addView(stepView)
        }
    }
}