package com.example.logiapplication.interfaces

import com.example.logiapplication.models.Cargo
import com.example.logiapplication.models.ProductCargo
import com.example.logiapplication.models.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ProductCargoService {
    @POST("productcargo")
    fun addProductCargo(@Body productCargoData: ProductCargo): Call<ProductCargo>


    @GET("buscarProductoCargoPorCargo/{codigo}")
    fun getProductCargoByCargoId(@Path("codigo") id: Int): Call<List<ProductCargo>>

    @POST("actualizarProductCargo/{codigo}")
    fun updateProductCargo(@Body productCargoData: ProductCargo, @Path("codigo") id: Int): Call<ProductCargo>
}