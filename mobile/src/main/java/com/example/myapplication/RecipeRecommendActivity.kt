package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.models.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeRecommendActivity : AppCompatActivity() {

    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnToggleViewMode: Button
    private lateinit var layoutRecipeRecommend: LinearLayout
    private lateinit var homeButton: LinearLayout
    private lateinit var myPageButton: LinearLayout
    private var isListView = true // 초기값: 리스트 보기 모드

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_recommend)

        // View 초기화
        recyclerView = findViewById(R.id.recyclerViewRecipes)
        btnToggleViewMode = findViewById(R.id.btnToggleViewMode)
        layoutRecipeRecommend = findViewById(R.id.layout_recipe_recommend)
        homeButton = findViewById(R.id.button_home)
        myPageButton = findViewById(R.id.button_my_page)

        // RecyclerView 설정
        setupRecyclerView()

        // 하단 네비게이션 버튼 이벤트 처리
        setupBottomNavigation()

        // 보기 모드 전환 버튼 클릭 이벤트
        btnToggleViewMode.setOnClickListener {
            toggleViewMode()
        }

        // 추천 버튼 클릭 이벤트
        layoutRecipeRecommend.setOnClickListener {
            fetchRecommendedRecipes()
        }
    }

    private fun setupRecyclerView() {
        recipeAdapter = RecipeAdapter(
            onRecipeClick = { recipe ->
                // 레시피 클릭 시 상세 화면으로 이동
                val intent = Intent(this, RecipeDetailActivity::class.java).apply {
                    putExtra("recipe_name", recipe.title)
                    putExtra("recipe_image", recipe.imageUrl)
                    putExtra("recipe_ingredients", recipe.ingredients.toTypedArray())
                    putExtra("recipe_instructions", recipe.steps.toTypedArray())
                    putExtra("recipe_instruction_images", recipe.stepImages.toTypedArray())
                    putExtra("recipe_source", recipe.source)
                }
                startActivity(intent)
            },
            onFavoriteClick = { recipe ->
                toggleFavorite(recipe)
            }
        )
        recyclerView.adapter = recipeAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupBottomNavigation() {
        // 홈 버튼 클릭 이벤트
        homeButton.setOnClickListener {
            val intent = Intent(this, MainHomeActivity::class.java)
            startActivity(intent)
        }

        // 마이페이지 버튼 클릭 이벤트
        myPageButton.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }
    }

    private fun toggleViewMode() {
        if (isListView) {
            recyclerView.layoutManager = GridLayoutManager(this, 2) // 그리드 보기
            btnToggleViewMode.text = "리스트 보기"
        } else {
            recyclerView.layoutManager = LinearLayoutManager(this) // 리스트 보기
            btnToggleViewMode.text = "그리드 보기"
        }
        isListView = !isListView
    }

    private fun fetchRecommendedRecipes() {
        val apiKey = "121780846bdd4ceab936" // 유효한 API 키
        val call = ApiClient.recipeApi.getRecipes(apiKey, "")

        call.enqueue(object : Callback<api_response> {
            override fun onResponse(call: Call<api_response>, response: Response<api_response>) {
                if (response.isSuccessful) {
                    val apiRecipes = response.body()?.cOOKRCP01?.row?.map { row ->
                        convertRowToPost(row)
                    } ?: emptyList()

                    recipeAdapter.setRecipes(apiRecipes)
                } else {
                    // API 호출 실패 처리
                }
            }

            override fun onFailure(call: Call<api_response>, t: Throwable) {
                // API 호출 실패 처리
            }
        })
    }

    private fun toggleFavorite(recipe: Post) {
        // 즐겨찾기 상태를 Firestore에 저장/삭제
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val favoritesRef = FirebaseFirestore.getInstance()
            .collection("users").document(userId).collection("favorites")

        if (recipe.isFavorite) {
            favoritesRef.document(recipe.id).set(recipe)
        } else {
            favoritesRef.document(recipe.id).delete()
        }
    }

    private fun convertRowToPost(row: Row): Post {
        return Post(
            id = row.rCPSEQ,
            title = row.rCPNM,
            imageUrl = row.aTTFILENOMAIN ?: row.aTTFILENOMK,
            ingredients = row.rCPPARTSDTLS?.split(", ")?.filter { it.isNotBlank() } ?: emptyList(),
            steps = row.getAllInstructions(),
            stepImages = row.getAllImages().filterNotNull().filter { it.isNotEmpty() },
            source = "api"
        )
    }
}
