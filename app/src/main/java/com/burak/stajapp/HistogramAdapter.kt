package com.burak.stajapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint

class HistogramAdapter(private val histogramDataList: List<IntArray>) :
    RecyclerView.Adapter<HistogramAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val graph: GraphView = itemView.findViewById(R.id.histogramGraph)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_histogram, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val histogramData = histogramDataList[position]

        val series = BarGraphSeries<DataPoint>()
        for (i in histogramData.indices) {
            series.appendData(DataPoint(i.toDouble(), histogramData[i].toDouble()), true, histogramData.size)
        }

        holder.graph.addSeries(series)

        // Grafiği özelleştir
        series.spacing = 1
        series.color = COLORS[position % COLORS.size]

        holder.graph.viewport.isScalable = true
        holder.graph.viewport.isScrollable = true
        holder.graph.viewport.setScalableY(true)
        holder.graph.viewport.setScrollableY(true)

        holder.graph.gridLabelRenderer.verticalAxisTitle = "Frequency"
        holder.graph.gridLabelRenderer.horizontalAxisTitle = "Gray Value"
    }

    override fun getItemCount(): Int {
        return histogramDataList.size
    }

    companion object {
        private val COLORS = listOf(
            Color.RED, Color.BLUE, Color.GREEN,
            Color.YELLOW, Color.CYAN, Color.MAGENTA,
            Color.GRAY, Color.BLACK, Color.WHITE
        )
    }
}
