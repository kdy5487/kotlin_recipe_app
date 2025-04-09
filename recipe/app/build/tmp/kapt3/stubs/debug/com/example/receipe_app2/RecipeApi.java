package com.example.receipe_app2;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\"\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\u0007\u001a\u00020\u0006H\'\u00a8\u0006\b"}, d2 = {"Lcom/example/receipe_app2/RecipeApi;", "", "getRecipes", "Lretrofit2/Call;", "Lcom/example/receipe_app2/api_response;", "apiKey", "", "recipeName", "app_debug"})
public abstract interface RecipeApi {
    
    @retrofit2.http.GET(value = "{apiKey}/COOKRCP01/json/1/1000")
    @org.jetbrains.annotations.NotNull
    public abstract retrofit2.Call<com.example.receipe_app2.api_response> getRecipes(@retrofit2.http.Path(value = "apiKey")
    @org.jetbrains.annotations.NotNull
    java.lang.String apiKey, @retrofit2.http.Query(value = "RCP_NM")
    @org.jetbrains.annotations.NotNull
    java.lang.String recipeName);
}