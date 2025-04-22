package com.example.turnup.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.turnup.R
import com.example.turnup.databinding.ActivityRegisterBinding
import com.example.turnup.models.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity(), OnClickListener {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setupUI()
    }

    private fun setupUI() {
        binding.rgRol.setOnCheckedChangeListener { _, checkedId ->
            binding.lyCodI.visibility =
                if (checkedId == binding.rbAdministrador.id) View.VISIBLE else View.GONE
        }

        binding.spinnerOcupacion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedOcupacion = parent.getItemAtPosition(position).toString()
                val habilidadesArray = when (selectedOcupacion) {
                    "Medico/Personal de salud" -> R.array.arrayHabilidades_salud
                    "Seguridad/ Policia / Bombero" -> R.array.arrayHabilidades_seguridad
                    "Organizador de Eventos/ Coordinador" -> R.array.arrayHabilidades_eventos
                    "Tecnico en sistemas/ Soporte IT" -> R.array.arrayHabilidades_it
                    "Voluntario general/ Asistente social" -> R.array.arrayHabilidades_voluntario
                    else -> R.array.habilidades
                }

                val habilidadesAdapter = ArrayAdapter.createFromResource(
                    this@RegisterActivity, habilidadesArray, android.R.layout.simple_spinner_item
                )
                habilidadesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerHabilidades.adapter = habilidadesAdapter
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.btnRegistrarse.setOnClickListener(this)
        binding.tvTerms.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.btnRegistrarse.id -> registrarUsuario()
            binding.tvTerms.id -> startActivity(Intent(this, TermsActivity::class.java))
        }
    }

    private fun registrarUsuario() {
        val rolId = binding.rgRol.checkedRadioButtonId
        val rolSeleccionado = if (rolId != -1) findViewById<RadioButton>(rolId).text.toString() else ""
        val editTextCodigo = binding.lyCodI.getChildAt(0) as? android.widget.EditText
        val codigoInstitucion = editTextCodigo?.text?.toString()?.trim() ?: ""

        val usuario = Usuario(
            nombre = binding.etNombre.text.toString().trim(),
            apellido = binding.etApellido.text.toString().trim(),
            edad = binding.etEdad.text.toString().trim().toIntOrNull() ?: 0,
            email = binding.etEmail.text.toString().trim(),
            sexo = binding.spinnerSexo.selectedItem.toString(),
            ocupacion = binding.spinnerOcupacion.selectedItem.toString(),
            habilidad = binding.spinnerHabilidades.selectedItem.toString(),
            rol = rolSeleccionado,
            codigoInstitucion = if (rolSeleccionado == "Administrador") codigoInstitucion else "",
            nombreInstitucion = "" // Se completará tras validar
        )

        if (!binding.cbAccept.isChecked || usuario.nombre.isEmpty() || usuario.apellido.isEmpty() ||
            usuario.edad == 0 || usuario.email.isEmpty() || usuario.rol.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos y acepta términos", Toast.LENGTH_SHORT).show()
            return
        }

        if (usuario.rol == "Administrador") {
            validarCodigoInstitucion(usuario)
        } else {
            crearUsuarioEnFirebase(usuario)
        }
    }

    private fun validarCodigoInstitucion(usuario: Usuario) {
        db.collection("instituciones")
            .whereEqualTo("codigo", usuario.codigoInstitucion)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val nombreInstitucion = documents.first().getString("nombre") ?: "Sin nombre"
                    val usuarioConInstitucion = usuario.copy(nombreInstitucion = nombreInstitucion)
                    crearUsuarioEnFirebase(usuarioConInstitucion)
                } else {
                    Toast.makeText(this, "Código de institución inválido.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al validar código: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun crearUsuarioEnFirebase(usuario: Usuario) {
        auth.createUserWithEmailAndPassword(usuario.email, binding.etPassword.text.toString().trim())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser!!.uid
                    db.collection("Usuarios").document(uid).set(usuario)
                        .addOnSuccessListener {
                            Toast.makeText(this, "¡Registro exitoso!", Toast.LENGTH_SHORT).show()
                            val intent:Intent=Intent(applicationContext,HomeActivity::class.java)
                            intent.putExtra("email",binding.etEmail.text.toString())
                            intent.putExtra("provider","email")
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al guardar datos: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                } else {
                    Toast.makeText(this, "Error al registrar: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}

