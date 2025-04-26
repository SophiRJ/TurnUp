package com.example.turnup.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.turnup.adapters.DisponibilidadAdapter
import com.example.turnup.databinding.ActivityDisponibilidadBinding
import com.example.turnup.models.Evento
import com.example.turnup.models.Turno
import java.util.*

class DisponibilidadActivity : BaseActivity() {

    private lateinit var binding: ActivityDisponibilidadBinding
    private lateinit var adapter: DisponibilidadAdapter
    private lateinit var evento: Evento

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisponibilidadBinding.inflate(layoutInflater)
        val contentFrame = findViewById<android.widget.FrameLayout>(com.example.turnup.R.id.content_frame)
        contentFrame.removeAllViews()
        contentFrame.addView(binding.root)

        // obtener evento
        evento = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("evento", Evento::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("evento") as Evento
        }

        //todo :no funciona revisar modificar

        // configurar RecyclerView
        adapter = DisponibilidadAdapter(
            this,
            generarDiasConTurnos(evento.fechaInicio, evento.fechaFin, evento.turnos),
            onSeleccionChange = { seleccion ->
                println("Selección actual: $seleccion")
            }
        )
        binding.recyclerDiasTurnos.layoutManager = LinearLayoutManager(this)
        binding.recyclerDiasTurnos.adapter = adapter

        binding.btnGuardarDisponibilidad.setOnClickListener {
            val seleccionados = adapter.getSeleccion()

            val userId = mAuth.currentUser?.uid
            //val eventoId = intent.getStringExtra("eventoId")

            if (evento.id.isNotEmpty() && userId != null) {
                val disponibilidadRef = db
                    .collection("eventos")
                    .document(evento.id) // revisar estooo
                    .collection("disponibilidades")
                    .document(userId)

                disponibilidadRef.set(seleccionados)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Disponibilidad guardada", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            } else {
                Log.e("DEBUG_DISPO", "Error: evento.id o userId nulos o vacíos - evento.id = ${evento.id}, userId = $userId")
                Toast.makeText(this, "Error: usuario o evento inválido", Toast.LENGTH_SHORT).show()
            }

        }

        // Botón volver
        binding.btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun generarDiasConTurnos(fechaInicio: Long, fechaFin: Long, turnos: List<Turno>): Map<Long, List<String>> {
        val mapa = mutableMapOf<Long, List<String>>()
        val calendario = Calendar.getInstance()
        calendario.timeInMillis = fechaInicio

        // convertir los turnos a formato de texto
        val turnosTexto = turnos.map { "${it.horaInicio}-${it.horaFin}" }

        while (calendario.timeInMillis <= fechaFin) {
            // usamos el timestamp (Long) como clave
            mapa[calendario.timeInMillis] = turnosTexto
            calendario.add(Calendar.DAY_OF_MONTH, 1)
        }

        return mapa
    }
}
