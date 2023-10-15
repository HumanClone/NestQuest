package com.opsc.nestquest.api.nestquest.models


import com.google.gson.annotations.SerializedName

data class User(
   @SerializedName("userId"          ) var userId          : String?  = null,
   @SerializedName("name"            ) var name            : String?  = null,
   @SerializedName("email"           ) var email           : String?  = null,
   @SerializedName("birdSightingIds" ) var birdSightingIds : List<String>?  = null,
   @SerializedName("darkTheme"       ) var darkTheme       : Boolean? = null,
   @SerializedName("maxDistance"     ) var maxDistance     : Float?     = null,
   @SerializedName("metricSystem"    ) var metricSystem    : Boolean? = null


)

