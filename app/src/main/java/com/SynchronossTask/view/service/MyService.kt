package com.SynchronossTask.view.service

import android.app.*
import android.app.AlarmManager.ELAPSED_REALTIME
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.SystemClock.elapsedRealtime
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import com.SynchronossTask.R
import com.SynchronossTask.utils.MyPreference
import com.SynchronossTask.utils.PrefConstants
import com.SynchronossTask.model.LatLong
import com.SynchronossTask.view.activity.OpenWeatherActivity


class MyService : Service() {

    private var mLocationManager: LocationManager? = null
    var locationUpdate: Location? = null
    private var notifManager: NotificationManager? = null
    private lateinit var appPreference: MyPreference
    private lateinit var handler: Handler

    //inner class to get the location updates
    inner class LocationListener(provider: String) : android.location.LocationListener {
        var mLastLocation: Location
        override fun onLocationChanged(location: Location) {
            Log.e(TAG, "onLocationChanged: $location")
            mLastLocation.set(location)
            locationUpdate = location
        }

        override fun onProviderDisabled(provider: String) {
            Log.e(TAG, "onProviderDisabled: $provider")
        }

        override fun onProviderEnabled(provider: String) {
            Log.e(TAG, "onProviderEnabled: $provider")
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            Log.e(TAG, "onStatusChanged: $provider")
        }

        init {
            Log.e(TAG, "LocationListener $provider")
            mLastLocation = Location(provider)
        }
    }

    private var mLocationListeners = arrayOf<LocationListener>(
        LocationListener(LocationManager.GPS_PROVIDER),
        LocationListener(LocationManager.NETWORK_PROVIDER)
    )

    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand")
        val notificationIntent = Intent(this, OpenWeatherActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this, 0,
            notificationIntent, 0
        )
        //Assign Channel Id for groups and individual notification from OREO Version
        val notification: Notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ONE_ID,
                CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.setLightColor(Color.RED)
            notificationChannel.setShowBadge(true)
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC)
            manager.createNotificationChannel(notificationChannel)
            NotificationCompat.Builder(this, CHANNEL_ONE_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Weather Update")
                .setContentText("Location Sync Enabled")
                .setContentIntent(pendingIntent).build()
        } else {
            NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Nova")
                .setContentText("Location Track Enabled")
                .setContentIntent(pendingIntent).build()
        }
        startForeground(
            101,
            notification
        )
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    //Send your notifications to the NotificationManager system service//
    private val manager: NotificationManager
        private get() {
            if (notifManager == null) {
                notifManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            }
            return notifManager as NotificationManager
        }

    override fun onCreate() {
        appPreference = MyPreference(this)
        initializeLocationManager()

        // Get the location updates through the network or the GPS
        try {
            mLocationManager?.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL.toLong(), LOCATION_DISTANCE,
                mLocationListeners[1]
            )
        } catch (ex: SecurityException) {
            Log.i(TAG, "fail to request location update, ignore", ex)
        } catch (ex: IllegalArgumentException) {
            Log.d(TAG, "network provider does not exist, " + ex.message)
        }
        try {
            mLocationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, LOCATION_INTERVAL.toLong(), LOCATION_DISTANCE,
                mLocationListeners[0]
            )
        } catch (ex: SecurityException) {
            Log.i(TAG, "fail to request location update, ignore", ex)
        } catch (ex: IllegalArgumentException) {
            Log.d(TAG, "gps provider does not exist " + ex.message)
        }
        //Start the timer  for the duration
        startTimer()
    }

    private fun startTimer() {
        handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (locationUpdate != null) {
                    updateLocation()
                }
                handler.postDelayed(this, 7200000)
            }
        }, 0)
    }

    // Method to pass the data to Activity on time lapse
    private fun updateLocation() {
        val latLng =
            LatLong(locationUpdate?.latitude.toString(), locationUpdate?.longitude.toString())
        val gson = Gson()
        val locationJson: String = gson.toJson(latLng)
        appPreference.putString(PrefConstants.location, locationJson)
        sendDataToActivity(locationJson)

    }


    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        if (mLocationManager != null) {
            for (i in mLocationListeners.indices) {
                try {
                    mLocationManager!!.removeUpdates(mLocationListeners[i])
                } catch (ex: Exception) {
                    Log.i(TAG, "fail to remove location listeners, ignore", ex)
                }
            }
        }
    }


    private fun initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager")
        if (mLocationManager == null) {
            mLocationManager =
                applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager
        }
    }

    private fun sendDataToActivity(dataCahe: String) {
        val sendLevel = Intent()
        sendLevel.action = "UPDATE_WEATHER"
        sendLevel.putExtra("MESSAGE", "update")

        sendLevel.putExtra("DATA", dataCahe)
        sendBroadcast(sendLevel)
    }
    override fun onTaskRemoved(rootIntent: Intent?) {
        val restartServiceIntent = Intent(applicationContext, this.javaClass)
        val restartServicePendingIntent = PendingIntent.getService(
            applicationContext, 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT
        )
        val alarmService = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmService[ELAPSED_REALTIME, elapsedRealtime() + 1000] =
            restartServicePendingIntent
        super.onTaskRemoved(rootIntent)
    }
    companion object {
        private const val TAG = "SERVICETEST"
        private const val LOCATION_INTERVAL = 1000
        private const val LOCATION_DISTANCE = 0.0f

        //Set the channel’s ID//
        const val CHANNEL_ONE_ID = "com.synchronoss.openweather.ONE"

        //Set the channel’s user-visible name//
        const val CHANNEL_ONE_NAME = "Track Notification"
    }

}