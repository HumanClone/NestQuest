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
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.tabs.TabLayout
import com.opsc.nextquest.BuildConfig
import com.opsc.nextquest.Objects.CurrentLocation
import com.opsc.nextquest.R
import com.opsc.nextquest.api.ebird.eBirdApi
import com.opsc.nextquest.api.ebird.eBirdRetro
import com.opsc.nextquest.api.ebird.models.HotspotView
import com.opsc.nextquest.api.ebird.models.Hotspots
import com.opsc.nextquest.api.ebird.models.hDetails
import com.opsc.nextquest.api.weather.WeatherApi
import com.opsc.nextquest.api.weather.WeatherRetro
import com.opsc.nextquest.api.weather.models.ALocation
import com.opsc.nextquest.api.weather.models.Conditions
import com.opsc.nextquest.databinding.FragmentMapViewBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale


class MapView : Fragment() {

    private var _binding: FragmentMapViewBinding? = null
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private val locationPermissionCode = 2
    private val permissionId = 2
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var lolist: List<Address> = emptyList()
    private lateinit var alocal: ALocation
    private lateinit var conditions:Conditions
    var hotModal=HotspotsModal()
    var spots:List<HotspotView> = listOf()
    var itemModal=HotspotItem()
    var locationChanged:Boolean=true


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
        locationManager=requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        currentLocal()
        val fab = view.findViewById<FloatingActionButton>(R.id.extended_fab_loc)
        fab.setOnClickListener {
            currentLocal()

        }
        val fab2=view.findViewById<FloatingActionButton>(R.id.extended_fab_hot)
        fab2.setOnClickListener {
            val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
            mapFragment.getMapAsync { googleMap->
            hotModal.setGoogleMap(googleMap)
            }
            hotModal.show(parentFragmentManager,HotspotsModal.TAG)
        }

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                // This method will be called whenever the location of the user changes
                // You can perform actions based on the new location here
                getLocation()
                currentLocal()
                locationChanged=true
            }
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                Log.d("testing","Status change")
            }


        }
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            googleMap.setOnMarkerClickListener { marker ->
                val selectedSpot = spots.find { item -> item.latLng == marker.position }
                if (selectedSpot != null) {
                    itemModal.id = selectedSpot.locId!!
                    itemModal.setGoogleMap(googleMap)
                    itemModal.show(parentFragmentManager, HotspotItem.TAG)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.position, 15f))
                }
                true // Return true to indicate that the event is consumed.
            }
        }

    }




    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()

        if (checkPermissions()) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 20.0f, locationListener)
            Log.d("testing","started requests")
        }
    }

    override fun onStop() {
        super.onStop()
        locationManager.removeUpdates(locationListener)
    }

    @SuppressLint("MissingPermission")
    private fun currentLocal()
    {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
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
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
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
            val call:Call<List<Hotspots>?>?=ebirdapi.nbyHotspots(lolist[0].latitude.toString(),lolist[0].longitude.toString(),1.0)
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
                                distance=distance(item.locName!!,item.lat!!,item.lng!!),
                                numSpeciesAllTime = item.numSpeciesAllTime
                            )
                        }
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
                            hotModal.spots=spots.sortedBy { it.distance }

                            }


                        }
                        Log.d("testing",spots.toString())
                    }
                }

                override fun onFailure(call: Call<List<Hotspots>?>?, t: Throwable?) {
                    // displaying an error message in toast

                    Log.d("Testing","fail")
                }
            })
        }
    }




    private fun distance(name:String,lat:Double,lng:Double):Double{
        var current: Location = Location("currentLocation")
        current.latitude= CurrentLocation.lat
        current.longitude= CurrentLocation.lng
        var destination: Location = Location(name)
        destination.latitude=lat
        destination.longitude=lng
        var dist=current.distanceTo(destination)
        return dist.toDouble()
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
                        if(locationChanged)
                        {
                            getNearbyHotspots()
                            locationChanged=false;
                        }
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
                    // displaying an error message in toast

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