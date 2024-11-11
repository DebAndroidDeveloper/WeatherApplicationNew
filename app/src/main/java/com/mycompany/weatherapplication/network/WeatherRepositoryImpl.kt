package com.mycompany.weatherapplication.network

import com.mycompany.weatherapplication.model.WeatherData

class WeatherRepositoryImpl(
    private val weatherService: WeatherService,
    private val unit: String,
    private val appId: String): WeatherRepository {

    override suspend fun getWeatherData(query: String): Result<WeatherData> {
        return NetworkExecutor.execute { weatherService.getWeatherData(query,unit,appId) }
    }
}