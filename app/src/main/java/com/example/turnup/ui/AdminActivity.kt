package com.example.turnup.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.turnup.databinding.ActivityAdminBinding

class AdminActivity : AppCompatActivity(), OnClickListener{
    private lateinit var binding: ActivityAdminBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAdminBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        binding.btnCrearEvento.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            binding.btnCrearEvento.id->{
                val intent:Intent=Intent(applicationContext, NuevoEvento_Activity::class.java)
                startActivity(intent)
            }
        }
    }
}