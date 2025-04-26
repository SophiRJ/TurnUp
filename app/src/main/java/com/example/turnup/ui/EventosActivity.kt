package com.example.turnup.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.turnup.R
import com.example.turnup.adapters.EventoAdapter
import com.example.turnup.databinding.ActivityEventosBinding
import com.example.turnup.models.Evento

class EventosActivity : BaseActivity() {

    private lateinit var bindingEventos: ActivityEventosBinding
    private val listaEventos = mutableListOf<Evento>()
    private lateinit var adapter: EventoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflar el layout específico dentro del content_frame del BaseActivity
        bindingEventos = ActivityEventosBinding.inflate(layoutInflater)
        val contentFrame = findViewById<android.widget.FrameLayout>(R.id.content_frame)
        contentFrame.removeAllViews()
        contentFrame.addView(bindingEventos.root)

        // Inicializar RecyclerView
        adapter = EventoAdapter(listaEventos) { evento ->
            val intent = Intent(this, EventoActivity::class.java)
            intent.putExtra("evento", evento)
            startActivity(intent)
        }

        bindingEventos.recyclerEventos.layoutManager = LinearLayoutManager(this)
        bindingEventos.recyclerEventos.adapter = adapter

        // Cargar eventos
        cargarEventos()

    }

    private fun cargarEventos() {
        db.collection("eventos")
            .get()
            .addOnSuccessListener { result ->
                listaEventos.clear()
                for (document in result) {
                    val evento = document.toObject(Evento::class.java)
                    listaEventos.add(evento)
                    Log.e("DEBUG_DISPO", "Error: evento.id o userId nulos o vacíos - evento.id = ${evento.id}, userId = ${evento.nombre}")
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar eventos", Toast.LENGTH_SHORT).show()
            }
    }
}
