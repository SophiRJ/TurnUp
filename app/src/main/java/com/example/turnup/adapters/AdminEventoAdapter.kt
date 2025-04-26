package com.example.turnup.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.turnup.R
import com.example.turnup.models.Evento
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminEventoAdapter(
    private val eventos: List<Evento>,
    private val onEditar: (Evento) -> Unit,
    private val onEliminar: (Evento) -> Unit,
    private val onDetalles: (Evento) -> Unit
) : RecyclerView.Adapter<AdminEventoAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgTipo = view.findViewById<ImageView>(R.id.imgTipoEvento)
        val tvNombre = view.findViewById<TextView>(R.id.tvNombreEvento)
        val tvUbicacion = view.findViewById<TextView>(R.id.tvUbicacionEvento)
        val tvFechas = view.findViewById<TextView>(R.id.tvFechasEvento)
        val btnDetalles = view.findViewById<Button>(R.id.btnVerDetalles)
        val btnEditar = view.findViewById<Button>(R.id.btnEditarEvento)
        val btnEliminar = view.findViewById<Button>(R.id.btnEliminarEvento)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_evento_admin, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val evento = eventos[position]

        holder.tvNombre.text = evento.nombre
        holder.tvUbicacion.text = evento.ubicaciones.joinToString(", ")

        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        holder.tvFechas.text = "${formato.format(Date(evento.fechaInicio))} - ${formato.format(Date(evento.fechaFin))}"

        val tipoIcono = when (evento.tipo) {
            "Salud" -> R.drawable.evento_salud
            "Concierto" -> R.drawable.evento_concierto
            "Desastres" -> R.drawable.evento_desastres
            "Obras Sociales" -> R.drawable.evento_social
            else -> R.drawable.evento_generico
        }
        holder.imgTipo.setImageResource(tipoIcono)

        holder.btnDetalles.setOnClickListener { onDetalles(evento) }
        holder.btnEditar.setOnClickListener { onEditar(evento) }
        holder.btnEliminar.setOnClickListener { onEliminar(evento) }
    }

    override fun getItemCount(): Int = eventos.size
}
