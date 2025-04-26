package com.example.turnup.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.turnup.R
import com.example.turnup.adapters.TurnoAdapter
import com.example.turnup.databinding.ActivityEventoBinding
import com.example.turnup.models.Evento
import java.text.SimpleDateFormat
import java.util.*

class EventoActivity : BaseActivity(),OnClickListener {

    private lateinit var binding: ActivityEventoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEventoBinding.inflate(layoutInflater)
        val contentFrame = findViewById<android.widget.FrameLayout>(R.id.content_frame)
        contentFrame.removeAllViews()
        contentFrame.addView(binding.root)

        val evento = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("evento", Evento::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("evento") as? Evento
        }

        if (evento != null) {
            cargarDetallesEvento(evento)
        } else {
            Toast.makeText(this, "No se pudo cargar el evento", Toast.LENGTH_SHORT).show()
            finish()
        }

    }

    private fun cargarDetallesEvento(evento: Evento) {
        val imagenId = when (evento.tipo) {
            "Salud" -> R.drawable.evento_salud
            "Concierto" -> R.drawable.evento_concierto
            "Desastres" -> R.drawable.evento_desastres
            "Obras Sociales" -> R.drawable.evento_social
            else -> R.drawable.evento_generico
        }
        binding.imgEventoDetalle.setImageResource(imagenId)

        binding.tvNombreEventoDetalle.text = evento.nombre
        binding.tvInstitucion.text = "Organiza: ${evento.institucion}"
        binding.tvUbicacionesDetalle.text = evento.ubicaciones.joinToString(", ")

        // Fechas
        val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val inicio = formatoFecha.format(Date(evento.fechaInicio))
        val fin = formatoFecha.format(Date(evento.fechaFin))
        binding.tvFechasDetalle.text = "$inicio - $fin"

        binding.tvProfesionesDetalle.text = evento.profesiones.joinToString(", ")
        binding.tvHabilidadesDetalle.text = evento.habilidades.joinToString(", ")
        binding.tvTareasDetalle.text = evento.tareas.joinToString("\n• ", prefix = "• ")

        // Voluntarios requeridos
        binding.tvVoluntariosRequeridos.text = "Plazas disponibles: ${evento.voluntariosRequeridos}"

        // Turnos
        val adapterTurnos = TurnoAdapter(
            turnos = evento.turnos.toMutableList(),
            onEditar = { _, _ -> },
            onEliminar = { },
            soloLectura = true
        )
        binding.recyclerTurnos.layoutManager = LinearLayoutManager(this)
        binding.recyclerTurnos.adapter = adapterTurnos

        // ocultamos botones si el usuario es el autor del evento
        val usuarioActual = mAuth.currentUser
        if (usuarioActual != null && usuarioActual.uid == evento.autor) {
            binding.btnDisponibilidad.visibility = View.GONE
            binding.btnSolicitarColaborar.visibility = View.GONE
        }
        Log.e("DEBUG_DISPO", "Error: evento.id o userId nulos o vacíos - evento.id = ${evento.id}, userId = ")
        binding.btnDisponibilidad.setOnClickListener(this)
        // bton para solicitar colaborar -- simulacion
        binding.btnSolicitarColaborar.setOnClickListener {
            Toast.makeText(this, "Solicitud enviada (simulado)", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.btnDisponibilidad.id -> {
                val evento = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getSerializableExtra("evento", Evento::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getSerializableExtra("evento") as? Evento
                }

                if (evento != null) {
                    irADisponibilidad(evento)
                } else {
                    Toast.makeText(this, "Error al cargar el evento", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun irADisponibilidad(evento: Evento) {
        val intent = Intent(this, DisponibilidadActivity::class.java).apply {
            putExtra("evento", evento)
            putExtra("eventoId", evento.id) // Agregamos esto explícitamente
        }
        startActivity(intent)
    }

}
