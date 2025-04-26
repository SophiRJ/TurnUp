package com.example.turnup.models

import java.io.Serializable

data class Turno(
    var nombre: String = "",
    var horaInicio: String = "",
    var horaFin: String = "",
    var maxVoluntarios: Int = 0
):Serializable
