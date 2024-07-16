package com.example.weatherforecasting.api

import com.example.weatherforecasting.models.CurrentWeatherData
import com.example.weatherforecasting.models.DWeatherData
import com.example.weatherforecasting.models.Daily
import com.example.weatherforecasting.models.HWeatherData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {
    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") latitude: Double?,
        @Query("longitude") longitude: Double?,
        @Query("current") current: String = "temperature_2m,is_day"
    ): Response<CurrentWeatherData>

    @GET("v1/forecast")
    suspend fun getDailyWeather(
        @Query("latitude") latitude: Double?,
        @Query("longitude") longitude: Double?,
        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min,rain_sum"
    ): Response<DWeatherData>

    @GET("v1/forecast")
    suspend fun getHourlyWeather(
        @Query("latitude") latitude: Double?,
        @Query("longitude") longitude: Double?,
        @Query("hourly") hourly: String = "temperature_2m,rain"
    ): Response<HWeatherData>
}

//https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&current=temperature_2m,is_day
//https://api.open-meteo.com/v1/forecast?latitude=31.29&longitude=75.98&daily=temperature_2m_max,temperature_2m_min,rain_sum&timezone=auto
//https://api.open-meteo.com/v1/forecast?latitude=31.29&longitude=75.98&hourly=temperature_2m,rain&timezone=auto