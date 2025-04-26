package com.example.turnup.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.turnup.R
import com.example.turnup.adapters.AdminEventoAdapter
import com.example.turnup.databinding.ActivityAdminBinding
import com.example.turnup.models.Evento

class AdminActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Llama al onCreate de BaseActivity primero

        // Inflar el layout específico de AdminActivity
        binding = ActivityAdminBinding.inflate(layoutInflater)

        // Configurar el contenido dentro del layout base
        val contentFrame = findViewById<android.widget.FrameLayout>(R.id.content_frame)
        contentFrame.removeAllViews()
        contentFrame.addView(binding.root)

        binding.btnCrearEvento.setOnClickListener(this)
        targetAdmin()
        cargarMisEventos()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btnCrearEvento -> {
                val intent = Intent(this, NuevoEventoActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun targetAdmin() {
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            db.collection("Usuarios").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val sexo = document.getString("sexo")
                        val nombre = document.getString("nombre")
                        val apellido = document.getString("apellido")
                        val ocupacion = document.getString("ocupacion")
                        val institucion = document.getString("nombreInstitucion")

                        when (sexo) {
                            "Masculino" -> binding.imageAvatarAdmin.setImageResource(R.drawable.av_boy1)
                            "Femenino" -> binding.imageAvatarAdmin.setImageResource(R.drawable.av_girl2)
                        }

                        // Usar recursos con placeholders en lugar de concatenación
                        binding.textNombreAdmin.text = getString(R.string.nombre_completo, nombre, apellido)
                        binding.textInstitucion.text = institucion ?: ""
                        binding.textProfesion.text = ocupacion ?: ""
                    } else {
                        cerrarSesionYVolver()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("AdminActivity", "Error al obtener datos: ", exception)
                    cerrarSesionYVolver()
                }
        } else {
            cerrarSesionYVolver()
        }
    }

    private fun cerrarSesionYVolver() {
        mAuth.signOut()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
    private fun cargarMisEventos() {
        val uid = mAuth.currentUser?.uid ?: return

        db.collection("eventos")
            .whereEqualTo("autor", uid)
            .get()
            .addOnSuccessListener { documentos ->
                val eventos = documentos.mapNotNull { doc ->
                    try {
                        val evento = doc.toObject(Evento::class.java)
                        evento?.apply { id = doc.id }
                    } catch (e: Exception) {
                        Log.e("AdminActivity", "Error al convertir evento", e)
                        null
                    }
                }

                if (eventos.isEmpty()) {
                    Toast.makeText(this, "Aún no has creado eventos", Toast.LENGTH_SHORT).show()
                }

                val adapter = AdminEventoAdapter(
                    eventos,
                    onEditar = { evento ->
                        val intent = Intent(this, EditarEventoActivity::class.java)
                        intent.putExtra("evento", evento)
                        startActivity(intent)
                    },
                    onEliminar = { evento ->
                        db.collection("eventos").document(evento.id).delete()
                            .addOnSuccessListener {
                                Toast.makeText(this, "Evento eliminado", Toast.LENGTH_SHORT).show()
                                cargarMisEventos()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
                            }
                    },
                    onDetalles = { evento ->
                        val intent = Intent(this, EventoActivity::class.java)
                        intent.putExtra("evento", evento)
                        startActivity(intent)
                        Log.e("DEBUG_DISPO", "Error: evento.id o userId nulos o vacíos - evento.id = ${evento.id}, userId = ")
                    }
                )


                binding.recyclerMisEventos.layoutManager = LinearLayoutManager(this)
                binding.recyclerMisEventos.adapter = adapter

            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar eventos", Toast.LENGTH_SHORT).show()
            }
    }
}