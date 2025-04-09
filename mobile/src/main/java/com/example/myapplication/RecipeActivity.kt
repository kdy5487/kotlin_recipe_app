package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
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
import java.net.URLEncoder

class RecipeActivity : AppCompatActivity() {

    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var btnSearch: ImageView
    private lateinit var btnToggleViewMode: Button
    private lateinit var rgSearchOptions: RadioGroup
    private lateinit var rbSearchTitle: RadioButton
    private lateinit var rbSearchIngredients: RadioButton
    private lateinit var homeButton: LinearLayout
    private lateinit var myPageButton: LinearLayout
    private var isListView = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        // View 초기화
        recyclerView = findViewById(R.id.recyclerView)
        etSearch = findViewById(R.id.et_search)
        btnSearch = findViewById(R.id.btn_search)
        btnToggleViewMode = findViewById(R.id.btn_toggle_view_mode)
        rgSearchOptions = findViewById(R.id.rg_search_options)
        rbSearchTitle = findViewById(R.id.rb_search_title)
        rbSearchIngredients = findViewById(R.id.rb_search_ingredients)
        homeButton = findViewById(R.id.button_home)
        myPageButton = findViewById(R.id.button_my_page)

        // RecyclerView 설정
        setupRecyclerView()

        // 하단 네비게이션 설정
        setupBottomNavigation()

        // 검색 버튼 클릭 이벤트
        btnSearch.setOnClickListener {
            val query = etSearch.text.toString()
            val isTitleSearch = rbSearchTitle.isChecked
            if (query.isNotBlank()) {
                searchRecipes(query, isTitleSearch)
            } else {
                recipeAdapter.setRecipes(emptyList())
            }
        }

        // 보기 모드 전환 버튼 클릭 이벤트
        btnToggleViewMode.setOnClickListener {
            toggleViewMode()
        }
    }

    private fun setupRecyclerView() {
        recipeAdapter = RecipeAdapter(
            onRecipeClick = { recipe ->
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
            recyclerView.layoutManager = GridLayoutManager(this, 2)
            btnToggleViewMode.text = "리스트 보기"
        } else {
            recyclerView.layoutManager = LinearLayoutManager(this)
            btnToggleViewMode.text = "그리드 보기"
        }
        isListView = !isListView
    }

    private fun searchRecipes(query: String, isTitleSearch: Boolean) {
        fetchCommunityRecipes(query, isTitleSearch) { communityRecipes ->
            fetchApiRecipes(query, isTitleSearch) { apiRecipes ->
                val allRecipes = communityRecipes + apiRecipes
                recipeAdapter.setRecipes(allRecipes)
            }
        }
    }

    private fun fetchCommunityRecipes(query: String, isTitleSearch: Boolean, callback: (List<Post>) -> Unit) {
        FirebaseFirestore.getInstance().collection("posts")
            .get()
            .addOnSuccessListener { result ->
                val recipes = result.toObjects(Post::class.java).filter { post ->
                    if (isTitleSearch) {
                        post.title.contains(query, ignoreCase = true)
                    } else {
                        post.ingredients.any { it.contains(query, ignoreCase = true) }
                    }
                }
                callback(recipes)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    private fun fetchApiRecipes(query: String, isTitleSearch: Boolean, callback: (List<Post>) -> Unit) {
        val apiKey = "121780846bdd4ceab936"
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val call = ApiClient.recipeApi.getRecipes(apiKey, encodedQuery)

        call.enqueue(object : Callback<api_response> {
            override fun onResponse(call: Call<api_response>, response: Response<api_response>) {
                if (response.isSuccessful && response.body()?.cOOKRCP01?.row != null) {
                    val apiRecipes = response.body()?.cOOKRCP01?.row?.filter { row ->
                        if (isTitleSearch) {
                            row.rCPNM.contains(query, ignoreCase = true)
                        } else {
                            row.rCPPARTSDTLS?.contains(query, ignoreCase = true) ?: false
                        }
                    }?.map { row ->
                        Post(
                            id = row.rCPSEQ,
                            title = row.rCPNM,
                            imageUrl = row.aTTFILENOMAIN ?: row.aTTFILENOMK,
                            ingredients = row.rCPPARTSDTLS?.split(", ") ?: emptyList(),
                            steps = row.getAllInstructions(),
                            stepImages = row.getAllImages(),
                            source = "API"
                        )
                    } ?: emptyList()
                    callback(apiRecipes)
                } else {
                    callback(emptyList())
                }
            }

            override fun onFailure(call: Call<api_response>, t: Throwable) {
                callback(emptyList())
            }
        })
    }

    private fun toggleFavorite(recipe: Post) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val favoritesRef = FirebaseFirestore.getInstance()
            .collection("users").document(userId).collection("favorites")

        if (recipe.isFavorite) {
            favoritesRef.document(recipe.id).set(recipe)
        } else {
            favoritesRef.document(recipe.id).delete()
        }
    }
}
