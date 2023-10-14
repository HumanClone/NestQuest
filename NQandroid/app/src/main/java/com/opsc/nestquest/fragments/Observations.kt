package com.opsc.nestquest.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.opsc.nestquest.Objects.UserData
import com.opsc.nestquest.R
import com.opsc.nestquest.api.nestquest.adapters.observationAdapter
import com.opsc.nestquest.api.nestquest.models.Observation


class Observations : Fragment() {
    private lateinit var recycler:RecyclerView

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

        recycler=view.findViewById(R.id.recycler_observation)
        genRecycleView(UserData.observations,recycler)

        val fab2=view.findViewById<ExtendedFloatingActionButton>(R.id.extended_fab_hot)
        fab2.setOnClickListener {
            val co= CreateObservation()
            co.recycler=recycler
            co.show(parentFragmentManager,CreateObservation.TAG)
        }
    }
    private fun genRecycleView(data:List<Observation>, recyclerView: RecyclerView)
    {
        activity?.runOnUiThread(Runnable {
            recyclerView.layoutManager = LinearLayoutManager(context)
            val adapter = observationAdapter(data)
            recyclerView.adapter = adapter
//            adapter.setOnClickListener(object : observationAdapter.OnClickListener {
//                override fun onClick(position: Int, model: HotspotView) {
//
//
//                }
//            })
        })
    }

}