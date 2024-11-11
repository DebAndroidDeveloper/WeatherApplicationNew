package com.mycompany.weatherapplication.network

import com.mycompany.weatherapplication.model.WeatherData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Defines interface to request "Weather" data from server.
 */
interface WeatherService {

    /**
     * Method to get Weather Data from server.
     * @param query - Weather Data query/city name.
     * @param unit - temperature unit
     * @param appId = application id
     * @return [WeatherData] - Weather Details Data.
     */
    @GET("weather?")
    suspend fun getWeatherData(@Query("q")  query : String, @Query("units")  unit : String,
    @Query("appid") appId : String) : Response<WeatherData>
}