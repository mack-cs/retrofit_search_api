package com.example.mychinappp.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetroInstance {
    companion object{
          private const val BASE_URL = "https://ppp.hopewarriorscharity.org.za/api/slimapi-ppp/public/products/"
//        private const val BASE_URL = "https://restcountries192.168.0.196/api/slimapi-ppp/public/"
        fun getRetroInstance(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}