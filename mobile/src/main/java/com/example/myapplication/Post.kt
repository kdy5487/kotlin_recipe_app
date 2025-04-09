package com.example.myapplication.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val imageUrl: String? = null,
    val recommendations: Int = 0,
    var recommended: Boolean = false,
    val recipeTitle: String? = null,
    val ingredients: List<String> = emptyList(),
    val steps: List<String> = emptyList(),
    val stepImages: List<String> = emptyList(), // Step 이미지 저장
    val instructions: List<String> = emptyList(),
    var source: String = "community",  // 출처 (community 또는 api)
    val nutritionInfo: NutritionInfo? = null, // 영양 정보 추가
    var isFavorite: Boolean = false, // 즐겨찾기 상태
    var favoritedBy: MutableList<String> = mutableListOf(), // 변경 가능한 리스트로 수정
    val userId: String = ""
) : Parcelable
@Parcelize
data class NutritionInfo(
    val calories: String = "",
    val protein: String = "",
    val fat: String = "",
    val carbs: String = "",
    val sodium: String = ""
) : Parcelable