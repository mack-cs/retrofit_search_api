package com.example.mychinappp.retrofit

import androidx.lifecycle.LiveData
import com.example.mychinappp.data.ProductModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface RetrofitServiceInterface {
    @GET("all")
    fun getProductsList(): Call<List<ProductModel>>
    @GET("{barcode}")
    fun getProduct(@Path("barcode") barcode: String): Call<ProductModel>
    @GET("{barcode}")
    fun getProductLD(@Path("barcode") barcode: String): Call<LiveData<ProductModel>>
}