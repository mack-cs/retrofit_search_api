package com.example.mychinappp.retrofit

import com.example.mychinappp.data.ProductModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface RetrofitServiceInterface {
    @GET("all")
    fun getProductsList(): Call<List<ProductModel>>
    @GET("{barcode}")
    fun getProduct(@Path("barcode") barcode: String): Call<ProductModel>
}