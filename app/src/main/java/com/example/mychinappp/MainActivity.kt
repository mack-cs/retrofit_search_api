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
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.mychinappp.adapter.ProductListAdapter
import com.example.mychinappp.data.ProductModel
import com.example.mychinappp.databinding.ActivityMainBinding
import com.example.mychinappp.util.Utils
import com.example.mychinappp.viewmodel.MainActivityViewModel
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    var counter = 0
    private lateinit var mediaPlayer:MediaPlayer
    private lateinit var  viewModel:MainActivityViewModel

    private val resultLauncher =
        registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
            if (result.contents == null) {
                Toast.makeText(this, "Scan Canceled", Toast.LENGTH_LONG).show()
            } else {
                val barcode:String = result.contents.toString()
                binding.barcodeET.setText(barcode)
                getSingleProduct(barcode)
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        viewModel = ViewModelProvider(this).get(com.example.mychinappp.viewmodel.MainActivityViewModel::class.java)
        mediaPlayer = MediaPlayer.create(this,R.raw.sound)
        setOnClickListener()
        binding.searchBTN.setOnClickListener {
            binding.messageTV.text =""
            emptyViews()

           val barcode = binding.barcodeET.text.toString()
            if (barcode != ""){
                Toast.makeText(this,"Searching....",Toast.LENGTH_SHORT).show()
                getSingleProduct(barcode)
            }else{
                Toast.makeText(this,"Barcode is required!",Toast.LENGTH_LONG).show()
            }
        }

        binding.barcodeET.setOnEditorActionListener{
            view, actionId ,keyEvent -> if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || keyEvent == null
            || keyEvent.keyCode == KeyEvent.KEYCODE_ENTER){
                val barcode:String = binding.barcodeET.text.toString()
                getSingleProduct(barcode)
                    true
            }else{
                false
            }
        }
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
       if (Utils.checkForInternet(this)){
           viewModel.getProductAPICall(barcode)
           viewModel.foundProduct.observe(this, Observer {
               val product = viewModel.foundProduct.value
               if (product != null) {
                   setUpViews(product)
                   val msg = "Product Found"
                   binding.messageTV.text = msg
                   binding.messageTV.setTextColor(ContextCompat.getColor(this,R.color.green))
               }else{
                   val msg = "Product Not Found"
                   binding.messageTV.text = msg
                   binding.messageTV.setTextColor(ContextCompat.getColor(this,R.color.red))
                   emptyViews()
               }
               mediaPlayer.start()
           })
       }else{
           Toast.makeText(this, "No connection available", Toast.LENGTH_SHORT).show()
       }



    }

    private fun emptyViews() {
        binding.barcodeTV.text = ""
        binding.lengthTV.text = ""
        binding.widthTV.text = ""
        binding.heightTV.text = ""
    }

}


