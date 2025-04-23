package com.example.turnup.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.turnup.databinding.ItemShiftBinding
import com.example.turnup.models.Turno

class TurnoAdapter(
    private val turnos: MutableList<Turno>,
    private val onEditar: (Turno, Int) -> Unit,
    private val onEliminar: (Int) -> Unit
) : RecyclerView.Adapter<TurnoAdapter.TurnoViewHolder>() {

    inner class TurnoViewHolder(val binding: ItemShiftBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TurnoViewHolder {
        val binding = ItemShiftBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TurnoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TurnoViewHolder, position: Int) {
        val turno = turnos[position]
        holder.binding.apply {
            tvShiftName.text = turno.nombre
            tvShiftTime.text = "${turno.horaInicio} - ${turno.horaFin}"
            tvVolunteersCount.text = "Voluntarios: ${turno.maxVoluntarios}"

            btnEditShift.setOnClickListener { onEditar(turno, position) }
            btnDeleteShift.setOnClickListener { onEliminar(position) }
        }
    }

    override fun getItemCount() = turnos.size
}
