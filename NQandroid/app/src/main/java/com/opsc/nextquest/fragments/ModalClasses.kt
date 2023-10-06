package com.opsc.nextquest.fragments

import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.opsc.nextquest.BuildConfig
import com.opsc.nextquest.Objects.CurrentLocation
import com.opsc.nextquest.R
import com.opsc.nextquest.api.ebird.adapters.HotspotListAdapter
import com.opsc.nextquest.api.ebird.eBirdApi
import com.opsc.nextquest.api.ebird.eBirdRetro
import com.opsc.nextquest.api.ebird.models.HotspotView
import com.opsc.nextquest.api.ebird.models.hDetails
import com.opsc.nextquest.api.maps.MapsRetro
import com.opsc.nextquest.api.maps.adapters.StepsAdapter
import com.opsc.nextquest.api.maps.models.MapData
import com.opsc.nextquest.api.maps.models.MapsApi
import com.opsc.nextquest.api.maps.models.Steps
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
        current.latitude = CurrentLocation.lat
        current.longitude = CurrentLocation.lng
        Name=view.findViewById(R.id.locname)
        Address=view.findViewById(R.id.address)
        Dist=view.findViewById(R.id.dist)
        current= Location("currentLocation")
        current.latitude=CurrentLocation.lat
        current.longitude=CurrentLocation.lng
        getHotspotDetails(id)
        val fab = view.findViewById<ExtendedFloatingActionButton>(R.id.extended_fab_directions)
        fab.setOnClickListener {
            directions()
        }



    }

    //TODO: Change to helper
    //https://www.spaceotechnologies.com/blog/calculate-distance-two-gps-coordinates-google-maps-api/
    private fun distance():String{
        var current:Location= Location("currentLocation")
        current.latitude=CurrentLocation.lat
        current.longitude=CurrentLocation.lng
        var destination:Location=Location(hotspot.locName)
        destination.latitude=hotspot.latitude!!
        destination.longitude=hotspot.longitude!!
        var dist=current.distanceTo(destination)
        Log.d("testing",dist.toString())
        return CurrentLocation.convertDistance(dist.toDouble())
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
                            Dist.text=distance()
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
        this.googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(CurrentLocation.LatLng, 20.5f))
        getDirections()
        close()

    }


 //TODO:Change to helper
//https://www.geeksforgeeks.org/how-to-generate-route-between-two-locations-in-google-map-in-android/
    fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val latLng = LatLng((lat.toDouble() / 1E5),(lng.toDouble() / 1E5))
            poly.add(latLng)
        }
        return poly
    }

    fun addPoly(mapdata: MapData):PolylineOptions
    {
        val result =  ArrayList<List<LatLng>>()
        val respObj = mapdata
        val path =  ArrayList<LatLng>()
        for (i in 0 until respObj.routes[0].legs[0].steps.size){
            path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline!!.points!!))
        }

        result.add(path)
        Log.d("testing",result.toString())

        val lineoption = PolylineOptions()
        for (i in result.indices){
            lineoption.addAll(result[i])
            lineoption.width(20f)
            lineoption.color(Color.GREEN)
            lineoption.geodesic(true)
        }
        return lineoption
    }

    private fun getDirections()
    {
        val mapApi=MapsRetro.getInstance().create(MapsApi::class.java)
        GlobalScope.launch {
            val call:Call<MapData> =mapApi.getdirections("${CurrentLocation.lat},${CurrentLocation.lng}","${hotspot.lat},${hotspot.lng}","false","walking","metric",BuildConfig.MAPS_API_KEY)
            call!!.enqueue(object : Callback<MapData> {
                override fun onResponse(call: Call<MapData>?, response: Response<MapData>)
                {
                    if (response.isSuccessful) {
                        val mapdata = response.body()
                        if (mapdata != null) {
                             // Assuming the response contains multiple conditions

                            Log.d("testing", mapdata.toString())
                            val result =  ArrayList<List<LatLng>>()
                            val respObj = mapdata
                            val path =  ArrayList<LatLng>()
                            for (i in 0 until respObj.routes[0].legs[0].steps.size){
                                path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline!!.points!!))
                            }

                            result.add(path)
                            Log.d("testing",result.toString())

                            val lineoption = PolylineOptions()
                            for (i in result.indices){
                                lineoption.addAll(result[i])
                                lineoption.width(20f)
                                lineoption.color(Color.GREEN)
                                lineoption.geodesic(true)
                            }
                            googleMap.addPolyline(lineoption)
                            val dir:Directions= Directions()
                            dir.data=mapdata
                            dir.lineoption=lineoption
                            loadFrag(dir)
                            close()

                        } else {
                            Log.d("testing", "Empty or null response")
                        }
                    } else {
                        Log.d("testing", "Response not successful: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<MapData>?, t: Throwable?) {
                    Log.d("Testing", "fail\t${t?.message}")
                }
            })
        }
    }
    companion object {
        const val TAG = "ItemHotspotModal"

    }

    private fun close ()
    {
        this.dismiss()
    }

    private fun loadFrag(fragment: Fragment)
    {
        val fragmentManager:FragmentManager  =man
        val fragmentTransaction=fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment_activity_main,fragment).commit()
    }
}

class DirectionModal: BottomSheetDialogFragment() {

    var directions:MapData= MapData()
    lateinit var recycler: RecyclerView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.directions_modal_view, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        recycler=view.findViewById(R.id.modal_recycler_dir)
        genRecycleView(directions.routes[0].legs[0].steps,recycler)
        Log.d("testing","start of directions")
        val fab = view.findViewById<ExtendedFloatingActionButton>(R.id.extended_fab_directions_modal)
        fab.setOnClickListener {

        }


    }

    private fun genRecycleView(data:List<Steps>, recyclerView: RecyclerView)
    {
        activity?.runOnUiThread(Runnable {
            recyclerView.layoutManager = LinearLayoutManager(context)
            val adapter = StepsAdapter(data)
            recyclerView.adapter = adapter

        })
    }
    companion object {
        const val TAG = "DirectionsModal"

    }

    private fun close ()
    {
        this.dismiss()
    }


}

