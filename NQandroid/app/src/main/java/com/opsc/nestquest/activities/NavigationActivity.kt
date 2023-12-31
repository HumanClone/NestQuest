package com.opsc.nestquest.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.gms.maps.model.LatLng
import com.opsc.nestquest.Objects.UserData
import com.opsc.nestquest.R
import com.opsc.nestquest.classes.BackgroundLocal
import com.opsc.nestquest.fragments.Directions

class NavigationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        val serviceIntent = Intent(this, BackgroundLocal::class.java)
        stopService(serviceIntent)

        val dir:Directions=Directions()
        dir.destination=LatLng(UserData.destLat,UserData.destLng)
        loadFrag(dir)

    }

    private fun loadFrag(fragment: Fragment)
    {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction=fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment_navigation,fragment).commit()
    }


}