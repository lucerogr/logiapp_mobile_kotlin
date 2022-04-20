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

            return Retrofit.Builder().baseUrl("http://10.0.2.2:8080/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()
        }
        /*var API_BASE_URL : String = "http://10.0.2.2:8080/api/"
        lateinit var retrofit : Retrofit
        lateinit var gson : Gson

        fun getUsersClient(): Retrofit{
            if(retrofit == null) {
                gson = GsonBuilder()
                    .setLenient()
                    .create()
                retrofit=Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            }
            return retrofit
        }*/
    }
}