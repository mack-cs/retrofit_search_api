package com.example.mychinappp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mychinappp.R
import com.example.mychinappp.data.ProductModel

class ProductListAdapter: RecyclerView.Adapter<ProductListAdapter.ProductViewHolder>() {
   private var productList: List<ProductModel>? = null
    fun setProductList(productList: List<ProductModel>?) {
        this.productList = productList
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_list, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(productList?.get(position)!!)
    }

    override fun getItemCount(): Int {
        if (productList == null) return 0
        else return productList?.size!!
    }
    class ProductViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var barcode = view.findViewById<TextView>(R.id.barcodeTV)
        fun bind(data: ProductModel){
            barcode.text = data.barcode
        }

    }
}