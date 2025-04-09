package com.example.receipe_app2

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecipeAdapter(private val onRecipeClick: (Row) -> Unit) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    private var recipes: List<Row> = listOf()

    fun setRecipes(recipes: List<Row>) {
        this.recipes = recipes
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view, onRecipeClick)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    class RecipeViewHolder(itemView: View, private val onRecipeClick: (Row) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.textView_title)
        private val imageView: ImageView = itemView.findViewById(R.id.imageview_food)

        fun bind(recipe: Row) {
            titleTextView.text = recipe.rCPNM
            Glide.with(itemView.context).load(recipe.aTTFILENOMAIN).into(imageView)

            itemView.setOnClickListener {
                onRecipeClick(recipe)
            }
        }
    }
}
