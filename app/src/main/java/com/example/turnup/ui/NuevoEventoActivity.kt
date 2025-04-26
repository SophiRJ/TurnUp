package com.example.turnup.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.example.turnup.adapters.TurnoAdapter
import com.example.turnup.adapters.UbicacionAdapter
import com.example.turnup.databinding.ActivityNuevoEventoBinding
import com.example.turnup.models.Evento
import com.example.turnup.models.Turno
import com.google.firebase.auth.FirebaseAuth
import android.app.DatePickerDialog
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.EditText
import com.example.turnup.R
import com.example.turnup.adapters.TareaAdapter
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.*


class NuevoEventoActivity : AppCompatActivity(), OnClickListener,OnItemClickListener {

    private lateinit var binding: ActivityNuevoEventoBinding
    private val listaTurnos = mutableListOf<Turno>()
    private val listaUbicaciones = mutableListOf<String>()
    private lateinit var ubicacionAdapter: UbicacionAdapter
    private lateinit var adapter: TurnoAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db:FirebaseFirestore
    private lateinit var autorEvento:String
    private lateinit var apellido:String
    private lateinit var institucion:String
    private var fechaInicio: Long = 0L
    private var fechaFin: Long = 0L
    private val listaTareas = mutableListOf<String>()
    private lateinit var tareaAdapter: TareaAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNuevoEventoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = TurnoAdapter(listaTurnos, ::editarTurno, ::eliminarTurno)
        binding.rvTurnos.layoutManager = LinearLayoutManager(this)
        binding.rvTurnos.adapter = adapter

        binding.btnAddTurno.setOnClickListener(this)
        binding.btnCrearEvento.setOnClickListener(this)

        ubicacionAdapter = UbicacionAdapter(listaUbicaciones) { posicion ->
            listaUbicaciones.removeAt(posicion)
            ubicacionAdapter.notifyItemRemoved(posicion)
        }
        binding.rvUbicaciones.layoutManager = LinearLayoutManager(this)
        binding.rvUbicaciones.adapter = ubicacionAdapter

        binding.btnAddUbicacion.setOnClickListener(this)

        binding.btnFechaInicio.setOnClickListener(this)
        binding.btnFechaFin.setOnClickListener(this)

        binding.btnSeleccionarProfesiones.setOnClickListener(this)
        binding.actHabilidades.onItemClickListener = this

        tareaAdapter = TareaAdapter(listaTareas) { posicion ->
            listaTareas.removeAt(posicion)
            tareaAdapter.notifyItemRemoved(posicion)
        }

        binding.rvTareas.layoutManager = LinearLayoutManager(this)
        binding.rvTareas.adapter = tareaAdapter

        binding.btnAddTarea.setOnClickListener {
            mostrarDialogoTarea()
        }


        mAuth=FirebaseAuth.getInstance()
        db=FirebaseFirestore.getInstance()
        cargarInstitucion()

    }
    private fun cargarInstitucion() {
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            db.collection("Usuarios").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        autorEvento = document.getString("nombre").toString()
                        apellido = document.getString("apellido").toString()
                        institucion = document.getString("nombreInstitucion").toString()

                        binding.tvInstitucion.text="Institucion: $institucion"
                    }
                }
        }
    }
        private fun abrirDialogoTurno() {
            val dialog = TurnoDialog(this) { turno ->
                listaTurnos.add(turno)
                adapter.notifyItemInserted(listaTurnos.size - 1)
            }
            dialog.show()
        }

        private fun editarTurno(turno: Turno, posicion: Int) {
            val dialog = TurnoDialog(this) { turnoEditado ->
                listaTurnos[posicion] = turnoEditado
                adapter.notifyItemChanged(posicion)
            }
            dialog.show()
        }

        private fun eliminarTurno(posicion: Int) {
            listaTurnos.removeAt(posicion)
            adapter.notifyItemRemoved(posicion)
        }

        private fun guardarEvento() {
            val nombre = binding.etNombreEvento.text.toString()
            val tipo = binding.spinnerTipoEvento.selectedItem.toString()
            val voluntariosStr = binding.etVoluntariosRequeridos.text.toString()
            val voluntarios = voluntariosStr.toIntOrNull() ?: 0
            if (nombre.isEmpty() || listaTurnos.isEmpty()) {
                Toast.makeText(this, "Nombre y al menos un turno requerido", Toast.LENGTH_SHORT)
                    .show()
                return
            }
            val profesionesSeleccionadas = mutableListOf<String>()
            for (i in 0 until binding.chipGroupProfesiones.childCount) {
                val chip = binding.chipGroupProfesiones.getChildAt(i) as Chip
                profesionesSeleccionadas.add(chip.text.toString())
            }
            val habilidadesSeleccionadas = mutableListOf<String>()
            for (i in 0 until binding.chipGroupHabilidades.childCount) {
                val chip = binding.chipGroupHabilidades.getChildAt(i) as Chip
                habilidadesSeleccionadas.add(chip.text.toString())
            }


            val evento = Evento(
                nombre = nombre,
                turnos = listaTurnos,
                tipo = tipo,
                voluntariosRequeridos = voluntarios,
                autor = mAuth.currentUser?.uid ?: "",
                institucion = institucion,
                ubicaciones = listaUbicaciones,
                fechaInicio = fechaInicio,
                fechaFin = fechaFin,
                profesiones = profesionesSeleccionadas,
                habilidades = habilidadesSeleccionadas,
                tareas = listaTareas

            )

            db.collection("eventos")
                .add(evento)
                .addOnSuccessListener { doc->
                    evento.id=doc.id
                    Toast.makeText(this, "Evento guardado con éxito", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_LONG).show()
                }
        }

    override fun onClick(v: View?) {
        when(v?.id){
            binding.btnAddUbicacion.id->{
                val ubicacion = binding.etUbicacion.text.toString().trim()
                if (ubicacion.isNotEmpty()) {
                    listaUbicaciones.add(ubicacion)
                    ubicacionAdapter.notifyItemInserted(listaUbicaciones.size - 1)
                    binding.etUbicacion.text.clear()
                } else {
                    Toast.makeText(this, "Ingresa una ubicación", Toast.LENGTH_SHORT).show()
                }
            }
            binding.btnAddTurno.id->{
                abrirDialogoTurno()
            }
            binding.btnCrearEvento.id->{
                guardarEvento()
            }
            binding.btnFechaInicio.id->{
                mostrarDatePicker { fechaMillis ->
                    fechaInicio = fechaMillis
                    binding.btnFechaInicio.text = formatFecha(fechaMillis)
                }
            }
            binding.btnFechaFin.id->{
                mostrarDatePicker { fechaMillis ->
                    fechaFin = fechaMillis
                    binding.btnFechaFin.text = formatFecha(fechaMillis)
                }
            }
            binding.btnSeleccionarProfesiones.id->{
                mostrarDialogoSeleccionProfesiones()
            }

        }
    }
    private fun mostrarDatePicker(onDateSelected: (Long) -> Unit) {
        val calendario = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendario.set(year, month, dayOfMonth)
                onDateSelected(calendario.timeInMillis)
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun formatFecha(millis: Long): String {
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formato.format(Date(millis))
    }
    private fun mostrarDialogoSeleccionProfesiones() {
        val profesionesArray = resources.getStringArray(R.array.arrayProfesion).drop(1).toTypedArray() // Drop "Profesión"
        val profesionesSeleccionadas = BooleanArray(profesionesArray.size)

        AlertDialog.Builder(this)
            .setTitle("Seleccionar profesiones")
            .setMultiChoiceItems(profesionesArray, profesionesSeleccionadas) { _, which, isChecked ->
                profesionesSeleccionadas[which] = isChecked
            }
            .setPositiveButton("Aceptar") { _, _ ->
                val chipGroup = binding.chipGroupProfesiones
                chipGroup.removeAllViews() // Limpiar anteriores
                profesionesSeleccionadas.forEachIndexed { index, selected ->
                    if (selected) {
                        val chip = Chip(this).apply {
                            text = profesionesArray[index]
                            isCloseIconVisible = true
                            setOnCloseIconClickListener { chipGroup.removeView(this) }
                        }
                        chipGroup.addView(chip)
                    }
                }
                actualizarSugerenciasHabilidades()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    private fun obtenerHabilidadesPorProfesiones(profesiones: List<String>): List<String> {
        val habilidades = mutableSetOf<String>()

        val mapa = mapOf(
            "Medico/Personal de salud" to R.array.arrayHabilidades_salud,
            "Seguridad/ Policia / Bombero" to R.array.arrayHabilidades_seguridad,
            "Organizador de Eventos/ Coordinador" to R.array.arrayHabilidades_eventos,
            "Tecnico en sistemas/ Soporte IT" to R.array.arrayHabilidades_it,
            "Voluntario general/ Asistente social" to R.array.arrayHabilidades_voluntario
        )

        profesiones.forEach { profesion ->
            mapa[profesion]?.let { arrayResId ->
                habilidades.addAll(resources.getStringArray(arrayResId))
            }
        }

        return habilidades.toList()
    }
    private fun actualizarSugerenciasHabilidades() {
        val profesionesSeleccionadas = mutableListOf<String>()
        for (i in 0 until binding.chipGroupProfesiones.childCount) {
            val chip = binding.chipGroupProfesiones.getChildAt(i) as Chip
            profesionesSeleccionadas.add(chip.text.toString())
        }

        val habilidades = obtenerHabilidadesPorProfesiones(profesionesSeleccionadas)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, habilidades)
        binding.actHabilidades.setAdapter(adapter)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val habilidad = parent?.getItemAtPosition(position).toString()

        val chip = Chip(this).apply {
            text = habilidad
            isCloseIconVisible = true
            setOnCloseIconClickListener {
                binding.chipGroupHabilidades.removeView(this)
            }
        }

        // Evita duplicados
        val yaExiste = (0 until binding.chipGroupHabilidades.childCount).any {
            val chipExistente = binding.chipGroupHabilidades.getChildAt(it) as Chip
            chipExistente.text == habilidad
        }

        if (!yaExiste) {
            binding.chipGroupHabilidades.addView(chip)
        }

        binding.actHabilidades.text = null
    }
    private fun mostrarDialogoTarea() {
        val input = EditText(this)
        input.hint = "Descripción de la tarea"

        AlertDialog.Builder(this)
            .setTitle("Nueva Tarea")
            .setView(input)
            .setPositiveButton("Agregar") { _, _ ->
                val tarea = input.text.toString()
                if (tarea.isNotEmpty()) {
                    listaTareas.add(tarea)
                    tareaAdapter.notifyItemInserted(listaTareas.size - 1)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
