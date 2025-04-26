package com.example.turnup.ui

import android.content.Intent
import android.os.Bundle
import com.example.turnup.R
import com.example.turnup.databinding.ActivityHomeBinding

class HomeActivity : BaseActivity() {

    private lateinit var bindingHome: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflar solo el contenido de esta Activity, no el layout base (eso ya lo hace BaseActivity)
        bindingHome = ActivityHomeBinding.inflate(layoutInflater)
        val contentFrame = findViewById<android.widget.FrameLayout>(R.id.content_frame)
        contentFrame.removeAllViews()
        contentFrame.addView(bindingHome.root)

        val email = intent.getStringExtra("email") ?: "No email"
        val provider = intent.getStringExtra("provider") ?: "No provider"

        bindingHome.tvWelcome.text = "¡Bienvenido!\n$email"
        bindingHome.tvProvider.text = "Autenticado con: $provider"

        /*bindingHome.btnLogout.setOnClickListener {
            mAuth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }*/
    }
}

