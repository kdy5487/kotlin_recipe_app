package com.example.receipe_app2

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RecipeApi {
    @GET("{apiKey}/COOKRCP01/json/1/1000")
    fun getRecipes(
        @Path("apiKey") apiKey: String,
        @Query("RCP_NM") recipeName: String
    ): Call<api_response>
}
