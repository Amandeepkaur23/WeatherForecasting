package com.example.weatherforecasting.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecasting.databinding.HourlyWeatherItemListBinding
import com.example.weatherforecasting.models.HourlyWeatherData

class HourlyWeatherAdapter(var hourlyWeatherData: List<HourlyWeatherData>): RecyclerView.Adapter<HourlyWeatherAdapter.HourlyViewModel>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewModel {
        val binding = HourlyWeatherItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HourlyViewModel(binding)
    }

    override fun getItemCount(): Int {
        return hourlyWeatherData.size
    }

    override fun onBindViewHolder(holder: HourlyViewModel, position: Int) {
        val hourlyData = hourlyWeatherData[position]
        hourlyData.let {
            holder.bind(it)
        }
    }

    class HourlyViewModel(private val binding: HourlyWeatherItemListBinding):
        RecyclerView.ViewHolder(binding.root){
        fun bind(hourlyWeatherData: HourlyWeatherData){
            binding.hourlyTime.text = hourlyWeatherData.time.toString()
            binding.hourlyTemp.text = hourlyWeatherData.temp.toString()
            binding.hourlyRain.text = hourlyWeatherData.rain.toString()
        }
    }
}