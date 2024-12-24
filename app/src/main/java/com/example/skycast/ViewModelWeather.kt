package com.example.skycast

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skycast.api.Constant
import com.example.skycast.api.InstanceRetrofit
import com.example.skycast.api.NetworkResponse
import com.example.skycast.api.WeatherModel
import kotlinx.coroutines.launch
import okhttp3.Response

class ViewModelWeather : ViewModel() {

    private val weatherApi = InstanceRetrofit.weatherApi
    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherModel>>()
    val weatherResult : LiveData<NetworkResponse<WeatherModel>> = _weatherResult

    fun getData(city : String){

        _weatherResult.value = NetworkResponse.Loading

        viewModelScope.launch {
            try {
                val response = weatherApi.getWeather(Constant.apiKey, city)
                if (response.isSuccessful){
                    response.body()?.let {
                        _weatherResult.value = NetworkResponse.Success(it)
                    }
                }else{
                    _weatherResult.value = NetworkResponse.Error("No data found")
                }
            }
            catch (e : Exception){
                _weatherResult.value = NetworkResponse.Error("No data found")
            }

        }



    }
}