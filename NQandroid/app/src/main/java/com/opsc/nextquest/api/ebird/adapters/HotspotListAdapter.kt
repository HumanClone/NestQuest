package com.opsc.nextquest.api.ebird.adapters

import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.opsc.nextquest.Objects.CurrentLocation
import com.opsc.nextquest.R
import com.opsc.nextquest.api.ebird.models.HotspotView
import com.opsc.nextquest.api.ebird.models.Hotspots
import com.opsc.nextquest.classes.DirectionHelper

class HotspotListAdapter (var data: List<HotspotView>) :
    RecyclerView.Adapter<HotspotListAdapter.MyViewHolder>() {
    private var onClickListener:OnClickListener?=null
    val helper:DirectionHelper=DirectionHelper()

    //binds and sets elements and values in a viewholder  to that of each object
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var Name:TextView=view.findViewById(R.id.LocName)
        var Dist:TextView=view.findViewById(R.id.Dist)
        var Num:TextView=view.findViewById(R.id.num)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.hotspot_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var system:Boolean=true
        val item = data[position]
        holder.Name.text=item.locName
        holder.Num.text="Number of Speices: "+item.numSpeciesAllTime
        holder.Dist.text=helper.convertDistance(item.distance!!)



        //Code attributed
        //https://www.geeksforgeeks.org/how-to-apply-onclicklistener-to-recyclerview-items-in-android/
        // to set an onclick listener to an item in a recycler view
        //aurthor chinmaya121221
        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, item )
            }
        }

    }

    // A function to bind the onclickListener.
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    // onClickListener Interface
    interface OnClickListener {
        fun onClick(position: Int, model: HotspotView)
    }


}