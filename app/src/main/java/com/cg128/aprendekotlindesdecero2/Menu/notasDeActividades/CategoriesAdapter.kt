
package com.cg128.aprendekotlindesdecero2.Menu.notasDeActividades

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.cg128.aprendekotlindesdecero2.R
import androidx.appcompat.app.AlertDialog

class CategoriesAdapter(private val context: Context,
                        private val categories:MutableList<TaskCategory>,
                        private val onItemSelected:(Int) -> Unit,
                        private val onDeleteCategory: (Int) -> Unit):
    RecyclerView.Adapter<CategoriesViewHolde>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolde {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task_category, parent, false)
        return CategoriesViewHolde(view)
    }

    override fun onBindViewHolder(holder: CategoriesViewHolde, position: Int) {
        val category = categories[position]

        holder.render(category, onItemSelected, onDeleteCategory)

        if (category is TaskCategory.Other) {
            holder.setSpecialStyle()
        } else {
            holder.resetStyle()
        }
    }

    override fun getItemCount() = categories.size

    private fun deleteCategory(position: Int) {
        val categoryToDelete = categories[position]

        if (categoryToDelete is TaskCategory.Other) {
            Toast.makeText(context, "No se puede eliminar la categoría 'Otro'", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(context)
            .setTitle("Eliminar categoría")
            .setMessage("¿Estás seguro de que deseas eliminar esta categoría y todas sus tareas?")
            .setPositiveButton("Eliminar") { _, _ ->
                categories.removeAt(position)
                notifyItemRemoved(position)
                onDeleteCategory(position) // Llama al callback para que la Activity también borre tareas
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }



}