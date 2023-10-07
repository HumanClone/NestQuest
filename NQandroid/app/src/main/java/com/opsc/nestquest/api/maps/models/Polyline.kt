package com.opsc.nestquest.api.maps.models
import com.google.gson.annotations.SerializedName


data class Polyline (

  @SerializedName("points" ) var points : String? = null

)