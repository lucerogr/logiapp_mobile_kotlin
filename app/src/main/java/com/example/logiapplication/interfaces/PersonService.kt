package com.example.logiapplication.interfaces

import com.example.logiapplication.models.Person
import retrofit2.Call
import retrofit2.http.GET

interface PersonService {
    @GET("Userdata")
    fun getPersonData(): Call<List<Person>>
}