package com.example.turnup.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.turnup.databinding.ItemDiaTurnoBinding
import java.text.SimpleDateFormat
import java.util.*

class DisponibilidadAdapter(
    private val context: Context,
    private val disponibilidad: Map<Long, List<String>>,
    private val onSeleccionChange: (Map<Long, List<String>>) -> Unit
) : RecyclerView.Adapter<DisponibilidadAdapter.ViewHolder>() {

    //guarda lo que el usuario selecciona
    private val seleccionUsuario = mutableMapOf<Long, MutableList<String>>()

    inner class ViewHolder(val binding: ItemDiaTurnoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDiaTurnoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = disponibilidad.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fechaMillis = disponibilidad.keys.toList()[position]
        val turnos = disponibilidad[fechaMillis] ?: listOf()

        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        holder.binding.tvFecha.text = formato.format(Date(fechaMillis))

        // limpiamos primero los turnos del layout
        holder.binding.layoutTurnos.removeAllViews()

        for (turno in turnos) {
            val checkBox = CheckBox(context).apply {
                text = turno
                isChecked = seleccionUsuario[fechaMillis]?.contains(turno) == true
                setOnCheckedChangeListener { _, isChecked ->
                    val seleccionados = seleccionUsuario.getOrPut(fechaMillis) { mutableListOf() }
                    if (isChecked) {
                        if (!seleccionados.contains(turno)) seleccionados.add(turno)
                    } else {
                        seleccionados.remove(turno)
                    }
                    onSeleccionChange(seleccionUsuario)
                }
            }
            holder.binding.layoutTurnos.addView(checkBox)
        }
    }

    fun getSeleccion(): Map<Long, List<String>> = seleccionUsuario
}
