package com.mycompany.weatherapplication.model

data class WeatherData (
    var weather: List<Weather>? = null,
    var base: String? = null,
    var main: Main? = null,
    var visibility: Int? = null,
    var wind: Wind? = null,
    var dt: Long? = null,
    var sys: Sys? = null,
    var id: Int? = null,
    var name: String? = null,
    var cod: Int? = null
)