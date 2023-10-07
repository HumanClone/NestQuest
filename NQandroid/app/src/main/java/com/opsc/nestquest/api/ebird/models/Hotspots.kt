package com.opsc.nestquest.api.ebird.models

import com.google.gson.annotations.SerializedName

data class Hotspots (

    @SerializedName("locId"             ) var locId             : String? = null,
    @SerializedName("locName"           ) var locName           : String? = null,
    @SerializedName("countryCode"       ) var countryCode       : String? = null,
    @SerializedName("subnational1Code"  ) var subnational1Code  : String? = null,
    @SerializedName("subnational2Code"  ) var subnational2Code  : String? = null,
    @SerializedName("lat"               ) var lat               : Double? = null,
    @SerializedName("lng"               ) var lng               : Double? = null,
    @SerializedName("latestObsDt"       ) var latestObsDt       : String? = null,
    @SerializedName("numSpeciesAllTime" ) var numSpeciesAllTime : Int?    = null

)