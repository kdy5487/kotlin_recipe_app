package com.example.receipe_app2;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u0014B\u0019\u0012\u0012\u0010\u0003\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u0004\u00a2\u0006\u0002\u0010\u0007J\b\u0010\n\u001a\u00020\u000bH\u0016J\u0018\u0010\f\u001a\u00020\u00062\u0006\u0010\r\u001a\u00020\u00022\u0006\u0010\u000e\u001a\u00020\u000bH\u0016J\u0018\u0010\u000f\u001a\u00020\u00022\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u000bH\u0016J\u0014\u0010\u0013\u001a\u00020\u00062\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00050\tR\u001a\u0010\u0003\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00050\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0015"}, d2 = {"Lcom/example/receipe_app2/RecipeAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/example/receipe_app2/RecipeAdapter$RecipeViewHolder;", "onRecipeClick", "Lkotlin/Function1;", "Lcom/example/receipe_app2/Row;", "", "(Lkotlin/jvm/functions/Function1;)V", "recipes", "", "getItemCount", "", "onBindViewHolder", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "setRecipes", "RecipeViewHolder", "app_debug"})
public final class RecipeAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.example.receipe_app2.RecipeAdapter.RecipeViewHolder> {
    @org.jetbrains.annotations.NotNull
    private final kotlin.jvm.functions.Function1<com.example.receipe_app2.Row, kotlin.Unit> onRecipeClick = null;
    @org.jetbrains.annotations.NotNull
    private java.util.List<com.example.receipe_app2.Row> recipes;
    
    public RecipeAdapter(@org.jetbrains.annotations.NotNull
    kotlin.jvm.functions.Function1<? super com.example.receipe_app2.Row, kotlin.Unit> onRecipeClick) {
        super();
    }
    
    public final void setRecipes(@org.jetbrains.annotations.NotNull
    java.util.List<com.example.receipe_app2.Row> recipes) {
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public com.example.receipe_app2.RecipeAdapter.RecipeViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull
    com.example.receipe_app2.RecipeAdapter.RecipeViewHolder holder, int position) {
    }
    
    @java.lang.Override
    public int getItemCount() {
        return 0;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B!\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005\u00a2\u0006\u0002\u0010\bJ\u000e\u0010\r\u001a\u00020\u00072\u0006\u0010\u000e\u001a\u00020\u0006R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2 = {"Lcom/example/receipe_app2/RecipeAdapter$RecipeViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "itemView", "Landroid/view/View;", "onRecipeClick", "Lkotlin/Function1;", "Lcom/example/receipe_app2/Row;", "", "(Landroid/view/View;Lkotlin/jvm/functions/Function1;)V", "imageView", "Landroid/widget/ImageView;", "titleTextView", "Landroid/widget/TextView;", "bind", "recipe", "app_debug"})
    public static final class RecipeViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        @org.jetbrains.annotations.NotNull
        private final kotlin.jvm.functions.Function1<com.example.receipe_app2.Row, kotlin.Unit> onRecipeClick = null;
        @org.jetbrains.annotations.NotNull
        private final android.widget.TextView titleTextView = null;
        @org.jetbrains.annotations.NotNull
        private final android.widget.ImageView imageView = null;
        
        public RecipeViewHolder(@org.jetbrains.annotations.NotNull
        android.view.View itemView, @org.jetbrains.annotations.NotNull
        kotlin.jvm.functions.Function1<? super com.example.receipe_app2.Row, kotlin.Unit> onRecipeClick) {
            super(null);
        }
        
        public final void bind(@org.jetbrains.annotations.NotNull
        com.example.receipe_app2.Row recipe) {
        }
    }
}