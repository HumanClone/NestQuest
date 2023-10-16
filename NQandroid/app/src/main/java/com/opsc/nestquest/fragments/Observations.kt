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
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.opsc.nestquest.Objects.UserData
import com.opsc.nestquest.R
import com.opsc.nestquest.api.nestquest.adapters.observationAdapter
import com.opsc.nestquest.api.nestquest.models.Observation
import java.util.Locale





class Observations : Fragment() {
    private lateinit var recycler:RecyclerView
    private lateinit var locationManager: LocationManager
    private val permissionId = 2
    lateinit var address:String
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_observations, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationManager=requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        recycler=view.findViewById(R.id.recycler_observation)
        genRecycleView(UserData.observations,recycler)
        getLocation()

        val fab2=view.findViewById<ExtendedFloatingActionButton>(R.id.extended_fab_hot)
        fab2.setOnClickListener {
            getLocation()
            val co= CreateObservation()
            co.recycler=recycler
            co.address=address
            if(co.isAdded)
            {
                co.dismiss()
                co.show(parentFragmentManager,CreateObservation.TAG)
            }
            else
            {
                co.show(parentFragmentManager,CreateObservation.TAG)
            }

        }
    }
    private fun genRecycleView(data:List<Observation>, recyclerView: RecyclerView)
    {
        activity?.runOnUiThread(Runnable {
            recyclerView.layoutManager = LinearLayoutManager(context)
            val adapter = observationAdapter(data)
            recyclerView.adapter = adapter
            adapter.setOnClickListener(object : observationAdapter.OnClickListener {
                override fun onClick(position: Int, model: Observation) {

                    val ob=ObservationView()
                    ob.ob=model
                    if (ob.isAdded)
                    {
                        ob.dismiss()
                        ob.show(parentFragmentManager,ObservationView.TAG)
                    }
                    else
                    {
                        ob.show(parentFragmentManager,ObservationView.TAG)
                    }

                }
            })
        })
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        var addressList: List<Address?>? = null
                        addressList = Geocoder(requireContext(), Locale.ENGLISH).getFromLocation(location.latitude, location.longitude, 1)
                        Log.d("testing",addressList.toString())
                        Log.d("testing",addressList!![0].getAddressLine(0).toString())
                        address=addressList!![0].getAddressLine(0).toString()
                        UserData.lat=location.latitude
                        UserData.lng=location.longitude
                        Log.d("testing","${UserData.lat},${UserData.lng}")
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

}
