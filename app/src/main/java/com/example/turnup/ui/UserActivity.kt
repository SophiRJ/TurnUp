package com.example.turnup.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.turnup.databinding.ActivityUserBinding

class UserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCalendar()
    }

    private fun setupCalendar() {

    }
}
