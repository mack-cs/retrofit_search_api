package com.example.mychinappp

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.mychinappp.data.ProductModel
import com.example.mychinappp.databinding.ActivityMainBinding
import com.example.mychinappp.util.Utils
import com.example.mychinappp.viewmodel.MainActivityViewModel
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    var count = 0
    private lateinit var mediaPlayerError:MediaPlayer
    private lateinit var mediaPlayerSuccess:MediaPlayer
    private lateinit var  viewModel:MainActivityViewModel

    private val resultLauncher =
        registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
            if (result.contents == null) {
                Toast.makeText(this, "Scan Canceled", Toast.LENGTH_LONG).show()
            } else {
                val barcode:String = result.contents.toString()
                binding.barcodeET.setText(barcode)
                getSingleProduct(barcode)
                Log.d("LD-Called","Called after barcode scan")
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        viewModel = ViewModelProvider(this).get(com.example.mychinappp.viewmodel.MainActivityViewModel::class.java)
        mediaPlayerError = MediaPlayer.create(this,R.raw.error)
        mediaPlayerSuccess = MediaPlayer.create(this,R.raw.success)
        binding.barcodeET.requestFocus();
        setOnClickListener()
        binding.searchBTN.setOnClickListener {
            binding.messageTV.text =""
            emptyViews()

           val barcode = binding.barcodeET.text.toString()
            if (barcode != ""){
                Toast.makeText(this,"Searching....",Toast.LENGTH_SHORT).show()
                getSingleProduct(barcode)
                Log.d("LD-Called","Called after button click")
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

    fun <T> LiveData<T>.observeOnceAfterInit(owner: LifecycleOwner, observer: (T) -> Unit) {
        var firstObservation = true

        observe(owner, object: Observer<T>
        {
            override fun onChanged(value: T) {
                if(firstObservation)
                {
                    firstObservation = false
                }
                else
                {
                    removeObserver(this)
                    observer(value)
                }
            }
        })
    }

    private fun getSingleProduct(barcode:String){
       if (Utils.checkForInternet(this)){
           viewModel.getProductAPICall(barcode)
           viewModel.getLoadingStatus().observe(this, Observer {
               val status = viewModel.getLoadingStatus().value
               if (status == true){
                   //binding.containerLLC.visibility = View.INVISIBLE
                   binding.progressBar.visibility = View.VISIBLE
               }else{
                   binding.containerLLC.visibility = View.VISIBLE
                   binding.progressBar.visibility = View.GONE
               }
           })
           val productLiveData = viewModel.getFoundProduct()

           productLiveData.observe(this, Observer { it ->
               if (it != null) {
                   setUpViews(it)
                   binding.detailsContainer.visibility = View.VISIBLE
                   val msg = "Product Found"
                   binding.barcodeET.setText("")
                   binding.messageTV.text = msg
                   binding.messageTV.setTextColor(ContextCompat.getColor(this, R.color.green))
                   mediaPlayerSuccess.start()
               } else {
                   binding.detailsContainer.visibility = View.GONE
                   var errMsg: String? = ""
                   viewModel.getErrorMessage().observe(this, {
                       errMsg = it.toString()
                   })
                   binding.messageTV.text = errMsg
                   binding.messageTV.setTextColor(ContextCompat.getColor(this, R.color.red))
                   emptyViews()
                   mediaPlayerError.start()
               }
               binding.barcodeET.requestFocus();

           })

       }else{
           Toast.makeText(this, "No connection available", Toast.LENGTH_SHORT).show()
       }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        Log.d("Observer", "OnStop")
    }

    private fun emptyViews() {
        binding.barcodeTV.text = ""
        binding.lengthTV.text = ""
        binding.widthTV.text = ""
        binding.heightTV.text = ""
    }

}


