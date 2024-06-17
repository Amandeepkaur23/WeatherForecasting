package com.example.weatherforecasting.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecasting.databinding.DailyWeatherItemListBinding
import com.example.weatherforecasting.models.DailyWeatherData

class DailyWeatherAdapter(var dailyWeatherData: List<DailyWeatherData>): RecyclerView.Adapter<DailyWeatherAdapter.DailyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val binding = DailyWeatherItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DailyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dailyWeatherData.size
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val dailyWeatherData = dailyWeatherData[position]
        dailyWeatherData.let {
            holder.binds(it)
        }
    }

    class DailyViewHolder(val binding: DailyWeatherItemListBinding): RecyclerView.ViewHolder(binding.root){
        fun binds(dailyWeatherData: DailyWeatherData){
            binding.daily.text = dailyWeatherData.daily
            binding.dailyRain.text = dailyWeatherData.rain.toString()
            binding.dailyTemp.text = dailyWeatherData.temp.toString()
        }
    }


}