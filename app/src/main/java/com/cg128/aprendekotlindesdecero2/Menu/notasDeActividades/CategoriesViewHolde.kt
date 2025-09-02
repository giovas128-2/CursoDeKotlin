package com.cg128.aprendekotlindesdecero2.Menu.notasDeActividades

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cg128.aprendekotlindesdecero2.R


class CategoriesViewHolde(view: View): RecyclerView.ViewHolder(view) {
    private val tvCategoryName: TextView = view .findViewById(R.id.tvCategoryName)
    private val divider: View = view.findViewById(R.id.divider)
    private val viewContainer: CardView = view.findViewById(R.id.viewContainer)
    private val ivDeleteCategory: ImageView = view.findViewById(R.id.ivDeleteCategory)


    fun render(taskCategory: TaskCategory, onItemSelected: (Int) -> Unit, onDeleteCategory: (Int) -> Unit){

        val color = when {
            taskCategory.isSelected && taskCategory != TaskCategory.Other -> R.color.todo_background_selected_category
            else -> R.color.todo_background_card
        }

        if (taskCategory != TaskCategory.Other) {
            viewContainer.setCardBackgroundColor(ContextCompat.getColor(viewContainer.context, color))
        }



        itemView.setOnClickListener {  val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onItemSelected(position)
            } }

        when(taskCategory){
            TaskCategory.Business -> {
                tvCategoryName.text = "Negocios"
                divider.setBackgroundColor(
                    ContextCompat.getColor(divider.context, R.color.todo_business_category)
                )
                ivDeleteCategory.visibility = View.VISIBLE
            }
            TaskCategory.Other -> {
                tvCategoryName.text = "Otro"
                tvCategoryName.setTextColor(
                    ContextCompat.getColor(tvCategoryName.context, R.color.todo_other_category_highlight)
                )

                divider.setBackgroundColor(
                    ContextCompat.getColor(divider.context, R.color.todo_other_category)
                )
                ivDeleteCategory.visibility = View.GONE
            }
            TaskCategory.Personal -> {
                tvCategoryName.text = "Personal"
                divider.setBackgroundColor(
                    ContextCompat.getColor(divider.context, R.color.todo_personal_category)
                )
                ivDeleteCategory.visibility = View.VISIBLE
            }
            is TaskCategory.Custom -> {
                tvCategoryName.text = taskCategory.name
                divider.setBackgroundColor(
                    ContextCompat.getColor(divider.context, taskCategory.colorResId)
                )
                ivDeleteCategory.visibility = View.VISIBLE
            }
        }
        ivDeleteCategory.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onDeleteCategory(position)
            }
        }
    }
    fun setSpecialStyle() {
        itemView.setBackgroundResource(R.drawable.dotted_border_other) // O punteros
    }

    fun resetStyle() {
        itemView.setBackgroundResource(R.drawable.bg_category_normal) // Resetear si ya no es "Otro"
    }
}