package com.example.mychinappp.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetroInstance {
    companion object{
          private const val BASE_URL = "https://promo.mychinasa.co.za/api/slimapi-ppp/public/products/"
//        private const val BASE_URL = "https://restcountries192.168.0.196/api/slimapi-ppp/public/"
        val client = OkHttpClient.Builder().apply {
            this.connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
        }.build()


        fun getRetroInstance(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}