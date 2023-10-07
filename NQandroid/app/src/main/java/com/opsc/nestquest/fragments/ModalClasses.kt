package com.opsc.nestquest.fragments

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.opsc.nestquest.Objects.UserData
import com.opsc.nestquest.R
import com.opsc.nestquest.activities.NavigationActivity
import com.opsc.nestquest.api.ebird.adapters.HotspotListAdapter
import com.opsc.nestquest.api.ebird.eBirdApi
import com.opsc.nestquest.api.ebird.eBirdRetro
import com.opsc.nestquest.api.ebird.models.HotspotView
import com.opsc.nestquest.api.ebird.models.hDetails
import com.opsc.nestquest.classes.DirectionHelper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HotspotsModal: BottomSheetDialogFragment()
{
    lateinit var recycler: RecyclerView
    var spots:List<HotspotView> = listOf()
    var hot=HotspotItem()
    private lateinit var googleMap: GoogleMap
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var mLocationCallback  : LocationCallback

    fun setGoogleMap(map: GoogleMap) {
        googleMap = map
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {

        return inflater.inflate(R.layout.hotspotlist_modal_view, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler=view.findViewById(R.id.modal_recycler)
        genRecycleView(spots,recycler)


    }

    private fun genRecycleView(data:List<HotspotView>, recyclerView: RecyclerView)
    {
        activity?.runOnUiThread(Runnable {
            recyclerView.layoutManager = LinearLayoutManager(context)
            val adapter = HotspotListAdapter(data)
            recyclerView.adapter = adapter
            adapter.setOnClickListener(object : HotspotListAdapter.OnClickListener {
                override fun onClick(position: Int, model: HotspotView) {
                    hot.id=model.locId!!
                    if (hot.isAdded) {
                        hot.dismiss()
                    }
                    hot.setGoogleMap(googleMap)
                    hot.mLocationCallback=mLocationCallback
                    hot.mFusedLocationClient=mFusedLocationClient
                    hot.man=parentFragmentManager
                    hot.show(parentFragmentManager, HotspotItem.TAG)

                    activity?.runOnUiThread(Runnable {
                        close()
                    })
                }
            })
        })
    }

    companion object {
        const val TAG = "ListModalBottomSheet"

    }

    private fun close ()
    {
        this.dismiss()
    }
}


class HotspotItem: BottomSheetDialogFragment()
{
    private lateinit var Name:TextView
    private lateinit var Address:TextView
    private lateinit var Dist:TextView
    private lateinit var googleMap: GoogleMap
    private lateinit var current:Location
    var id:String=""
    var hotspot:hDetails= hDetails()
    lateinit var man:FragmentManager
    val helper:DirectionHelper= DirectionHelper()
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var mLocationCallback  : LocationCallback


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {

        return inflater.inflate(R.layout.hotspot_modal_view, container, false)
    }


    fun setGoogleMap(map: GoogleMap) {
        googleMap = map
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        current = Location("currentLocation")
        current.latitude = UserData.lat
        current.longitude = UserData.lng
        Name=view.findViewById(R.id.locname)
        Address=view.findViewById(R.id.address)
        Dist=view.findViewById(R.id.dist)
        current= Location("currentLocation")
        current.latitude=UserData.lat
        current.longitude=UserData.lng
        getHotspotDetails(id)
        val fab = view.findViewById<ExtendedFloatingActionButton>(R.id.extended_fab_directions)
        fab.setOnClickListener {
            directions()
        }



    }


    private fun getHotspotDetails(code:String) {
        val ebirdapi = eBirdRetro.getInstance().create((eBirdApi::class.java))
        GlobalScope.launch {
            val call: Call<hDetails>? = ebirdapi.hotspotInfo(code)
            call!!.enqueue(object : Callback<hDetails>
            {

                override fun onResponse(call: Call<hDetails>?, response: Response<hDetails>)
                {
                    if (response.isSuccessful())
                    {
                        Log.d("testing", response.body()!!.toString())
                        hotspot = response.body()!!
                        activity?.runOnUiThread(Runnable {
                            Name.text=hotspot.locName
                            Address.text=hotspot.hierarchicalName
                            Dist.text=helper.distance(hotspot.locName!!,hotspot.latitude!!,hotspot.longitude!!)
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(hotspot.latitude!!,hotspot.longitude!!), 15F))

                        })

                    }
                }

                override fun onFailure(call: Call<hDetails>?, t: Throwable?)
                {
                    // displaying an error message in toast

                    Log.d("Testing", "fail")
                }
            })
        }

    }


    private fun directions()
    {
        UserData.destLat=hotspot.latitude!!
        UserData.destLng=hotspot.longitude!!
        close()
        stoplocationUpdates()
        toActivity()

    }


    companion object {
        const val TAG = "ItemHotspotModal"

    }

    private fun close ()
    {
        this.dismiss()
    }
    private fun toActivity()
    {
        val intent = Intent(requireContext(), NavigationActivity::class.java)
        startActivity(intent)
    }

    private fun stoplocationUpdates() {
        mFusedLocationClient!!.removeLocationUpdates(mLocationCallback)
        Log.d("testing","main stopped")
    }
}



