package com.opsc.nextquest.api.maps.models
import com.google.gson.annotations.SerializedName


data class MapData (

  @SerializedName("geocoded_waypoints" ) var geocodedWaypoints : ArrayList<GeocodedWaypoints> = arrayListOf(),
  @SerializedName("routes"             ) var routes            : ArrayList<Routes>            = arrayListOf(),
  @SerializedName("status"             ) var status            : String?                      = null

)