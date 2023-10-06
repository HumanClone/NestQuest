package com.opsc.nextquest.classes

import android.graphics.Color
import android.location.Location
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.opsc.nextquest.Objects.CurrentLocation
import com.opsc.nextquest.R
import com.opsc.nextquest.api.maps.models.MapData

class DirectionHelper {

    //https://www.spaceotechnologies.com/blog/calculate-distance-two-gps-coordinates-google-maps-api/
     fun distance(name:String,lat:Double,lng:Double):String{
        return convertDistance(distanceM(name,lat,lng))
    }



    fun distanceM(name:String,lat:Double,lng:Double):Double{
        var current: Location = Location("currentLocation")
        current.latitude= CurrentLocation.lat
        current.longitude= CurrentLocation.lng
        var destination: Location = Location(name)
        destination.latitude=lat
        destination.longitude=lng
        var dist=current.distanceTo(destination)
        return dist.toDouble()
    }

    //https://www.geeksforgeeks.org/how-to-generate-route-between-two-locations-in-google-map-in-android/
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

    fun getPolyLine(mapdata: MapData): PolylineOptions
    {
        val result =  ArrayList<List<LatLng>>()
        val respObj = mapdata
        val path =  ArrayList<LatLng>()
        for (i in 0 until respObj.routes[0].legs[0].steps.size){
            path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline!!.points!!))
        }

        result.add(path)
        Log.d("testing",result.toString())

        val lineoption = PolylineOptions()
        for (i in result.indices){
            lineoption.addAll(result[i])
            lineoption.width(20f)
            lineoption.color(Color.GREEN)
            lineoption.geodesic(true)
        }
        return lineoption
    }

    fun convertDistance(distanceInMeters: Double): String {
        //TODO:This needs to check the system
        return if (true) {
            String.format("%.2f",distanceInMeters / 1000.0) + " km"// Convert to kilometers
        } else {
            String.format("%.2f",distanceInMeters * 0.000621371).toString() +" mi." // Convert to miles
        }
    }
}