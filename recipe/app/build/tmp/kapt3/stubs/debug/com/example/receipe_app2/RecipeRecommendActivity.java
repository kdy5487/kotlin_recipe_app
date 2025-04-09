package com.example.receipe_app2;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\t\u001a\u00020\nH\u0002J\u0012\u0010\u000b\u001a\u00020\n2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u0014R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000e"}, d2 = {"Lcom/example/receipe_app2/RecipeRecommendActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "btnRecommend", "Landroid/widget/Button;", "recipeAdapter", "Lcom/example/receipe_app2/RecipeAdapter;", "recyclerView", "Landroidx/recyclerview/widget/RecyclerView;", "fetchRecommendedRecipes", "", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "app_debug"})
public final class RecipeRecommendActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.example.receipe_app2.RecipeAdapter recipeAdapter;
    private androidx.recyclerview.widget.RecyclerView recyclerView;
    private android.widget.Button btnRecommend;
    
    public RecipeRecommendActivity() {
        super();
    }
    
    @java.lang.Override
    protected void onCreate(@org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    private final void fetchRecommendedRecipes() {
    }
}