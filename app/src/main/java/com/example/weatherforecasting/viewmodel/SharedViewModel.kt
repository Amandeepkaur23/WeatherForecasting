package com.example.weatherforecasting.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherforecasting.models.Place


class SharedViewModel: ViewModel() {

    private val _selectedPlace = MutableLiveData<Place>()
    val selectedPlace: LiveData<Place>
        get() = _selectedPlace

    fun setSelectedPlace(place: Place){
        _selectedPlace.value = place
    }
}