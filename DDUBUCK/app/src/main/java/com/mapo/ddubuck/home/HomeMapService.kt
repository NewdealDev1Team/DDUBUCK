package com.mapo.ddubuck.home

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.mapo.ddubuck.MainActivity
import com.mapo.ddubuck.R

@RequiresApi(Build.VERSION_CODES.O)
class HomeMapService : Service() {

    companion object {
        const val TAG = "DDUBUCKLocationService"
        const val LOCATION_INTERVAL = 1000
        const val LOCATION_DISTANCE = 10f
        private const val NOTIFICATION_NORMAL = 1
        private const val CHANNEL_ID = "my_channel"
        private const val CHANNEL_NAME = "default"
        private const val CHANNEL_DESCRIPTION = "This is default notification channel"
    }

    private val notificationManager
        get() = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    var mLocationManager: LocationManager? = null



    inner class LocationListener(provider: String) : android.location.LocationListener {
        private var mLastLocation: Location
        override fun onLocationChanged(location: Location) {
            Log.e(TAG, "onLocationChanged: $location")
            mLastLocation.set(location)
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

    var mLocationListeners = arrayOf(
        LocationListener(LocationManager.PASSIVE_PROVIDER)
    )

    override fun onBind(arg0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand")
        super.onStartCommand(intent, flags, startId)
        notificationManager.notify(NOTIFICATION_NORMAL, createCompleteNotification())
        return START_STICKY
    }

    override fun onCreate() {
        Log.e(TAG, "onCreate")
        registerDefaultNotificationChannel()
        initializeLocationManager()
        try {
            mLocationManager!!.requestLocationUpdates(
                LocationManager.PASSIVE_PROVIDER,
                LOCATION_INTERVAL.toLong(),
                LOCATION_DISTANCE,
                mLocationListeners[0]
            )
        } catch (ex: SecurityException) {
            Log.i(TAG, "fail to request location update, ignore", ex)
        } catch (ex: IllegalArgumentException) {
            Log.d(TAG, "network provider does not exist, " + ex.message)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (mLocationManager != null) {
            for (i in mLocationListeners.indices) {
                try {
                    if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    mLocationManager!!.removeUpdates(mLocationListeners[i])
                } catch (ex: Exception) {
                    Log.i(TAG, "fail to remove location listener, ignore", ex)
                }
            }
        }
    }

    private fun initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager - LOCATION_INTERVAL: $LOCATION_INTERVAL LOCATION_DISTANCE: $LOCATION_DISTANCE")
        if (mLocationManager == null) {
            mLocationManager =
                applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
    }

    private fun registerDefaultNotificationChannel() {
        notificationManager.createNotificationChannel(createDefaultNotificationChannel())
    }

    private fun createDefaultNotificationChannel() =
        NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW).apply {
            description = CHANNEL_DESCRIPTION
            this.setShowBadge(true)
            this.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
    }

    private fun createCompleteNotification() = NotificationCompat.Builder(this, CHANNEL_ID).apply {
        setContentTitle("Download complete!")
        setContentText("Nice 🚀")
        setSmallIcon(R.drawable.ic_launcher_foreground)
        setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        setContentIntent(
            PendingIntent.getActivity(
                this@HomeMapService, 0, Intent(this@HomeMapService, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                }, 0
            )
        )
    }.build()
}