package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.models.Post

class RecipeAdapter(
    private val onRecipeClick: (Post) -> Unit,
    private val onFavoriteClick: (Post) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    private var recipes: MutableList<Post> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe)
    }

    override fun getItemCount(): Int = recipes.size

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvSource: TextView = itemView.findViewById(R.id.tvSource)
        private val imgRecipe: ImageView = itemView.findViewById(R.id.imgRecipe)
        private val btnFavorite: ImageButton = itemView.findViewById(R.id.btnFavorite)

        fun bind(recipe: Post) {
            // 제목 설정
            tvTitle.text = recipe.title

            // 출처 설정
            tvSource.text = "출처: ${recipe.source}"

            // 이미지 설정 (기본 이미지 추가)
            Glide.with(itemView.context)
                .load(recipe.imageUrl)
                .placeholder(R.drawable.no_image_icon) // 기본 이미지 설정
                .error(R.drawable.no_image_icon) // 에러 시 기본 이미지
                .into(imgRecipe)

            // 즐겨찾기 버튼 상태
            btnFavorite.setImageResource(
                if (recipe.isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
            )

            // 즐겨찾기 클릭 이벤트
            btnFavorite.setOnClickListener {
                recipe.isFavorite = !recipe.isFavorite
                notifyItemChanged(adapterPosition)
                onFavoriteClick(recipe)
            }

            // 레시피 카드 클릭 이벤트
            itemView.setOnClickListener {
                onRecipeClick(recipe)
            }
        }
    }

    fun setRecipes(newRecipes: List<Post>) {
        recipes.clear()
        recipes.addAll(newRecipes)
        notifyDataSetChanged()
    }
}
