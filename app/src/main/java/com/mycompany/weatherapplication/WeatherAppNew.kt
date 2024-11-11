package com.mycompany.weatherapplication

import android.app.Application
import com.mycompany.weatherapplication.viewmodel.WeatherDataViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class WeatherAppNew : Application() {
    private val appModule = module {
        viewModel { WeatherDataViewModel(applicationContext.getString(R.string.open_weather_api_key)) }
    }
    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidContext(this@WeatherAppNew)
            modules(appModule)
        }
    }
}