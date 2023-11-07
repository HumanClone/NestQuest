package com.opsc.nestquest.classes

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.location.Location
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.opsc.nestquest.Objects.UserData
import com.opsc.nestquest.R
import com.opsc.nestquest.activities.MainActivity
import com.opsc.nestquest.activities.NavigationActivity
import com.opsc.nestquest.api.ebird.models.HotspotView

//COde attribution
//author: Suresh Jairwaal
//Background location service in Android 13 with most updated libraries
//https://medium.com/@sureshjairwaal_27563/background-location-service-in-android-13-with-most-updated-libraries-a440ac8b9435

//Code Attribution
//author:TechYourChance
//Foreground Service Android 14
//https://www.youtube.com/watch?v=2x5FABUViMc
class BackgroundLocal: Service() {

    var requiredPermission = android.Manifest.permission.POST_NOTIFICATIONS
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
//    private val INTERVAL: Long = (60*1000)
//    private val FASTEST_INTERVAL: Long = (30*1000)
//    private val oneHour = (300*1000)
//    val distance=10000
    private val INTERVAL: Long = (7200*1000)
    private val FASTEST_INTERVAL: Long = (3600*1000)
    val oneHour = 7200000
    val distance=1000
    //TODO: Change for Deployment

    lateinit var mLastLocation: Location
    internal lateinit var mLocationRequest: LocationRequest
    val CHANNEL_ID = "Location"
    val CHANNEL_NAME = "Near Hotspots"
    val NOTIF_ID = 101
    val helper:DirectionHelper=DirectionHelper()

    override fun onBind(intent: Intent): IBinder? {
        // This service doesn't need to provide binding, so return null
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY // Service will be restarted if it's killed by the system
    }

    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel("Tracking Location", "Tracking", NotificationManager.IMPORTANCE_DEFAULT)

        val intent= Intent(this, MainActivity::class.java)
        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_MUTABLE)
        }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        val notification = NotificationCompat.Builder(this, "Tracking Location")
            .setContentTitle("Tracking Location")
            .setContentText("Disable the Notification Category to Stop Seeing this  Specific notification")
            .setAutoCancel(true)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.logo_round)
        startForeground(10, notification.build(), FOREGROUND_SERVICE_TYPE_LOCATION)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mLocationRequest = LocationRequest()
        startLocationUpdates()

    }

    override fun onDestroy() {
        super.onDestroy()
        stoplocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun Notif(spot: HotspotView)
    {
        val intent= Intent(this, NavigationActivity::class.java)
        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_MUTABLE)
        }

        val notif = NotificationCompat.Builder(this,CHANNEL_ID)
            .setContentTitle("Near a Hotspot")
            .setContentText("You are near a hotspot: ${spot.locName}.\n Click here to navigate to the Hotspot")
            .setSmallIcon(R.drawable.logo_round)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle())
            .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        val notifManger = NotificationManagerCompat.from(this)
        val sharedPreferences = getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE)
        val lastNotificationTime = sharedPreferences.getLong("lastNotificationTime", 0)
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastNotificationTime >= oneHour) {
            Log.d("testing","Notifing User")
            UserData.destLat=spot.latLng!!.latitude
            UserData.destLng=spot.latLng!!.longitude
            notifManger.notify(NOTIF_ID,notif)
            sharedPreferences.edit().putLong("lastNotificationTime", currentTime).apply()
        }
    }

    private fun createNotifChannel() {

        val descriptionText = "Notifications that appear when you are near a Hotspot"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
        channel.description = descriptionText
        // Register the channel with the system
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    @SuppressLint("MissingPermission")
    protected fun startLocationUpdates() {

        Log.d("testing","Start of updates on the activity")
        // Create the location request to start receiving updates

        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest!!.setInterval(INTERVAL)
        mLocationRequest!!.setFastestInterval(FASTEST_INTERVAL)

        // Create LocationSettingsRequest object using location request
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        val locationSettingsRequest = builder.build()

        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(locationSettingsRequest)


        mFusedLocationClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback,
            Looper.myLooper())
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation
            onLocationChanged(locationResult.lastLocation!!)
        }
    }

    fun onLocationChanged(location: Location) {
        // New location has now been determined
        mLastLocation = location
        UserData.lat=location.latitude
        UserData.lng=location.longitude
        Log.d("testing","Location call back ON Activity ${UserData.lat}, ${UserData.lng}")
        if(UserData.spots.size>0)
        {
            var dist= helper.distanceM(
                UserData.spots[0].locName!!,
                UserData.spots[0].latLng!!.latitude,
                UserData.spots[0].latLng!!.longitude!!
            )
            if(dist<distance)//ideally 500
            {
                var checkVal = this.checkCallingOrSelfPermission(requiredPermission)

                if(UserData.user.notifications!!&&checkVal== PackageManager.PERMISSION_GRANTED ) {
                    createNotifChannel()
                    Notif(UserData.spots[0])
                }
            }
        }
        else
        {
            Log.d("testing",UserData.spots.toString())
        }

    }

    private fun stoplocationUpdates() {
        mFusedLocationClient!!.removeLocationUpdates(mLocationCallback)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(10)
        Log.d("testing","Background stopped")
    }

    private fun stop(){
        stopForeground(true)
        stopSelf()
    }



}