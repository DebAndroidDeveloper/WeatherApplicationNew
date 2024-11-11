package com.mycompany.weatherapplication.network
import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NetworkClient {
    private val okHttpClient: OkHttpClient by lazy {
        val okHttpClientBuilder = OkHttpClient
            .Builder()
            .connectTimeout(1,TimeUnit.MINUTES)
            .readTimeout(1,TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .callTimeout(1,TimeUnit.MINUTES)
        okHttpClientBuilder.build()
    }

    private val gson: Gson by lazy {
        Gson()
            .newBuilder()
            .setPrettyPrinting()
            .create()
    }

    fun <T> createService(serviceClass: Class<T>, url : String) : T {
        return Retrofit.
        Builder()
            .baseUrl("")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(serviceClass)
    }
}