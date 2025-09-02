package com.cg128.aprendekotlindesdecero2.Menu.notasDeActividades

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cg128.aprendekotlindesdecero2.R

class TasksAdapter (var tasks:List<Task>, private val onTaskSelected:(Task) -> Unit) : RecyclerView.Adapter<TasksViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notes_task, parent, false)
        return TasksViewHolder(view)
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        val task = tasks[position]
        holder.render(task)
        holder.itemView.setOnClickListener { onTaskSelected(task) }
    }

    override fun getItemCount() = tasks.size
}