package com.example.logiapplication.interfaces

import com.example.logiapplication.models.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {
    @GET("Usersdata")
    fun login(@Query("userUsername") userUsername: String,
              @Query("userPassword") userPassword: String): Call<List<User>>
}