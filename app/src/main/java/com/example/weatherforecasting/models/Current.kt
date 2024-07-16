package com.example.weatherforecasting.models

data class Current(
    val interval: Int,
    val is_day: Int,
    val temperature_2m: Double,
    val time: String
)