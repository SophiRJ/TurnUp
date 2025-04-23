package com.example.turnup.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.turnup.databinding.ItemTareaBinding

class TareaAdapter(
    private val tareas: MutableList<String>,
    private val onEliminar: (Int) -> Unit
) : RecyclerView.Adapter<TareaAdapter.TareaViewHolder>() {

    inner class TareaViewHolder(val binding: ItemTareaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaViewHolder {
        val binding = ItemTareaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TareaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TareaViewHolder, position: Int) {
        val tarea = tareas[position]
        holder.binding.tvTarea.text = tarea
        holder.binding.btnEliminarTarea.setOnClickListener {
            onEliminar(position)
        }
    }

    override fun getItemCount() = tareas.size
}
