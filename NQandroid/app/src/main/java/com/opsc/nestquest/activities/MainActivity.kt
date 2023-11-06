package com.opsc.nestquest.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
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
import android.view.View
import android.view.Window
import android.view.WindowManager
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.opsc.nestquest.Objects.UserData
import com.opsc.nestquest.R
import com.opsc.nestquest.api.ebird.models.HotspotView
import com.opsc.nestquest.classes.BackgroundLocal
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
    val helper:DirectionHelper=DirectionHelper()
    var firstTime:Boolean=true
    var permis:Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if(currentUser==null)
        {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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
    val notifPermiss = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permissions ->

        permis=permissions
        getLocation()

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
            AlertDialog.Builder(this)
                .setTitle("Location Permissions Required")
                .setMessage("Please enable location permissions to use this app.")
                .setPositiveButton("OK") { dialog, _ ->
                    // You can optionally add code here to take the user to the settings screen to enable permissions.
                    val intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                    dialog.dismiss()

                }
                .show()

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

                        if(firstTime)
                        {
                            firstTime=false
                            if(permis)
                            {
                                val serviceIntent = Intent(this, BackgroundLocal::class.java)
                                startForegroundService(serviceIntent)
                            }
                            loadFrag(MapView())
                        }
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



}