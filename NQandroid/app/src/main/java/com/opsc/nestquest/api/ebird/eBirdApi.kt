package com.opsc.nestquest.api.ebird

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import com.opsc.nestquest.api.ebird.models.Hotspots
import com.opsc.nestquest.api.ebird.models.hDetails
import retrofit2.http.Query

interface eBirdApi {



    @GET("hotspot/geo?fmt=json")
    fun nbyHotspots(@Query("lat")lat:String,@Query("lng")lng:String,@Query("dist")dist:Double):Call<List<Hotspots>?>?

    @GET("hotspot/info/{locId}")
    fun hotspotInfo(@Path("locId")locId:String):Call<hDetails>?




}