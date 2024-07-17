package com.burak.stajapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class HistogramActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_histogram)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Dosya yolunu Intent'ten al
        val imagePath = intent.getStringExtra("imagePath")
        val histogramBitmap = BitmapFactory.decodeFile(imagePath)
        histogramBitmap?.let {
            // Histogram arrayleri
            val histogramData = calculateHistogram(it) // 256
            val histogramData2 = arrayTopla(histogramData) // 128
            val histogramData3 = arrayTopla(histogramData2) // 64
            val histogramData4 = arrayTopla(histogramData3) // 32
            val histogramData5 = arrayTopla(histogramData4) // 16
            val histogramData6 = arrayTopla(histogramData5) // 8
            val histogramData7 = arrayTopla(histogramData6) // 4
            val histogramData8 = arrayTopla(histogramData7) // 2

            // Tüm histogram verilerini bir liste içinde topla
            val histogramDataList = listOf(
                histogramData, histogramData2, histogramData3,
                histogramData4, histogramData5, histogramData6,
                histogramData7, histogramData8
            )

            // RecyclerView için Adapter'ı ve veri setini ayarla
            val adapter = HistogramAdapter(histogramDataList)
            recyclerView.adapter = adapter
        }
    }

    fun arrayTopla(array: IntArray): IntArray {
        // Yeni array için gerekli boyutta bir IntArray oluşturuyoruz
        val yeniArray = IntArray(array.size / 2)

        // Array'in elemanlarını ikişerli gruplar halinde toplayıp yeni array'e atıyoruz
        for (i in array.indices step 2) {
            val toplam = array[i] + array[i + 1]
            yeniArray[i / 2] = toplam
        }

        return yeniArray
    }

    private fun calculateHistogram(bitmap: Bitmap): IntArray {
        val histogram = IntArray(256)

        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                val pixel = bitmap.getPixel(x, y)
                val grayValue = Color.red(pixel)  // Bitmap zaten siyah-beyaz olduğundan sadece bir kanalı almak yeterli

                // RGB(0,0,0) rengini yoksay
                if (grayValue != 0) {
                    histogram[grayValue]++
                }
            }
        }

        return histogram
    }
}
