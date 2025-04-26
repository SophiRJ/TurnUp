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

class EventoAdapter(
    private val listaEventos: List<Evento>,
    private val onVerDetallesClick: (Evento) -> Unit
) : RecyclerView.Adapter<EventoAdapter.EventoViewHolder>() {

    inner class EventoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgTipo = itemView.findViewById<ImageView>(R.id.imgTipoEvento)
        val tvNombre = itemView.findViewById<TextView>(R.id.tvNombreEvento)
        val tvUbicacion = itemView.findViewById<TextView>(R.id.tvUbicacionEvento)
        val tvFechas = itemView.findViewById<TextView>(R.id.tvFechasEvento)
        val btnDetalles = itemView.findViewById<Button>(R.id.btnVerDetalles)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_evento, parent, false)
        return EventoViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventoViewHolder, position: Int) {
        val evento = listaEventos[position]

        // iexto
        holder.tvNombre.text = evento.nombre
        holder.tvUbicacion.text = evento.ubicaciones.joinToString(", ")

        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaInicio = formato.format(Date(evento.fechaInicio))
        val fechaFin = formato.format(Date(evento.fechaFin))
        holder.tvFechas.text = "$fechaInicio - $fechaFin"

        // imagen según tipo de evento
        val imagenId = when (evento.tipo) {
            "Salud" -> R.drawable.evento_salud
            "Concierto" -> R.drawable.evento_concierto
            "Desastres" -> R.drawable.evento_desastres
            "Obras Sociales" -> R.drawable.evento_social
            else -> R.drawable.evento_generico
        }
        holder.imgTipo.setImageResource(imagenId)

        holder.btnDetalles.setOnClickListener {
            onVerDetallesClick(evento)
        }
    }
    override fun getItemCount(): Int = listaEventos.size
}
