package com.example.receipe_app2

import UserPreferences
import kotlin.math.sqrt

object RecommendationUtils {

    fun cosineSimilarity(vec1: Map<String, Int>, vec2: Map<String, Int>): Double {
        val dotProduct = vec1.keys.intersect(vec2.keys).sumOf { vec1[it]!! * vec2[it]!! }
        val magnitude1 = sqrt(vec1.values.sumOf { it * it }.toDouble())
        val magnitude2 = sqrt(vec2.values.sumOf { it * it }.toDouble())
        return dotProduct / (magnitude1 * magnitude2)
    }

    fun kMeansClustering(preferences: List<UserPreferences>, k: Int): List<List<UserPreferences>> {
        // 간단한 K-means 클러스터링 알고리즘 구현 (여기서는 무작위 초기화 사용)
        val clusters = MutableList(k) { mutableListOf<UserPreferences>() }
        preferences.forEach { preference ->
            val randomCluster = clusters.random()
            randomCluster.add(preference)
        }
        return clusters
    }

    fun recommendRecipes(preferences: List<UserPreferences>, recipes: List<Row>): List<Row> {
        val clusters = kMeansClustering(preferences, 3)
        val userCluster = clusters.maxByOrNull { it.size } ?: return recipes.shuffled().take(10)

        val userVector = userCluster.flatMap { it.ingredients.split(",") }.groupingBy { it }.eachCount()
        return recipes.sortedByDescending { recipe ->
            val recipeVector = recipe.rCPPARTSDTLS.split(",").groupingBy { it }.eachCount()
            cosineSimilarity(userVector, recipeVector)
        }.take(10)
    }
}
