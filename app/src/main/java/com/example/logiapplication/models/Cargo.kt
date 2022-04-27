package com.example.logiapplication.models

import android.text.format.DateFormat
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.DateSerializer
import java.time.LocalTime
import java.util.*

class Cargo (
    val codigo: Int?,

    val cargoName: String,

    val cargoDate: String,

    val cargoHour: String,

    val cargoInitialUbication: String,

    val cargoFinalUbication: String,

    val cargoStatus: String,

    val cargoRouteDuration: String,

    val cargoRouteStatus: String,

    val camion: Truck,

    val famproducto: FamilyProduct,

    val personClientId: Person,

    val personOperatorId: Person,

    val personDriverId: Person,

)