package com.mycompany.weatherapplication.network

import com.mycompany.weatherapplication.model.WeatherData

/**
 * Repository Interface for Weather Details Screen.
 */
interface WeatherRepository {

    /**
     * Method to get Weather Data.
     * @return [WeatherData]
     */
    suspend fun getWeatherData(query: String) : Result<WeatherData>
}