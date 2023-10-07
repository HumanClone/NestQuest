package com.opsc.nestquest.api.maps.models

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MapsApi {

    @GET("directions/json")
    fun getdirections(@Query("origin")origin:String,
                      @Query("destination")dest:String,
                      @Query("sensor")sen:String,
                      @Query("mode")mode:String,
                      @Query("units")unit:String,
                      @Query("key")key:String): Call<MapData>
}