package com.example.logiapplication.interfaces

import com.example.logiapplication.models.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

interface UserService {
    @GET("Usersdata")
    fun login(): Call<List<User>>

    @GET("buscarUsers/{codigo}")
    fun getUser(@Path("codigo") id: Int): Call<User>
    /*@GET("Usersdata")
    fun login(@Query("userUsername") userUsername: String,
              @Query("userPassword") userPassword: String): Call<List<User>>
     */
    @GET("buscarRoles/{codigo}")
    fun getRolesByUserId(@Path("codigo") id: Int): Call<Array<Any?>?>

    @GET("buscarPerson/{codigo}")
    fun getPersonByUserId(@Path("codigo") id: Int): Call<Array<Any?>?>

    @GET("buscarUsersPorRol/{codigo}")
    fun getUsersByRolId(@Path("codigo") id: Int): Call<List<User>>
}