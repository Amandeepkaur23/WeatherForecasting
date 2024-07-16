package com.example.weatherforecasting.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecasting.databinding.HourlyWeatherItemListBinding
import com.example.weatherforecasting.models.HWeatherData
import com.example.weatherforecasting.models.Hourly

class HourlyWeatherAdapter(private var hWeatherData: HWeatherData): RecyclerView.Adapter<HourlyWeatherAdapter.HourlyViewModel>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewModel {
        val binding = HourlyWeatherItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HourlyViewModel(binding)
    }

    override fun getItemCount(): Int {
        return hWeatherData.hourly.rain.size
    }

    override fun onBindViewHolder(holder: HourlyViewModel, position: Int) {
        val hourlyData = Hourly(
            listOf(hWeatherData.hourly.rain[position]),
            listOf(hWeatherData.hourly.temperature_2m[position]),
            listOf(hWeatherData.hourly.time[position]),
        )
       holder.bind(hourlyData)
    }

    fun updateData(newData: HWeatherData) {
        hWeatherData = newData
        notifyDataSetChanged()
    }

    class HourlyViewModel(private val binding: HourlyWeatherItemListBinding):
        RecyclerView.ViewHolder(binding.root){
        fun bind(hourlyData: Hourly){
            binding.hourlyTime.text = hourlyData.time.joinToString(" ")
            binding.hourlyTemp.text = hourlyData.temperature_2m.joinToString(" ")
            val rainData = "${hourlyData.rain.joinToString(" ")}mm"
            binding.hourlyRain.text = rainData
        }
    }
}