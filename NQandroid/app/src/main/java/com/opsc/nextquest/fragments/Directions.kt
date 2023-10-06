package com.opsc.nextquest.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.opsc.nextquest.BuildConfig
import com.opsc.nextquest.Objects.CurrentLocation
import com.opsc.nextquest.R
import com.opsc.nextquest.api.maps.models.MapData
import com.opsc.nextquest.api.weather.WeatherApi
import com.opsc.nextquest.api.weather.WeatherRetro
import com.opsc.nextquest.api.weather.models.ALocation
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale


class Directions : Fragment() {

    private lateinit var googleMap: GoogleMap
    private lateinit var locationManager: LocationManager
    private val permissionId = 2
    private lateinit var locationListener: LocationListener
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    var data:MapData= MapData()
    var lineoption = PolylineOptions()
    val directionsModal = DirectionModal()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?, ): View?
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_directions, container, false)
    }


    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationManager=requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                // This method will be called whenever the location of the user changes
                // You can perform actions based on the new location here
                getLocation()
                currentLocal()

            }
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                Log.d("testing","Status change")
            }


        }
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment_dir) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 20.0f, locationListener)
            val directionsModal = DirectionModal()
            directionsModal.directions = data// Set your MapData here
            googleMap.addPolyline(lineoption)
            directionsModal.show(parentFragmentManager, DirectionModal.TAG)
        }
    }
    override fun onStop() {
        super.onStop()
        locationManager.removeUpdates(locationListener)
    }

    @SuppressLint("MissingPermission")
    private fun currentLocal()
    {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment_dir) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            // Enable user's location
            googleMap.uiSettings.isMyLocationButtonEnabled = false
            if (checkPermissions()) {
                googleMap.isMyLocationEnabled = true
            }

            // Move camera to user's current location
            mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                val location: Location? = task.result
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15F))
                }
            }
        }
    }





    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        CurrentLocation.LatLng= LatLng(location.latitude,location.longitude)
                        CurrentLocation.lat=location.latitude
                        CurrentLocation.lng=location.longitude
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }


    private fun isLocationEnabled(): Boolean {

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean
    {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }


    private fun requestPermissions()
    {
        ActivityCompat.requestPermissions(requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
    {
        if (requestCode == permissionId)
        {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED))
            {
                getLocation()
            }
        }
    }



}