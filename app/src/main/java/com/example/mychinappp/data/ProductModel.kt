package com.example.mychinappp.data

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

//@Serializable
data class ProductModel(@SerializedName("barcode") val barcode: String?, @SerializedName("length")
                        val length: String?, @SerializedName("width") val width: String?, @SerializedName("height") val height: String?){
    var message: String? = null
    constructor(message: String?) : this(null,null,null,null){
        this.message = message
    }
}
