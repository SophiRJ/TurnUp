package com.example.turnup.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.example.turnup.R
import com.example.turnup.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        etEmail = binding.etEmail
        etPassword = binding.etPass

        mAuth = FirebaseAuth.getInstance()
        acciones()

        // Si ya hay un usuario logueado, ir directamente al Home
        mAuth.currentUser?.let {
            goHome(it.email ?: "Usuario", "email")
        }
        //Validacion de email y contraseña
        manageButtonLogin()
        etEmail.doOnTextChanged { text, start, before, count -> manageButtonLogin() }
        etPassword.doOnTextChanged { text, start, before, count -> manageButtonLogin()  }
    }

    private fun acciones() {
        binding.btnLogin.setOnClickListener(this)
        binding.tvRegistro.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.btnLogin.id -> loginUser()
            binding.tvRegistro.id -> startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
    private fun manageButtonLogin(){
        var btnLogin= binding.btnLogin
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        if (
            TextUtils.isEmpty(password) || !ValidateEmail.isEmail(email)){
            btnLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.pale_turquoise))
            btnLogin.isEnabled=false
        }
        else {
            btnLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.deep_cyan))
            btnLogin.isEnabled=true
        }
    }

    private fun loginUser() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Snackbar.make(binding.root, "Por favor completa todos los campos", Snackbar.LENGTH_SHORT).show()
            return
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                goHome(email, "email")
            } else {
                Snackbar.make(binding.root, "Usuario o contraseña incorrectos", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun goHome(email: String, provider: String) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("email", email)
        intent.putExtra("provider", provider)
        startActivity(intent)
        finish()
    }
}
