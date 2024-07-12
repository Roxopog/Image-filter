package com.burak.stajapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class HistogramActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_histogram)

        // Dosya yolunu Intent'ten al
        val imagePath = intent.getStringExtra("imagePath")
        val histogramBitmap = BitmapFactory.decodeFile(imagePath)
        histogramBitmap?.let {
            val histogramData = calculateHistogram(it)
            displayHistogramData(histogramData)
            logHistogramData(histogramData)
        }
    }

    private fun calculateHistogram(bitmap: Bitmap): IntArray {
        val histogram = IntArray(256)

        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                val pixel = bitmap.getPixel(x, y)
                val grayValue = Color.red(pixel)  // Bitmap zaten siyah-beyaz olduğundan sadece bir kanalı almak yeter

                // RGB(0,0,0) rengini yoksay
                if (grayValue != 0) {
                    histogram[grayValue]++
                }
            }
        }

        return histogram
    }

    private fun displayHistogramData(histogramData: IntArray) {
        val histogramTextView = findViewById<TextView>(R.id.histogramTextView)
        val histogramStringBuilder = StringBuilder()

        for (i in histogramData.indices) {
            histogramStringBuilder.append("$i = ${histogramData[i]}, ")
        }

        // Son virgülü kaldır
        val histogramString = histogramStringBuilder.toString().removeSuffix(", ")
        histogramTextView.text = histogramString
    }

    private fun logHistogramData(histogramData: IntArray) {
        for (i in histogramData.indices) {
            println("$i = ${histogramData[i]}")
        }
    }
}
