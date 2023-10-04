package com.opsc.nextquest.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.tabs.TabLayout
import com.opsc.nextquest.BuildConfig
import com.opsc.nextquest.R
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
    private val locationPermissionCode = 2
    private val permissionId = 2
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var lolist: List<Address> = emptyList()
    private lateinit var alocal: ALocation
    private lateinit var conditions:Conditions
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map_view, container, false)


    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationManager=requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        getLocation()
//        link=view.findViewById(R.id.icon)
//        link.setOnClickListener {
//            val i = Intent(Intent.ACTION_VIEW);
//            i.setData(Uri.parse(conditions.MobileLink))
//            startActivity(i)
//
//        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
                override fun onResponse(
                    call: Call<List<Conditions>?>?,
                    response: Response<List<Conditions>?>
                ) {
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




    //https://techpassmaster.com/get-current-location-in-android-studio-using-kotlin/
    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        lolist=
                            geocoder.getFromLocation(location.latitude,location.longitude,1) as List<Address>
                        Log.d("testing","Latitude:${lolist[0].latitude}\tLongitude:${lolist[0].longitude}")
                        getLoKey()
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

    private fun checkPermissions(): Boolean {
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
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }
}