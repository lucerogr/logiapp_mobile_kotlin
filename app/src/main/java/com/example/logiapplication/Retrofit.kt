package com.example.logiapplication

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class RetrofitClients {
    companion object{
        fun getUsersClient(): Retrofit {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val httpClient : OkHttpClient=OkHttpClient.Builder().addInterceptor(interceptor).build()

            return Retrofit.Builder().baseUrl("http://ec2-44-203-57-34.compute-1.amazonaws.com:8080/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()
        }
    }
}