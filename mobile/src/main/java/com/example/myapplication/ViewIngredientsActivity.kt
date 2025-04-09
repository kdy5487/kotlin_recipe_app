package com.example.myapplication

import IngredientAdapter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ViewIngredientsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: IngredientAdapter
    private val ingredientsList = mutableListOf<IngredientInformation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_ingredients)

        recyclerView = findViewById(R.id.recyclerViewIngredients)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = IngredientAdapter(ingredientsList) { ingredient, isChecked ->
            //
        }
        recyclerView.adapter = adapter

        fetchIngredients()
    }

    private fun fetchIngredients() {
        val db = FirebaseFirestore.getInstance()
        db.collection("ingredient")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val ingredient = document.toObject(IngredientInformation::class.java)
                    ingredientsList.add(ingredient)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error getting documents: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
