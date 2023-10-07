package com.opsc.nestquest.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.opsc.nestquest.BuildConfig
import com.opsc.nestquest.Objects.CurrentLocation
import com.opsc.nestquest.R
import com.opsc.nestquest.api.ebird.eBirdApi
import com.opsc.nestquest.api.ebird.eBirdRetro
import com.opsc.nestquest.api.ebird.models.HotspotView
import com.opsc.nestquest.api.ebird.models.Hotspots
import com.opsc.nestquest.api.weather.WeatherApi
import com.opsc.nestquest.api.weather.WeatherRetro
import com.opsc.nestquest.api.weather.models.ALocation
import com.opsc.nestquest.api.weather.models.Conditions
import com.opsc.nestquest.classes.DirectionHelper
import com.opsc.nestquest.databinding.FragmentMapViewBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale


class MapView : Fragment() {

    private var _binding: FragmentMapViewBinding? = null
    private lateinit var locationManager: LocationManager
    private val permissionId = 2
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var lolist: List<Address> = emptyList()
    private lateinit var alocal: ALocation
    private lateinit var conditions:Conditions
    var hotModal=HotspotsModal()
    var spots:List<HotspotView> = listOf()
    var itemModal=HotspotItem()
    val helper:DirectionHelper=DirectionHelper()
    private val INTERVAL: Long = 2000
    private val FASTEST_INTERVAL: Long = 20000
    lateinit var mLastLocation: Location
    internal lateinit var mLocationRequest: LocationRequest



    //TODO: Change Later
    var conditionsNeeded:Boolean= false
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map_view, container, false)


    }
    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        mLocationRequest = LocationRequest()
        locationManager=requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hotModal.mFusedLocationClient=mFusedLocationClient
        hotModal.mLocationCallback=mLocationCallback
        val fab = view.findViewById<FloatingActionButton>(R.id.extended_fab_loc)
        fab.setOnClickListener {
            currentLocal()

        }
        val fab2=view.findViewById<FloatingActionButton>(R.id.extended_fab_hot)
        fab2.setOnClickListener {
            if(spots.size!=0)
            {
                val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
                mapFragment.getMapAsync { googleMap->
                    hotModal.setGoogleMap(googleMap)
                }

                val existingFragment = parentFragmentManager.findFragmentByTag(HotspotsModal.TAG)
                if (existingFragment == null) {
                    hotModal.show(parentFragmentManager, HotspotsModal.TAG)
                }


            }
        }


        if(checkPermissions())
        {
            getLocation()
            currentLocal()
            val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
            mapFragment.getMapAsync { googleMap ->
                googleMap.setOnMarkerClickListener { marker ->
                    if(spots.size!=0)
                    {
                        val selectedSpot = spots.find { item -> item.latLng == marker.position }
                        if (selectedSpot != null) {

                            val existingFragment = parentFragmentManager.findFragmentByTag(HotspotItem.TAG)
                            if (existingFragment == null) {
                                itemModal.id = selectedSpot.locId!!
                                itemModal.setGoogleMap(googleMap)
                                itemModal.mFusedLocationClient=mFusedLocationClient
                                itemModal.mLocationCallback=mLocationCallback
                                itemModal.show(parentFragmentManager, HotspotItem.TAG)
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.position, 15F))
                            }
                            else
                            {
                                parentFragmentManager.beginTransaction().remove(itemModal).commit()
                            }

                        }
                    }
                    true // Return true to indicate that the event is consumed.
                }
            }
        }
        else
        {
            getLocation()
            startLocationUpdates()
        }


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

        val settingsClient = LocationServices.getSettingsClient(requireActivity())
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
        CurrentLocation.lat=location.latitude
        CurrentLocation.lng=location.longitude
        Log.d("testing","Location call back main ${CurrentLocation.lat}, ${CurrentLocation.lng}")
        currentLocal()
        getNearbyHotspots()
    }

    private fun stoplocationUpdates() {
        mFusedLocationClient!!.removeLocationUpdates(mLocationCallback)
        Log.d("testing","main stopped")
    }



    @SuppressLint("MissingPermission")
    private fun currentLocal()
    {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            // Enable user's location
            googleMap.uiSettings.isMyLocationButtonEnabled = false
            googleMap.uiSettings.isMapToolbarEnabled=true
            if (checkPermissions()) {
                googleMap.isMyLocationEnabled = true
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val latLng = LatLng(location.latitude, location.longitude)
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15F))
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun getNearbyHotspots()
    {
        val ebirdapi=eBirdRetro.getInstance().create((eBirdApi::class.java))
        //TODO: Change the distance parameter according to the settings
        GlobalScope.launch {
            val call:Call<List<Hotspots>?>?=ebirdapi.nbyHotspots(CurrentLocation.lat.toString(),CurrentLocation.lng.toString(),4.0)
            call!!.enqueue(object : Callback<List<Hotspots>?> {

                override fun onResponse(call: Call<List<Hotspots>?>?, response: Response<List<Hotspots>?>)
                {
                    if (response.isSuccessful())
                    {
                        Log.d("testing",response.body()!!.toString())
                        val hotspots=response.body()!!
                        spots = hotspots.map { item ->
                            HotspotView(
                                locId = item.locId,
                                locName = item.locName,
                                latLng = LatLng(item.lat!!,item.lng!!),
                                distance=helper.distanceM(item.locName!!,item.lat!!,item.lng!!),
                                numSpeciesAllTime = item.numSpeciesAllTime
                            )
                        }
                        spots=spots.sortedBy{ it.distance }
                        //https://stackoverflow.com/questions/18053156/set-image-from-drawable-as-marker-in-google-map-version-2
                        val icon = resources.getDrawable(R.drawable.hot_24)
                        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
                        mapFragment.getMapAsync {googleMap->
                            spots.map { item->
                                googleMap.addMarker(
                                    MarkerOptions()
                                        .position(item.latLng!!)
                                        .title(item.locName)
                                        .snippet(item.numSpeciesAllTime.toString())
                                        .icon(BitmapDescriptorFactory.fromBitmap(icon.toBitmap(icon.intrinsicWidth, icon.intrinsicHeight, null)))
                                )
                            hotModal.spots=spots

                            }


                        }

                        Log.d("testing","sorted: "+spots.toString())
                    }
                }

                override fun onFailure(call: Call<List<Hotspots>?>?, t: Throwable?) {
                    // displaying an error message in toast

                    Log.d("Testing","fail")
                }
            })
        }
    }



    //https://techpassmaster.com/get-current-location-in-android-studio-using-kotlin/
    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        lolist=  geocoder.getFromLocation(location.latitude,location.longitude,1) as List<Address>
                        CurrentLocation.LatLng= LatLng(location.latitude,location.longitude)
                        CurrentLocation.lat=location.latitude
                        CurrentLocation.lng=location.longitude
                        Log.d("testing","Latitude:${lolist[0].latitude}\tLongitude:${lolist[0].longitude}")
                        if(conditionsNeeded)
                        {
                            getLoKey()
                            conditionsNeeded=false;
                        }
                        getNearbyHotspots()
                        startLocationUpdates()
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
        Log.d("testing",requestCode.toString())
        if (requestCode == permissionId)
        {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED))
            {
                getLocation()
            }
        }
    }

    private fun getLoKey()
    {
        val weatherApiKey = BuildConfig.WEATHER_API_KEY
        val weatherApi = WeatherRetro.getInstance().create(WeatherApi::class.java)
        GlobalScope.launch {
            val call: Call<ALocation?>? = weatherApi.getKey(weatherApiKey,"${lolist[0].latitude},${lolist[0].longitude}")
            //val call: Call<ALocation?>? = weatherApi.getKey()
            call!!.enqueue(object : Callback<ALocation?> {
                override fun onResponse(
                    call: Call<ALocation?>?,
                    response: Response<ALocation?>
                ) {
                    if (response.isSuccessful())
                    {
                        Log.d("testing",response.body()!!.toString())
                        alocal=response.body()!!

                        getConditions()

                    }
                }

                override fun onFailure(call: Call<ALocation?>?, t: Throwable?) {

                    Log.d("Testing","fail")
                }
            })
        }
    }


    private fun getConditions() {
        val weatherApiKey = BuildConfig.WEATHER_API_KEY
        val weatherApi = WeatherRetro.getInstance().create(WeatherApi::class.java)

        GlobalScope.launch {
            val call: Call<List<Conditions>?>? = weatherApi.getConditions(alocal.Key.toString(), weatherApiKey)

            call!!.enqueue(object : Callback<List<Conditions>?> {
                override fun onResponse(call: Call<List<Conditions>?>?, response: Response<List<Conditions>?>)
                {
                    if (response.isSuccessful) {
                        val conditionsList = response.body()
                        if (conditionsList != null && conditionsList.isNotEmpty()) {
                            val conditions =
                                conditionsList[0] // Assuming the response contains multiple conditions
                            Log.d("testing", conditions.toString())

                        } else {
                            Log.d("testing", "Empty or null response")
                        }
                    } else {
                        Log.d("testing", "Response not successful: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<List<Conditions>?>?, t: Throwable?) {
                    Log.d("Testing", "fail\t${t?.message}")
                }
            })

        }
    }


}