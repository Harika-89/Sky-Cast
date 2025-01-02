package com.example.skycast

import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skycast.api.Constant
import com.example.skycast.api.InstanceRetrofit
import com.example.skycast.api.NetworkResponse
import com.example.skycast.api.WeatherModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

class ViewModelWeather : ViewModel() {

    private val weatherApi = InstanceRetrofit.weatherApi
    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherModel>>()
    val weatherResult: LiveData<NetworkResponse<WeatherModel>> = _weatherResult

    fun getData(city: String) {
        _weatherResult.value = NetworkResponse.Loading
        viewModelScope.launch {
            try {
                val response = weatherApi.getWeather(Constant.apiKey, city)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _weatherResult.value = NetworkResponse.Success(it)
                    }
                } else {
                    _weatherResult.value = NetworkResponse.Error("No data found")
                }
            } catch (e: Exception) {
                _weatherResult.value = NetworkResponse.Error("No data found")
            }
        }
    }

    fun fetchCurrentLocationWeather(context: Context) {
        _weatherResult.value = NetworkResponse.Loading
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    fetchWeatherByCoordinates(location.latitude, location.longitude)
                } else {
                    _weatherResult.value = NetworkResponse.Error("Unable to get location")
                }
            }.addOnFailureListener {
                _weatherResult.value = NetworkResponse.Error("Failed to get location")
            }
        } catch (e: SecurityException) {
            _weatherResult.value = NetworkResponse.Error("Location permission not granted")
        }
    }

    private fun fetchWeatherByCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val response = weatherApi.getWeather(Constant.apiKey, "$lat,$lon")
                if (response.isSuccessful) {
                    response.body()?.let {
                        _weatherResult.value = NetworkResponse.Success(it)
                    }
                } else {
                    _weatherResult.value = NetworkResponse.Error("No data found")
                }
            } catch (e: Exception) {
                _weatherResult.value = NetworkResponse.Error("Error fetching weather data")
            }
        }
    }
}