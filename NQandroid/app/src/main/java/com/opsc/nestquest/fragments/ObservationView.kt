package com.opsc.nestquest.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.opsc.nestquest.R
import com.opsc.nestquest.api.nestquest.models.Observation


class ObservationView : BottomSheetDialogFragment() {

   lateinit var ob:Observation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_observation_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var date: TextInputEditText =view.findViewById(R.id.Sdate)
        var description: TextInputEditText =view.findViewById(R.id.Sdescription)
        var address: TextInputEditText =view.findViewById(R.id.SAddress)
        var lat: TextInputEditText =view.findViewById(R.id.Slat)
        var lng:TextInputEditText =view.findViewById(R.id.Slng)
        var picture: ImageView =view.findViewById(R.id.Spicture)

        val co:List<String> =ob.coordinates!!.split(";")
        lat.setText(co[0])
        lng.setText(co[1])
        address.setText(co[2])
        description.setText(ob.description)
        date.setText(ob.dateSeen.toString().substring(0,10))
        picture.load(ob.picture)


    }


    companion object {
        const val TAG = "Observation View"

    }

}