package com.example.mychinappp.viewmodel


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mychinappp.data.ProductModel
import com.example.mychinappp.retrofit.RetroInstance
import com.example.mychinappp.retrofit.RetrofitServiceInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException


class MainActivityViewModel: ViewModel() {
    private val foundProduct = MutableLiveData<ProductModel>()
    private val errorMessage = MutableLiveData<String>()
    private val isLoadingData = MutableLiveData<Boolean>()

    fun getErrorMessage(): LiveData<String> = errorMessage
    fun getLoadingStatus(): LiveData<Boolean> = isLoadingData
    fun getFoundProduct(): LiveData<ProductModel> = foundProduct


    fun getProductAPICall(barcode:String){
        val retroInstance = RetroInstance.getRetroInstance()
        val retroService = retroInstance.create(RetrofitServiceInterface::class.java)
        val call = retroService.getProduct(barcode)
        isLoadingData.value = true
        call.enqueue(object : Callback<ProductModel>{
            override fun onResponse(call: Call<ProductModel>, response: Response<ProductModel>) {
                val data = response.body()
                isLoadingData.value = false
                if (data?.barcode != null) {
                    foundProduct.value = data
                }else if (data != null){
                    foundProduct.value = null
                    errorMessage.value = "Product Not Found"
                }
            }

            override fun onFailure(call: Call<ProductModel>, t: Throwable) {
                isLoadingData.value = false
                foundProduct.value = null
                if (t is IllegalStateException){
                    errorMessage.value = "Product Not Found"
                }
                if(t is SocketTimeoutException){
                    errorMessage.value = "Connection Timeout"
                }
            }

        })
    }


}

