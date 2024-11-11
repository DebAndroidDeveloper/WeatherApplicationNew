package com.mycompany.weatherapplication.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycompany.weatherapplication.BuildConfig
import com.mycompany.weatherapplication.model.WeatherData
import com.mycompany.weatherapplication.network.NetworkClient
import com.mycompany.weatherapplication.network.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.mycompany.weatherapplication.network.Result
import com.mycompany.weatherapplication.network.WeatherRepositoryImpl
import com.mycompany.weatherapplication.network.WeatherService

class WeatherDataViewModel(private val appId: String) :  ViewModel() {

    private val networkClient by lazy { NetworkClient() }

    private val weatherDataRepository by lazy {
        val weatherService = networkClient.createService(WeatherService::class.java, BuildConfig.WEATHER_API_BASE_URL)
        WeatherRepositoryImpl(weatherService,"imperial",appId)
    }
    private val weatherData: MutableLiveData<WeatherData> = MutableLiveData()
    val weatherLiveData: LiveData<WeatherData>
        get() = weatherData

    private val error = MutableLiveData<String>()
    val weatherDataError: LiveData<String> get() = error

    companion object {
        private val TAG = WeatherDataViewModel::class.qualifiedName
    }

    /**
     * Load FAQ from server and display to the user.
     */
    fun fetchWeatherData(cityName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = weatherDataRepository.getWeatherData(cityName)) {
                is Result.Success -> {
                    result.data?.let { weatherInfo ->
                        weatherData.postValue(weatherInfo)
                    }
                }

                is Result.Error -> {
                    Log.e(TAG, "Failed to fetch Weather Data with error: $result")
                    error.postValue(result.errorMessage)
                }
            }
        }
    }
}