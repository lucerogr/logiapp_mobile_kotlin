package com.example.logiapplication.interfaces

import com.example.logiapplication.models.Cargo
import com.example.logiapplication.models.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CargoService {
    @POST("cargo")
    fun addCargo(@Body cargoData: Cargo): Call<Cargo>

    @GET("Cargodata")
    fun getCargoList(): Call<List<Cargo>>

    @GET("buscarCargo/{codigo}")
    fun getCargo(@Path("codigo") id: Int): Call<Cargo>

    @GET("buscarCargoporCliente/{codigo}")
    fun getCargoByClient(@Path("codigo") id: Int): Call<List<Cargo>>

    @GET("buscarCargoporConductor/{codigo}")
    fun getCargoByCarrier(@Path("codigo") id: Int): Call<List<Cargo>>

    @GET("buscarCargoporOperador/{codigo}")
    fun getCargoByOperator(@Path("codigo") id: Int): Call<List<Cargo>>
}