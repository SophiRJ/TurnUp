package com.example.turnup.ui

import android.os.Bundle
import com.example.turnup.R
import com.example.turnup.databinding.ActivityEventosBinding

class EventosActivity : BaseActivity() {

    private lateinit var bindingEventos: ActivityEventosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflar SOLO el contenido de esta Activity en el content_frame del BaseActivity
        bindingEventos = ActivityEventosBinding.inflate(layoutInflater)
        val contentFrame = findViewById<android.widget.FrameLayout>(R.id.content_frame)
        contentFrame.removeAllViews()
        contentFrame.addView(bindingEventos.root)

        
    }
}
