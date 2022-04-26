package com.example.logiapplication.interfaces

import com.example.logiapplication.models.Cargo
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface CargoService {
    @POST("cargo")
    fun addCargo(@Body cargoData: Cargo): Call<Cargo>
}