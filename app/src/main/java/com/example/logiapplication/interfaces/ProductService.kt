package com.example.logiapplication.interfaces

import com.example.logiapplication.models.Product
import com.example.logiapplication.models.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductService {
    @GET("Productodata")
    fun getProductList(): Call<List<Product>>

    @GET("buscarProductosPorFamilia/{codigo}")
    fun getProductosByFamilyId(@Path("codigo") id: Int): Call<List<Product>>

    @GET("buscarProducto/{codigo}")
    fun getProduct(@Path("codigo") id: Int): Call<Product>
}