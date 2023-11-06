package com.opsc.nestquest.api.maps.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.opsc.nestquest.R
import com.opsc.nestquest.api.maps.models.Steps
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class StepsAdapter  (var data: List<Steps>) :
RecyclerView.Adapter<StepsAdapter.MyViewHolder>() {
    private var onClickListener:OnClickListener?=null

    //binds and sets elements and values in a viewholder  to that of each object
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var Dir: TextInputEditText=view.findViewById(R.id.modal_dir)
        var Dist: TextInputEditText =view.findViewById(R.id.modal_dist)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.diri_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var system:Boolean=true
        val item = data[position]
        holder.Dir.setText(toPlainText(item.htmlInstructions!!))
        holder.Dist.setText(item.distance!!.text)
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
        fun onClick(position: Int, model: Steps)
    }

    fun toPlainText(htmlString: String): String {
        val document: Document = Jsoup.parse(htmlString)
        return document.text()
    }

}