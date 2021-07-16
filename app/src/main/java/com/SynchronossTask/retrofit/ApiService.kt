package com.SynchronossTask.retrofit

import android.content.Context
import com.SynchronossTask.model.WeatherResponse
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Query

class ApiService(context: Context) {

    private val BASE_URL = "http://api.openweathermap.org/"


    private val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(ApiInterface::class.java)


    fun getWeatherData(
        @Query("lat") lat: String?,
        @Query("lon") lon: String?,
        @Query("APPID") app_id: String?
    ): Single<WeatherResponse> {
        return api.getWeatherData(lat, lon, app_id)
    }
}