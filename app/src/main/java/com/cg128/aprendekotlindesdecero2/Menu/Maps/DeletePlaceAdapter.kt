package com.cg128.aprendekotlindesdecero2.Menu.Maps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cg128.aprendekotlindesdecero2.R

class DeletePlaceAdapter(
    private val places: MutableList<Place>,
    private val onDeleteClick: (Place) -> Unit
) : RecyclerView.Adapter<DeletePlaceAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPlaceName: TextView = itemView.findViewById(R.id.tvPlaceName)
        val btnDeleteItem: Button = itemView.findViewById(R.id.btnDeleteItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_place_delete, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = places.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = places[position]
        holder.tvPlaceName.text = place.name
        holder.btnDeleteItem.setOnClickListener {
            onDeleteClick(place)
            // Eliminar del adaptador y refrescar la lista
            places.remove(place)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, places.size)
        }
    }
}
