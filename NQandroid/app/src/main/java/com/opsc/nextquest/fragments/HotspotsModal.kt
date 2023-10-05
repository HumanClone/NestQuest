package com.opsc.nextquest.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.opsc.nextquest.R
import com.opsc.nextquest.api.ebird.adapters.HotspotListAdapter
import com.opsc.nextquest.api.ebird.eBirdApi
import com.opsc.nextquest.api.ebird.eBirdRetro
import com.opsc.nextquest.api.ebird.models.HotspotView
import com.opsc.nextquest.api.ebird.models.Hotspots
import com.opsc.nextquest.api.ebird.models.hDetails
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
                    hot.show(parentFragmentManager,HotspotItem.TAG)
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
    private lateinit var Num:TextView
    private lateinit var Dist:TextView

    var id:String=""
    var hotspot:hDetails= hDetails()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {

        return inflater.inflate(R.layout.hotspot_modal_view, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Name=view.findViewById(R.id.locname)
        Address=view.findViewById(R.id.address)
        Num=view.findViewById(R.id.num)
        Dist=view.findViewById(R.id.dist)

        getHotspotDetails(id)




    }
    private fun getHotspotDetails(code:String) {
        val ebirdapi = eBirdRetro.getInstance().create((eBirdApi::class.java))
        var hotspot:hDetails= hDetails()
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

    companion object {
        const val TAG = "ItemModalBottomSheet"

    }

    private fun close ()
    {
        this.dismiss()
    }
}