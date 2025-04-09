package com.example.receipe_app2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeRecommendActivity : AppCompatActivity() {

    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnRecommend: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_recommend)

        recyclerView = findViewById(R.id.recyclerView_recommend)
        btnRecommend = findViewById(R.id.btn_recommend)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recipeAdapter = RecipeAdapter { recipe ->
            val intent = Intent(this, RecipeDetailActivity::class.java).apply {
                putExtra("recipe_name", recipe.rCPNM)
                putExtra("recipe_image", recipe.aTTFILENOMAIN)
                putExtra("recipe_ingredients", recipe.rCPPARTSDTLS)
                putExtra("recipe_instructions", recipe.getAllInstructions())
                putExtra("recipe_images", recipe.getAllImages())
            }
            startActivity(intent)
        }
        recyclerView.adapter = recipeAdapter

        btnRecommend.setOnClickListener {
            fetchRecommendedRecipes()
        }
    }

    private fun fetchRecommendedRecipes() {
        val apiKey = "121780846bdd4ceab936"
        val call = ApiClient.recipeApi.getRecipes(apiKey, "")

        call.enqueue(object : Callback<api_response> {
            override fun onResponse(call: Call<api_response>, response: Response<api_response>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val recipes = responseBody.cOOKRCP01.row.shuffled().take(10) // 랜덤으로 10개의 레시피 추천
                        recipeAdapter.setRecipes(recipes)
                    } else {
                        Log.e("RecipeRecommendActivity", "API 응답 바디가 null입니다.")
                    }
                } else {
                    Log.e("RecipeRecommendActivity", "API 호출 실패: ${response.message()}")
                    Log.d("RecipeRecommendActivity", "API 응답: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<api_response>, t: Throwable) {
                Log.e("RecipeRecommendActivity", "API 호출 실패", t)
            }
        })
    }
}
//추천 로직을 fetchRecommendedRecipes 메서드에 추가