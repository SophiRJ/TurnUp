package com.example.turnup.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.turnup.databinding.ItemUbicacionBinding

class UbicacionAdapter(
    private val ubicaciones: MutableList<String>,
    private val onEliminar: (Int) -> Unit
) : RecyclerView.Adapter<UbicacionAdapter.UbicacionViewHolder>() {

    inner class UbicacionViewHolder(val binding: ItemUbicacionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UbicacionViewHolder {
        val binding = ItemUbicacionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UbicacionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UbicacionViewHolder, position: Int) {
        val ubicacion = ubicaciones[position]
        holder.binding.apply {
            tvUbicacion.text = ubicacion
            btnEliminarUbicacion.setOnClickListener {
                onEliminar(position)
            }
        }
    }

    override fun getItemCount() = ubicaciones.size
}
