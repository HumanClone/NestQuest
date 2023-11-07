package com.opsc.nestquest.classes

import android.graphics.Color
import android.location.Location
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.opsc.nestquest.Objects.UserData
import com.opsc.nestquest.R
import com.opsc.nestquest.api.maps.models.MapData

class DirectionHelper {

    //Code attributed
    // https://www.spaceotechnologies.com/blog/calculate-distance-two-gps-coordinates-google-maps-api/
    // How to Calculate Distance Between Two GPS Coordinates in an App
    //aurthor:Bhaval Patel

     fun distance(name:String,lat:Double,lng:Double):String{
        return convertDistance(distanceM(name,lat,lng))
    }



    fun distanceM(name:String,lat:Double,lng:Double):Double{
        var current: Location = Location("currentLocation")
        current.latitude= UserData.lat
        current.longitude= UserData.lng
        var destination: Location = Location(name)
        destination.latitude=lat
        destination.longitude=lng
        var dist=current.distanceTo(destination)
        return dist.toDouble()
    }

    //Code attributed
    //https://www.geeksforgeeks.org/how-to-generate-route-between-two-locations-in-google-map-in-android/
    // How to Generate Route Between Two Locations in Google Map in Android?
    //aurthor:aashaypawar

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
        return if (UserData.user.metricSystem!!) {
            String.format("%.2f",distanceInMeters / 1000.0) + " km"// Convert to kilometers
        } else {
            String.format("%.2f",distanceInMeters * 0.000621371).toString() +" mi" // Convert to miles
        }
    }

    fun getIcon(num:Int):Int
    {
        when(num)
        {
            1-> return R.drawable.s01_s
            2->return R.drawable.s02_s
            3->return R.drawable.s03_s
            4->return R.drawable.s04_s
            5->return R.drawable.s05_s
            6->return R.drawable.s06_s
            7->return R.drawable.s07_s
            8->return R.drawable.s08_s
            11->return R.drawable.s11_s
            12->return R.drawable.s12_s
            13->return R.drawable.s13_s
            14->return R.drawable.s14_s
            15->return R.drawable.s15_s
            16->return R.drawable.s16_s
            17->return R.drawable.s17_s
            18->return R.drawable.s18_s
            19->return R.drawable.s19_s
            20->return R.drawable.s20_s
            21->return R.drawable.s21_s
            22->return R.drawable.s22_s
            23->return R.drawable.s23_s
            24->return R.drawable.s24_s
            25->return R.drawable.s25_s
            26->return R.drawable.s26_s
            29->return R.drawable.s29_s
            30->return R.drawable.s30_s
            31->return R.drawable.s31_s
            32->return R.drawable.s32_s
            33->return R.drawable.s33_s
            34->return R.drawable.s34_s
            35->return R.drawable.s35_s
            36->return R.drawable.s36_s
            37->return R.drawable.s37_s
            38->return R.drawable.s38_s
            39->return R.drawable.s39_s
            40->return  R.drawable.s40_s
            41->return R.drawable.s41_s
            42->return R.drawable.s42_s
            43->return R.drawable.s43_s
            44->return R.drawable.s44_s
            else->{
                return R.drawable.eye_24
            }
        }
    }
}