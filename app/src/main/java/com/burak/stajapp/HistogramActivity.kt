package com.burak.stajapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
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
            displayHistogram(histogramData)
            logHistogramData(histogramData)
        }
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

    private fun displayHistogram(histogramData: IntArray) {
        val graph = findViewById<GraphView>(R.id.histogramGraph)
        val series = BarGraphSeries<DataPoint>()

        for (i in histogramData.indices) {
            series.appendData(DataPoint(i.toDouble(), histogramData[i].toDouble()), true, histogramData.size)
        }

        graph.addSeries(series)

        // Grafiği özelleştir
        series.spacing = 1
        series.color = Color.BLUE

        // Grafiğin görünümünü düzenleyin
        graph.viewport.isXAxisBoundsManual = true
        graph.viewport.setMinX(0.0)
        graph.viewport.setMaxX(255.0)
        graph.viewport.isYAxisBoundsManual = true
        graph.viewport.setMinY(0.0)
        graph.viewport.setMaxY(histogramData.maxOrNull()?.toDouble() ?: 1.0)

        graph.viewport.isScalable = true
        graph.viewport.isScrollable = true
        graph.viewport.setScalableY(true)
        graph.viewport.setScrollableY(true)

        graph.gridLabelRenderer.verticalAxisTitle = "Frequency"
        graph.gridLabelRenderer.horizontalAxisTitle = "Gray Value"
    }

    private fun logHistogramData(histogramData: IntArray) {
        for (i in histogramData.indices) {
            println("$i = ${histogramData[i]}")
        }
    }
}
