package com.opsc.nestquest.api.maps.models

import com.google.gson.annotations.SerializedName


data class OverviewPolyline (

  @SerializedName("points" ) var points : String? = null

)