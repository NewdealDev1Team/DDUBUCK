package com.mapo.ddubuck.home

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import android.text.format.DateUtils
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.mapo.ddubuck.MainActivity
import com.mapo.ddubuck.R
import com.naver.maps.geometry.LatLng
import java.util.*
import kotlin.concurrent.timer

class HomeMapService : LifecycleService() {

    private var isFirstRun = true
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var timer: Timer
    private var walkTime : Long = 0
    companion object {
        val isTracking = MutableLiveData<Boolean>()
        const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
        const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
        const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
        const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"

        const val LOCATION_UPDATE_INTERVAL = 5000L
        const val FASTEST_LOCATION_INTERVAL = 2000L

        const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
        const val NOTIFICATION_CHANNEL_NAME = "Tracking"
        const val NOTIFICATION_ID = 1
        val currentLocation = MutableLiveData<Location>()
        val notification = MutableLiveData<Notification>()
    }

    private fun postInitialValues() {
        isTracking.postValue(false)
    }

    override fun onCreate() {
        super.onCreate()
        postInitialValues()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        isTracking.observe(this, Observer {
            updateLocationTracking(it)
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if(isFirstRun) {
                        startForegroundService()
                        isFirstRun = false
                    } else {
                        Log.d("HomeMapService","Resuming service...")
                    }
                    timer = timer(period = 1000) {
                        walkTime++
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    Log.d("HomeMapService","Paused service")
                }
                ACTION_STOP_SERVICE -> {
                    isTracking.postValue(false)
                    Log.d("HomeMapService","Stopped service")
                    timer.cancel()
                    walkTime = 0
                    stopSelf()
                }
                else -> Log.d("HomeMapService","else, $it")
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if(isTracking) {
            if(TrackingUtility.hasLocationPermissions(this)) {
                val request = LocationRequest.create().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = FASTEST_LOCATION_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            if(isTracking.value!!) {
                result?.locations?.let { locations ->
                    for(location in locations) {
                        currentLocation.value = location
                    }
                }
            }
        }
    }

    private fun startForegroundService() {
        isTracking.postValue(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("산책 진행중")
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
        notification.observe(this, {
            startForeground(NOTIFICATION_ID, it)
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }
}

object TrackingUtility {

    fun hasLocationPermissions(context: Context) =
        //TODO CHECK PERMISSION
        if(context!=null) {
            true
        } else {
            false
        }
}