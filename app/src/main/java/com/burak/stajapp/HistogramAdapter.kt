package com.burak.stajapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistogramAdapter(
    private val histogramDataList: List<IntArray>,
    private val histogramData9: IntArray
) : RecyclerView.Adapter<HistogramAdapter.HistogramViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistogramViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_histogram, parent, false)
        return HistogramViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistogramViewHolder, position: Int) {
        val histogramData = histogramDataList[position]
        holder.bind(histogramData, histogramData9)
    }

    override fun getItemCount(): Int = histogramDataList.size

    class HistogramViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        private val textViewData: TextView = itemView.findViewById(R.id.textViewData)
        private val ratioTitle: TextView = itemView.findViewById(R.id.ratioTitle)
        private val ratioData: TextView = itemView.findViewById(R.id.ratioData)

        fun bind(histogramData: IntArray, histogramData9: IntArray) {
            val adapterPosition = adapterPosition
            val binSize = Math.pow(2.0, (8 - adapterPosition).toDouble()).toInt()
            textViewTitle.text = "Bin size = $binSize"
            textViewData.text = histogramData.mapIndexed { index, value -> "Bin $index = $value" }.joinToString(", ")

            ratioTitle.text = "Ratio $binSize"
            ratioData.text = histogramData.mapIndexed { index, value ->
                val ratio = value.toDouble() / histogramData9[0].toDouble()
                String.format("Bin $index = %.10f", ratio)
            }.joinToString(", ")
        }
    }
}
