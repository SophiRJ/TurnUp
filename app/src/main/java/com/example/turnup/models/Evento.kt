package com.example.turnup.models

import com.google.firebase.firestore.Exclude
import java.io.Serializable

data class Evento(
    @get:Exclude
    var id: String = "",
    var nombre: String = "",
    var tipo: String = "",
    var ubicaciones: List<String> = listOf(),
    var fechaInicio: Long = 0L,
    var fechaFin: Long = 0L,
    var turnos: List<Turno> = listOf(),
    var voluntariosRequeridos: Int = 0,
    var profesiones: List<String> = listOf(),
    var habilidades: List<String> = listOf(),
    var tareas: List<String> = listOf(),
    var imagen: String = "",
    var autor: String="",
    var institucion: String=""
):Serializable
