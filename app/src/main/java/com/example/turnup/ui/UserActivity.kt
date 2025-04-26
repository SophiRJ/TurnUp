package com.example.turnup.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.example.turnup.R
import com.example.turnup.databinding.ActivityUserBinding

class UserActivity : BaseActivity() {
    private lateinit var binding: ActivityUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflar el layout específico de UserActivity
        binding = ActivityUserBinding.inflate(layoutInflater)

        // Configurar el contenido dentro del layout base
        val contentFrame = findViewById<android.widget.FrameLayout>(R.id.content_frame)
        contentFrame.removeAllViews()
        contentFrame.addView(binding.root)

        tarjetUser()
    }

    private fun tarjetUser() {
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            db.collection("Usuarios").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val sexo = document.getString("sexo")
                        val nombre = document.getString("nombre")
                        val apellido = document.getString("apellido")
                        val profesion = document.getString("ocupacion")

                        when (sexo) {
                            "Femenino" -> binding.avatarUsuario.setImageResource(R.drawable.av_girl3)
                            "Masculino" -> binding.avatarUsuario.setImageResource(R.drawable.av_boy4)
                            else -> {
                                cerrarSesionYVolver()
                                return@addOnSuccessListener
                            }
                        }
                        binding.tvNombreUsuario.text = getString(R.string.nombre_completo, nombre, apellido)
                        binding.tvAProfesion.text = profesion ?: ""
                    } else {
                        cerrarSesionYVolver()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("UserActivity", "Error al obtener datos: ", exception)
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
}

