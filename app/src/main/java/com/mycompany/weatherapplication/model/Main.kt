package com.mycompany.weatherapplication.model

data class Main (
    var temp: Double? = null,
    var pressure: Int? = null,
    var humidity: Int? = null,
    var tempMin: Double? = null,
    var tempMax: Double? = null
)