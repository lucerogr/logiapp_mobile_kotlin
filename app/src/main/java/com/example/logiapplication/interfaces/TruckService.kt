package com.example.logiapplication.interfaces

import com.example.logiapplication.models.Truck
import com.example.logiapplication.models.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface TruckService {
    @GET("Camiondata")
    fun getCamionData(): Call<List<Truck>>

    @GET("buscarCamion/{codigo}")
    fun getTruck(@Path("codigo") id: Int): Call<Truck>

}