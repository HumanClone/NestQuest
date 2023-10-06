package com.opsc.nextquest.classes

import android.location.Location
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.gms.maps.model.LatLng
import com.opsc.nextquest.Objects.CurrentLocation
import com.opsc.nextquest.R

class DirectionHelper {

    private fun distance(destination:LatLng):String{
        var current: Location = Location("currentLocation")
        current.latitude= CurrentLocation.lat
        current.longitude= CurrentLocation.lng
        var destination: Location = Location("Destination")
        destination.latitude=destination.latitude!!
        destination.longitude=destination.longitude!!
        var dist=current.distanceTo(destination)
        Log.d("testing",dist.toString())
        return CurrentLocation.convertDistance(dist.toDouble())
    }

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


}