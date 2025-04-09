package com.example.receipe_app2;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J.\u0010\u0003\u001a\u00020\u00042\u0012\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\b0\u00062\u0012\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\b0\u0006J(\u0010\n\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\u000b2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\f0\u000b2\u0006\u0010\u000e\u001a\u00020\bJ(\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00100\u000b2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\f0\u000b2\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00100\u000b\u00a8\u0006\u0012"}, d2 = {"Lcom/example/receipe_app2/RecommendationUtils;", "", "()V", "cosineSimilarity", "", "vec1", "", "", "", "vec2", "kMeansClustering", "", "LUserPreferences;", "preferences", "k", "recommendRecipes", "Lcom/example/receipe_app2/Row;", "recipes", "app_debug"})
public final class RecommendationUtils {
    @org.jetbrains.annotations.NotNull
    public static final com.example.receipe_app2.RecommendationUtils INSTANCE = null;
    
    private RecommendationUtils() {
        super();
    }
    
    public final double cosineSimilarity(@org.jetbrains.annotations.NotNull
    java.util.Map<java.lang.String, java.lang.Integer> vec1, @org.jetbrains.annotations.NotNull
    java.util.Map<java.lang.String, java.lang.Integer> vec2) {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<java.util.List<UserPreferences>> kMeansClustering(@org.jetbrains.annotations.NotNull
    java.util.List<UserPreferences> preferences, int k) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.example.receipe_app2.Row> recommendRecipes(@org.jetbrains.annotations.NotNull
    java.util.List<UserPreferences> preferences, @org.jetbrains.annotations.NotNull
    java.util.List<com.example.receipe_app2.Row> recipes) {
        return null;
    }
}