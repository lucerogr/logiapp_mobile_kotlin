package com.example.logiapplication.interfaces

import com.example.logiapplication.models.Truck
import retrofit2.Call
import retrofit2.http.GET

interface TruckService {
    @GET("Camiondata")
    fun getCamionData(): Call<List<Truck>>
}