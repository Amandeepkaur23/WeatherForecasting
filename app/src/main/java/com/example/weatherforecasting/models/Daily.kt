package com.example.weatherforecasting.models

data class Daily(
    val rain_sum: List<Double>,
    val temperature_2m_max: List<Double>,
    val temperature_2m_min: List<Double>,
    val time: List<String>
)