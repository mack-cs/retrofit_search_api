package com.example.mychinappp.viewmodel


import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mychinappp.data.ProductModel
import com.example.mychinappp.retrofit.RetroInstance
import com.example.mychinappp.retrofit.RetrofitServiceInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivityViewModel: ViewModel() {
    var liveDataList: MutableLiveData<List<ProductModel>> = MutableLiveData()
    var foundProduct = MutableLiveData<ProductModel>()

    fun getLiveDataObserver(): MutableLiveData<List<ProductModel>> {
        return liveDataList
    }



    fun getProductAPICall(barcode:String){
        val retroInstance = RetroInstance.getRetroInstance()
        val retroService = retroInstance.create(RetrofitServiceInterface::class.java)
        val call = retroService.getProduct(barcode)
        call.enqueue(object : Callback<ProductModel>{
            override fun onResponse(call: Call<ProductModel>, response: Response<ProductModel>) {
                val data = response.body()
                Log.d("ModelV","$data")
                if (data != null) {
                    foundProduct.postValue(data)
                    Log.e("MV","Success")

                }else{
                    //productModel = ProductModel(null,null,null,null)
                    foundProduct.postValue(null)
                    Log.e("ModelV","Failure")
                }
            }

            override fun onFailure(call: Call<ProductModel>, t: Throwable) {
                foundProduct.postValue(null)
                Log.d("ModelV", "failed here")
            }

        })
    }
}