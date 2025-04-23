package com.example.turnup.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import com.example.turnup.databinding.DialogAddShiftBinding
import com.example.turnup.models.Turno

class TurnoDialog(context: Context, private val onTurnoGuardado: (Turno) -> Unit) : Dialog(context) {

    private lateinit var binding: DialogAddShiftBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogAddShiftBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etStartTime.setOnClickListener { showTimePicker(true) }
        binding.etEndTime.setOnClickListener { showTimePicker(false) }

        binding.btnCancel.setOnClickListener { dismiss() }

        binding.btnSave.setOnClickListener {
            val nombre = binding.etShiftName.text.toString().trim()
            val horaInicio = binding.etStartTime.text.toString()
            val horaFin = binding.etEndTime.text.toString()
            val maxVoluntarios = binding.etMaxVolunteers.text.toString().toIntOrNull() ?: 0

            if (nombre.isEmpty() || horaInicio.isEmpty() || horaFin.isEmpty() || maxVoluntarios == 0) {
                Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val turno = Turno(nombre, horaInicio, horaFin, maxVoluntarios)
            onTurnoGuardado(turno)
            dismiss()
        }
    }

    private fun showTimePicker(isStart: Boolean) {
        val picker = android.app.TimePickerDialog(context, { _, hour, minute ->
            val hora = String.format("%02d:%02d", hour, minute)
            if (isStart) binding.etStartTime.setText(hora)
            else binding.etEndTime.setText(hora)
        }, 12, 0, true)
        picker.show()
    }
}
