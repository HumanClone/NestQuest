package com.opsc.nestquest.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.navigation.NavigationBarView
import com.opsc.nestquest.Objects.UserData
import com.opsc.nestquest.R
import com.opsc.nestquest.api.ebird.models.HotspotView
import com.opsc.nestquest.classes.DirectionHelper
import com.opsc.nestquest.databinding.ActivityMainBinding
import com.opsc.nestquest.fragments.MapView
import com.opsc.nestquest.fragments.Observations
import com.opsc.nestquest.fragments.Settings
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var locationManager: LocationManager
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val INTERVAL: Long = (600*1000)
    private val FASTEST_INTERVAL: Long = (300*1000)
    lateinit var mLastLocation: Location
    internal lateinit var mLocationRequest: LocationRequest
    val CHANNEL_ID = "Location"
    val CHANNEL_NAME = "Near Hotspots"
    val NOTIF_ID = 101
    val helper:DirectionHelper=DirectionHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mLocationRequest = LocationRequest()
        locationManager=this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        notifPermiss.launch(Manifest.permission.POST_NOTIFICATIONS)
        val navView: BottomNavigationView = binding.navView
        navView.selectedItemId = R.id.navigation_MapView
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_Observation -> {
                    // Respond to navigation item 1 click
                    loadFrag(Observations())
                    true
                }

                R.id.navigation_Settings -> {
                    // Respond to navigation item 2 click
                    loadFrag(Settings())
                    true
                }

                R.id.navigation_MapView -> {
                    // Respond to navigation item 2 click
                    loadFrag(MapView())
                    true
                }

                else -> false
            }
        }




    }

    private fun loadFrag(fragment: Fragment)
    {
        val fragmentManager: FragmentManager =supportFragmentManager
        val fragmentTransaction=fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment_activity_main,fragment).commit()
    }


    @SuppressLint("MissingPermission")
    private fun Notif(spot:HotspotView)
    {
        val intent= Intent(this,NavigationActivity::class.java)
        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_MUTABLE)
        }

        val notif = NotificationCompat.Builder(this,CHANNEL_ID)
            .setContentTitle("Near a Hotspot")
            .setContentText("You are near a hotspot: ${spot.locName}")
            .setSmallIcon(R.drawable.logo)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        val notifManger = NotificationManagerCompat.from(this)
        notifManger.notify(NOTIF_ID,notif)
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
    companion object {
        const  val NOTIF_ID = 101
        const val CHANNEL_ID = "Location"
        const val CHANNEL_NAME = "Near Hotspots"
    }


    @SuppressLint("MissingPermission")
    val notifPermiss = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permissions ->
          if(permissions)
          {
              createNotifChannel()
              getLocation()
              if(UserData.user.darkTheme!!)
              {
                  startLocationUpdates()
              }
              loadFrag(MapView())

          }
        else{
            Perm()
          }

    }

    private fun Perm()
    {
        notifPermiss.launch(Manifest.permission.POST_NOTIFICATIONS)
    }


    @SuppressLint("MissingPermission")
    protected fun startLocationUpdates() {

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
            // do work here
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
        if(UserData.spots.size>0) {
           var dist= helper.distanceM(
                UserData.spots[0].locName!!,
                UserData.spots[0].latLng!!.latitude,
                UserData.spots[0].latLng!!.longitude!!
            )

            if(dist<500)
            {
                Notif(UserData.spots[0])
            }
        }



    }

    private fun stoplocationUpdates() {
        mFusedLocationClient!!.removeLocationUpdates(mLocationCallback)
        Log.d("testing","Background stopped")
    }

    val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
                getLocation()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
                getLocation()
            } else -> {
            getLocation()
        }
        }
    }


    //Code Attributes
    //https://techpassmaster.com/get-current-location-in-android-studio-using-kotlin/
    //Get Current Location in Android Studio using Kotlin
    //arthor:Techpass Master
    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions())
        {
            if (isLocationEnabled())
            {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        UserData.lat=location.latitude
                        UserData.lng=location.longitude

                        startLocationUpdates()
                    }
                }
            }
            else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }
        else
        {
            Log.d("testing","Request")
            locationPermissionRequest.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION))


        }
    }


    private fun isLocationEnabled(): Boolean {

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean
    {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        stoplocationUpdates()
    }

}