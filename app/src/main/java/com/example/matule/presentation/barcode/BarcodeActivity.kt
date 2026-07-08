package com.example.matule.presentation.barcode

import android.graphics.Bitmap
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.matule.databinding.ActivityBarcodeBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder

class BarcodeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBarcodeBinding
    private var originalBrightness = -1f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarcodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutParams = window.attributes
        originalBrightness = layoutParams.screenBrightness
        layoutParams.screenBrightness = 0.8f
        window.attributes = layoutParams

        val userId = intent.getStringExtra("user_id") ?: "12345"
        val barcodeBitmap = generateBarcodeBitmap(userId)
        binding.ivBarcode.setImageBitmap(barcodeBitmap)

        binding.ivBarcode.setOnClickListener {
            finish()
        }
    }

    private fun generateBarcodeBitmap(data: String): Bitmap {
        val writer = MultiFormatWriter()
        val bitMatrix: BitMatrix = writer.encode(data, BarcodeFormat.CODE_128, 800, 300)
        val encoder = BarcodeEncoder()
        return encoder.createBitmap(bitMatrix)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (originalBrightness >= 0) {
            val layoutParams = window.attributes
            layoutParams.screenBrightness = originalBrightness
            window.attributes = layoutParams
        }
    }
}