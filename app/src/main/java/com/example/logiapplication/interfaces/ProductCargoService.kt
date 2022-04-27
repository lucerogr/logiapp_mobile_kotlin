package com.example.logiapplication.interfaces

import com.example.logiapplication.models.Cargo
import com.example.logiapplication.models.ProductCargo
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ProductCargoService {
    @POST("productcargo")
    fun addProductCargo(@Body productCargoData: ProductCargo): Call<ProductCargo>
}