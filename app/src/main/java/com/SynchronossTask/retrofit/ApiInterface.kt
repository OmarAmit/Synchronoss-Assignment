package com.SynchronossTask.retrofit
import com.SynchronossTask.model.WeatherResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("data/2.5/weather?")
    fun getWeatherData(
        @Query("lat") lat: String?,
        @Query("lon") lon: String?,
        @Query("APPID") app_id: String?
    ): Single<WeatherResponse>

}