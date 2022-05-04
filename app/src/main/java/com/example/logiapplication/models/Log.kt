package com.example.logiapplication.models

class Log (
    val codigo: Int?,

    val logCargoDate: String,

    val logCargoHour: String,

    val logCargoUbication: String,

    val logCargoTemperature: String,

    val logCargoHumidity: String,

    val logCargoVelocity: String,

    val logCargoAlertType: Int,

    val cargo: Cargo,
)