package com.opsc.nestquest.activities

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationBarView
import com.opsc.nestquest.R
import com.opsc.nestquest.databinding.ActivityMainBinding
import com.opsc.nestquest.fragments.MapView
import com.opsc.nestquest.fragments.Observations
import com.opsc.nestquest.fragments.Settings

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView


        loadFrag(MapView())

        navView.selectedItemId=R.id.navigation_MapView
        navView.setOnItemSelectedListener {  item ->
            when(item.itemId) {
                R.id.navigation_Observation -> {
                    // Respond to navigation item 1 click
                    loadFrag(Observations())
                    true
                }
                R.id.navigation_Settings -> {
                    // Respond to navigation item 2 click
                    loadFrag(Settings())
                    true
                }
                R.id.navigation_MapView -> {
                    // Respond to navigation item 2 click
                    loadFrag(MapView())
                    true
                }
                else -> false
            }
        }


    }

    private fun loadFrag(fragment: Fragment)
    {
        val fragmentManager: FragmentManager =supportFragmentManager
        val fragmentTransaction=fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment_activity_main,fragment).commit()
    }
}