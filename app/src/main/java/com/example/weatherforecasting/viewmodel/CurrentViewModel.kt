package com.example.weatherforecasting.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecasting.models.Current
import com.example.weatherforecasting.models.CurrentWeatherData
import com.example.weatherforecasting.models.DWeatherData
import com.example.weatherforecasting.models.HWeatherData
import com.example.weatherforecasting.repository.WeatherRepository
import com.example.weatherforecasting.utils.NetworkResults
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrentViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository): ViewModel() {
        val currentLiveData: LiveData<NetworkResults<CurrentWeatherData>>
            get() = weatherRepository.currentData

        val dailyLiveData: LiveData<NetworkResults<DWeatherData>>
            get() = weatherRepository.dailyData

        val hourlyLiveData: LiveData<NetworkResults<HWeatherData>>
            get() = weatherRepository.hourlyData

     fun fetchCurrentData(latitude: Double?, longitude: Double?) {
        viewModelScope.launch {
            weatherRepository.getCurrentData(latitude, longitude)
        }
    }
    fun fetchDailyData(latitude: Double?, longitude: Double?){
        viewModelScope.launch {
            weatherRepository.getDailyData(latitude, longitude)
        }
    }
    fun fetchHourlyData(latitude: Double?, longitude: Double?){
        viewModelScope.launch {
            weatherRepository.getHourlyData(latitude, longitude)
        }
    }
}