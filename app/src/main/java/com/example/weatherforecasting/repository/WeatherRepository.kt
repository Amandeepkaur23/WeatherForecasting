package com.example.weatherforecasting.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherforecasting.api.WeatherAPI
import com.example.weatherforecasting.models.CurrentWeatherData
import com.example.weatherforecasting.models.DWeatherData
import com.example.weatherforecasting.models.HWeatherData
import com.example.weatherforecasting.utils.NetworkResults
import org.json.JSONObject
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherAPI: WeatherAPI
) {

    private val _currentData = MutableLiveData<NetworkResults<CurrentWeatherData>>()
    val currentData: LiveData<NetworkResults<CurrentWeatherData>>
        get() = _currentData

    private val _dailyData = MutableLiveData<NetworkResults<DWeatherData>>()
    val dailyData: LiveData<NetworkResults<DWeatherData>>
        get() = _dailyData

    private val _hourlyData = MutableLiveData<NetworkResults<HWeatherData>>()
    val hourlyData: LiveData<NetworkResults<HWeatherData>>
        get() = _hourlyData

    suspend fun getCurrentData(latitude: Double?, longitude: Double?){
        _currentData.postValue(NetworkResults.Loading())
        try{
            val response = weatherAPI.getCurrentWeather(latitude, longitude)
            if(response.isSuccessful && response.body() != null){
                _currentData.postValue(NetworkResults.Success(response.body()!!))
            } else if(response.errorBody() != null){
                val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
                _currentData.postValue(NetworkResults.Error(errorObj.getString("message")))
            } else{
                _currentData.postValue(NetworkResults.Error("Something went wrong"))
            }
        } catch (e: Exception){
            _currentData.postValue(NetworkResults.Error("Something went wrong"))
        }
    }

    suspend fun getDailyData(latitude: Double?, longitude: Double?){
        _currentData.postValue(NetworkResults.Loading())
        try{
            val response = weatherAPI.getDailyWeather(latitude, longitude)
            if(response.isSuccessful && response.body() != null){
                _dailyData.postValue(NetworkResults.Success(response.body()!!))
            } else if(response.errorBody() != null){
                val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
                _dailyData.postValue(NetworkResults.Error(errorObj.getString("message")))
            } else{
                _dailyData.postValue(NetworkResults.Error("Something went wrong"))
            }
        } catch (e: Exception){
            _dailyData.postValue(NetworkResults.Error("Something went wrong"))
        }
    }

    suspend fun getHourlyData(latitude: Double?, longitude: Double?){
        _currentData.postValue(NetworkResults.Loading())
        try{
            val response = weatherAPI.getHourlyWeather(latitude, longitude)
            if(response.isSuccessful && response.body() != null){
                _hourlyData.postValue(NetworkResults.Success(response.body()!!))
            } else if(response.errorBody() != null){
                val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
                _hourlyData.postValue(NetworkResults.Error(errorObj.getString("message")))
            } else{
                _hourlyData.postValue(NetworkResults.Error("Something went wrong"))
            }
        } catch (e: Exception){
            _hourlyData.postValue(NetworkResults.Error("Something went wrong"))
        }
    }


}