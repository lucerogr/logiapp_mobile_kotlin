package com.example.logiapplication.interfaces

import com.example.logiapplication.models.Person
import com.example.logiapplication.models.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface PersonService {
    @GET("Userdata")
    fun getPersonData(): Call<List<Person>>

    @GET("buscarUser/{codigo}")
    fun getPerson(@Path("codigo") id: Int): Call<Person>
}