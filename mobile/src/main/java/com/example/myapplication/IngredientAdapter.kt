import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.IngredientInformation
import com.example.myapplication.R
import java.util.Locale
import java.text.SimpleDateFormat
import android.view.View
import android.widget.Button
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import com.example.myapplication.EditNotificationActivity
import com.example.myapplication.SetNotificationActivity


class IngredientAdapter @OptIn(ExperimentalGetImage::class) constructor
    (
    private var ingredients: List<IngredientInformation>,
    private val onItemChecked: (IngredientInformation, Boolean) -> Unit
) : RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder>() {

    // 선택된 아이템을 저장하는 Set으로 중복 방지
    private val selectedItems = mutableSetOf<IngredientInformation>()

    inner class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.textViewName)
        val dateTextView: TextView = itemView.findViewById(R.id.textViewDate)
        val imageView: ImageView = itemView.findViewById(R.id.imageViewIngredient)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
        val setNotificationButton: Button = itemView.findViewById(R.id.button_set_notification) // 알림 설정 버튼
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ingredient_item, parent, false)
        return IngredientViewHolder(view)
    }

    @OptIn(ExperimentalGetImage::class)
    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val ingredient = ingredients[position]
        holder.nameTextView.text = ingredient.name
        holder.dateTextView.text = "유통기한:  ${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(ingredient.date.toDate())}"

        // 이미지 로드 (Glide 사용)
        Glide.with(holder.itemView.context)
            .load(ingredient.imageUrl)
            .placeholder(R.drawable.placeholder_image)
            .into(holder.imageView)

        // 체크박스 상태 설정 및 리스너 - 리스너를 null로 설정 후 재설정
        holder.checkBox.setOnCheckedChangeListener(null) // 기존 리스너를 제거
        holder.checkBox.isChecked = selectedItems.contains(ingredient)
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedItems.add(ingredient)
            } else {
                selectedItems.remove(ingredient)
            }
            onItemChecked(ingredient, isChecked)
        }

        holder.setNotificationButton.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, EditNotificationActivity::class.java)
            intent.putExtra("ingredient_id", ingredient.id) // 이제 ingredient.id가 존재합니다
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = ingredients.size

    // 새로운 데이터를 설정하고 중복 없이 업데이트
    @OptIn(ExperimentalGetImage::class)
    fun setIngredients(ingredients: List<IngredientInformation>) {
        this.ingredients = ingredients
        notifyDataSetChanged()
    }

    // 선택된 아이템 반환하는 메소드
    @OptIn(ExperimentalGetImage::class)
    fun getSelectedItems(): List<IngredientInformation> {
        return selectedItems.toList()
    }
}
