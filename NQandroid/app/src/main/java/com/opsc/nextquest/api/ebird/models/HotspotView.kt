package com.opsc.nextquest.api.ebird.models

import com.google.gson.annotations.SerializedName

data class HotspotView(
    @SerializedName("locId"             ) var locId             : String? = null,
    @SerializedName("locName"           ) var locName           : String? = null,
    @SerializedName("lat"               ) var lat               : Double? = null,
    @SerializedName("lng"               ) var lng               : Double? = null,
    @SerializedName("numSpeciesAllTime" ) var numSpeciesAllTime : Int?    = null
)
