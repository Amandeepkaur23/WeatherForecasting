package com.example.weatherforecasting.utils

sealed class NetworkResults<T>(val data: T? = null, val message: String? = null) {
    class Loading<T>: NetworkResults<T>()

    class Success<T>(data: T): NetworkResults<T>(data)

    class Error<T>(message: String?, data: T? = null): NetworkResults<T>(data, message)
}