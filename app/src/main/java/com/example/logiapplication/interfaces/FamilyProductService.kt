package com.example.logiapplication.interfaces

import com.example.logiapplication.models.FamilyProduct
import com.example.logiapplication.models.Truck
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FamilyProductService {
    @GET("FamProductodata")
    fun getFamilyProduct(): Call<List<FamilyProduct>>

    @GET("buscarFamProducto/{codigo}")
    fun getAFamilyProduct(@Path("codigo") id: Int): Call<FamilyProduct>

}