package com.example.mychinappp

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.mychinappp.adapter.ProductListAdapter
import com.example.mychinappp.data.ProductModel
import com.example.mychinappp.databinding.ActivityMainBinding
import com.example.mychinappp.viewmodel.MainActivityViewModel
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions

class MainActivity : AppCompatActivity() {
    private var readPermissionGranted = false
    private var writePermissionGranted = false
    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>

    lateinit var binding : ActivityMainBinding
    lateinit var adapter : ProductListAdapter
    lateinit var  viewModel:MainActivityViewModel
    lateinit var productModel: ProductModel

    private val resultLauncher =
        registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
            if (result.contents == null) {
                Toast.makeText(this, "Scan Canceled", Toast.LENGTH_LONG).show()
            } else {
                val barcode:String = result.contents.toString()
                binding.barcodeET.setText(barcode)
                //getSingleProduct(barcode)
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        viewModel = ViewModelProvider(this).get(com.example.mychinappp.viewmodel.MainActivityViewModel::class.java)

        setOnClickListener()
        binding.searchBTN.setOnClickListener {
            binding.messageTV.text =""
            binding.barcodeTV.text = ""
            binding.lengthTV.text = ""
            binding.widthTV.text = ""
            binding.heightTV.text = ""
            Toast.makeText(this,"Searching....",Toast.LENGTH_SHORT).show()
           val barcode = binding.barcodeET.text.toString()
            if (barcode != ""){
                getSingleProduct(barcode)
            }else{
                Toast.makeText(this,"Barcode is required!",Toast.LENGTH_LONG).show()
            }
        }
    }



//    private fun initRecyclerView(){
//        binding.productListRV.layoutManager = LinearLayoutManager(this)
//        adapter = ProductListAdapter()
//        binding.productListRV.adapter = adapter
//    }

    private fun getListOfProducts(){
        viewModel.getLiveDataObserver().observe(this, Observer {
            if (it != null){
                adapter.setProductList(it)
                Log.d("MainData", "$it")
                adapter.notifyDataSetChanged()
            }else{
                Log.d("MainData", "$it")
                Toast.makeText(this, "Error in getting data", Toast.LENGTH_LONG).show()
            }
        })
        viewModel.makeAPICall()
    }
    private fun setupScanner() {

        resultLauncher.launch(ScanOptions().setCaptureActivity(CaptureActivityPortrait::class.java))
    }

    private fun setOnClickListener() {
        binding.scanBTN.setOnClickListener { setupScanner() }
    }
    private fun setUpViews(productModel: ProductModel){
        binding.barcodeTV.text = productModel.barcode
        binding.lengthTV.text = productModel.length
        binding.widthTV.text = productModel.width
        binding.heightTV.text = productModel.height
    }
    private fun getSingleProduct(barcode:String){
        val productModel = viewModel.getProductAPICall(barcode)

        viewModel.foundProduct.observe(this, Observer {
            val product = viewModel.foundProduct.value
            val mediaPlayer:MediaPlayer =MediaPlayer.create(this,R.raw.sound)
            if (product != null) {
                setUpViews(product)
                val msg = "Product Found"
                binding.messageTV.text = msg
                binding.messageTV.setTextColor(ContextCompat.getColor(this,R.color.green))
                mediaPlayer.start()
            }else{
                val msg = "Product Not Found"
                binding.messageTV.text = msg
                binding.messageTV.setTextColor(ContextCompat.getColor(this,R.color.red))
                binding.barcodeTV.text = ""
                binding.lengthTV.text = ""
                binding.widthTV.text = ""
                binding.heightTV.text = ""
                mediaPlayer.start()
            }
        })



    }

}


