package com.opsc.nextquest.api.weather


import com.opsc.nextquest.api.weather.models.ALocation
import com.opsc.nextquest.api.weather.models.Conditions
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherApi {

    @GET("locations/v1/cities/geoposition/search")
    fun getKey(@Query("apikey")apikey:String,@Query("q")location:String): Call<ALocation?>?
    //suspend fun getKey():Call<ALocation?>?

    @GET("currentconditions/v1/{locationKey}")
    fun getConditions(@Path("locationKey")key:String, @Query("apikey")apikey:String):Call<List<Conditions>?>?
}