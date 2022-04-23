package com.example.logiapplication.interfaces

import com.example.logiapplication.models.FamilyProduct
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FamilyProductService {
    @GET("FamProductodata")
    fun getFamilyProduct(): Call<List<FamilyProduct>>
}