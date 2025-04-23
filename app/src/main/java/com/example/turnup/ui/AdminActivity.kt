package com.example.turnup.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.turnup.R
import com.example.turnup.databinding.ActivityAdminBinding

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

    // Opcional: Si necesitas manejar items del menú de manera diferente en AdminActivity
    /*
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_item_eventos -> {
                // Ya estamos en AdminActivity, no necesitamos redirigir
                binding.drawerLayout.closeDrawers()
                return true
            }
            else -> return super.onNavigationItemSelected(item)
        }
    }*/
}