package com.opsc.nestquest.api.nestquest.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.opsc.nestquest.R
import com.opsc.nestquest.api.maps.adapters.StepsAdapter
import com.opsc.nestquest.api.maps.models.Steps
import com.opsc.nestquest.api.nestquest.models.Observation
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class observationAdapter (var data: List<Observation>) :
    RecyclerView.Adapter<observationAdapter.MyViewHolder>() {
    private var onClickListener:OnClickListener?=null

    //binds and sets elements and values in a viewholder  to that of each object
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var Date:TextView=view.findViewById(R.id.date)
        var Description:TextView=view.findViewById(R.id.description)
        var lat:TextView=view.findViewById(R.id.lat)
        var lng:TextView=view.findViewById(R.id.lng)
        var Picture: ImageView =view.findViewById(R.id.picture)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.observation_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var system:Boolean=true
        val item = data[position]
        holder.Date.text=item.dateSeen.toString().substring(0,10)
        var parts:List<String> =item.coordinates!!.split(",")
        holder.lat.text="Latitude:${parts[0]}"
        holder.lng.text="Longitude:${parts[1]}"
        holder.Description.text=item.description
        if (!item.picture.isNullOrEmpty())
        {
            holder.Picture.load(item.picture)
        }
        else{
            holder.Picture.load(R.drawable.no_image_24)
        }
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
        fun onClick(position: Int, model: Observation)
    }



}