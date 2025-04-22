package com.example.turnup.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.turnup.databinding.ActivityNuevoEventoBinding

class NuevoEvento_Activity : AppCompatActivity() {
    private lateinit var binding: ActivityNuevoEventoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityNuevoEventoBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

    }
}