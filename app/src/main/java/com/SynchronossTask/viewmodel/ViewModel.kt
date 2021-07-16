package com.SynchronossTask.viewmodel

import android.app.Application
import android.net.wifi.WifiManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.SynchronossTask.retrofit.ApiService
import com.SynchronossTask.model.WeatherResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.http.Query


class ViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService = ApiService(context = getApplication())
    private val disposable = CompositeDisposable()
    private lateinit var wifiManager: WifiManager
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val mContext = application
    var appId = "808c61c6ba0628cc657bdf99e8e1a447"
    var lat = "35"
    var lng = "139"
    val PERMISSION_ID = 99
    val weatherResponse = MutableLiveData<WeatherResponse>()
    val apiError = MutableLiveData<Boolean>()
    val apiLoading = MutableLiveData<Boolean>()

    fun callWeatherUpdateApi(
        @Query("lat") lat: String?,
        @Query("lon") lon: String?,
        @Query("APPID") app_id: String?
    ) {


        apiLoading.value = true
        disposable.add(
            apiService.getWeatherData(lat, lon, app_id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<WeatherResponse>() {
                    override fun onSuccess(responseObj: WeatherResponse) {
                        weatherResponse.value = responseObj
                        apiError.value = false
                        apiLoading.value = false

                    }

                    override fun onError(e: Throwable) {
                        apiError.value = true
                        apiLoading.value = false
                        e.printStackTrace()
                    }

                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

}