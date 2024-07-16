package com.example.weatherforecasting.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecasting.databinding.DailyWeatherItemListBinding
import com.example.weatherforecasting.models.DWeatherData
import com.example.weatherforecasting.models.Daily

class DailyWeatherAdapter(private var dWeatherData: DWeatherData): RecyclerView.Adapter<DailyWeatherAdapter.DailyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val binding = DailyWeatherItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DailyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dWeatherData.daily.time.size
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val dailyData = Daily(
            listOf(dWeatherData.daily.rain_sum[position]),
            listOf(dWeatherData.daily.temperature_2m_max[position]),
            listOf(dWeatherData.daily.temperature_2m_min[position]),
            listOf(dWeatherData.daily.time[position])
        )
        holder.bind(dailyData)
    }
    fun updateData(newData: DWeatherData) {
        dWeatherData = newData
        notifyDataSetChanged()
    }

    class DailyViewHolder(private val binding: DailyWeatherItemListBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(dailyData: Daily) {
            // Bind your data to the ViewHolder's views
            binding.daily.text = dailyData.time.joinToString(" ")
            val tempData = "${dailyData.temperature_2m_min.joinToString(" ")}\\${dailyData.temperature_2m_max.joinToString(" ")}"
            binding.dailyTemp.text = tempData
            val rainData = "${dailyData.rain_sum.joinToString(" ")}mm"
            binding.dailyRain.text = rainData
        }
    }


}