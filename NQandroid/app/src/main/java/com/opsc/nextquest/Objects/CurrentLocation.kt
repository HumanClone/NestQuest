package com.opsc.nextquest.Objects

import com.google.android.gms.maps.model.LatLng

object CurrentLocation
{
    var lat:Double=0.0
    var lng:Double=0.0
    var LatLng:LatLng= LatLng(0.0,0.0)

    fun convertDistance(distanceInMeters: Double): String {
        //TODO:This needs to check the system
        return if (true) {
            String.format("%.2f",distanceInMeters / 1000.0) + " km"// Convert to kilometers
        } else {
            String.format("%.2f",distanceInMeters * 0.000621371).toString() +" mi." // Convert to miles
        }
    }
}