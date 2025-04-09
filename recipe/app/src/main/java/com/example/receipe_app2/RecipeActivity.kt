package com.example.receipe_app2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeActivity : AppCompatActivity() {

    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var btnSearch: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        recyclerView = findViewById(R.id.recyclerView)
        etSearch = findViewById(R.id.et_search)
        btnSearch = findViewById(R.id.btn_search)

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

        btnSearch.setOnClickListener {
            val query = etSearch.text.toString()
            fetchRecipes(query)
        }
    }


    private fun fetchRecipes(query: String) {
        val apiKey = "121780846bdd4ceab936"
        val call = ApiClient.recipeApi.getRecipes(apiKey, query)

        call.enqueue(object : Callback<api_response> {
            override fun onResponse(call: Call<api_response>, response: Response<api_response>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        recipeAdapter.setRecipes(responseBody.cOOKRCP01.row.filter { it.rCPNM.contains(query) })
                    } else {
                        Log.e("RecipeActivity", "API 응답 바디가 null입니다.")
                    }
                } else {
                    Log.e("RecipeActivity", "API 호출 실패: ${response.message()}")
                    Log.d("RecipeActivity", "API 응답: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<api_response>, t: Throwable) {
                Log.e("RecipeActivity", "API 호출 실패", t)
                // 추가적인 로그를 통해 오류 원인 확인
                t.printStackTrace()
            }
        })
    }
}
