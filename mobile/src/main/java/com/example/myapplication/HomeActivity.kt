package com.example.myapplication


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

@androidx.camera.core.ExperimentalGetImage
class HomeActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Firebase 초기화
        FirebaseApp.initializeApp(this)

        // UI 요소 초기화
        val buttonAddIngredient = findViewById<Button>(R.id.add_ingredient_button)
        val buttonManageIngredient = findViewById<Button>(R.id.manage_ingredient)
        val buttonCamera = findViewById<Button>(R.id.camera_button)
        calendarView = findViewById<CalendarView>(R.id.calendarView)

        // 재료 추가 버튼 클릭 시 AddIngredientActivity로 이동
        buttonAddIngredient.setOnClickListener {
            val intent = Intent(this@HomeActivity, AddIngredientActivity::class.java)
            startActivity(intent)
        }

        // 재료 관리 버튼 클릭 시 ManageIngredientActivity로 이동
        buttonManageIngredient.setOnClickListener {
            val intent = Intent(this@HomeActivity, Manage_ingredient::class.java)
            startActivity(intent)
        }

        // 카메라 버튼 클릭 시 CameraActivity로 이동
        buttonCamera.setOnClickListener {
            val intent = Intent(this@HomeActivity, CameraActivity::class.java)
            startActivity(intent)
        }
    }
}

