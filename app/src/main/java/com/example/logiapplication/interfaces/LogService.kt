package com.example.logiapplication.interfaces

import com.example.logiapplication.models.Cargo
import com.example.logiapplication.models.Log
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface LogService {
    @POST("logs")
    fun addLog(@Body logData: Log): Call<Log>

    @GET("Logsdata")
    fun getLogList(): Call<List<Log>>

    @GET("buscarLogs/{codigo}")
    fun getLog(@Path("codigo") id: Int): Call<Log>
}