package com.opsc.nestquest.api.maps.models

import com.google.gson.annotations.SerializedName


data class Distance (

  @SerializedName("text"  ) var text  : String? = null,
  @SerializedName("value" ) var value : Int?    = null

)