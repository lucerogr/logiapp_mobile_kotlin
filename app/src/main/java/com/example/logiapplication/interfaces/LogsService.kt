package com.example.logiapplication.interfaces

import com.example.logiapplication.models.Logs
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface LogsService {
    @POST("logs")
    fun addLog(@Body logData: Logs): Call<Logs>

    @GET("Logsdata")
    fun getLogList(): Call<List<Logs>>

    @GET("buscarLogs/{codigo}")
    fun getLog(@Path("codigo") id: Int): Call<Logs>

    @GET("buscarLogsCargo/{codigo}")
    fun getLogsByCargoId(@Path("codigo") id: Int): Call<List<Logs>>
}