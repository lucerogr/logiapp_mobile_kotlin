package com.example.logiapplication.interfaces

import com.example.logiapplication.models.Rol
import retrofit2.Call
import retrofit2.http.GET

interface RolService {
    @GET("Roldata")
    fun getRolData(): Call<List<Rol>>
}