package com.opsc.nestquest.api.ebird.models

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

data class HotspotView(
    @SerializedName("locId"             ) var locId             : String? = null,
    @SerializedName("locName"           ) var locName           : String? = null,
    @SerializedName("lat"               ) var latLng               : LatLng? = null,
    @SerializedName("dist"               ) var distance               : Double? = null,
    @SerializedName("numSpeciesAllTime" ) var numSpeciesAllTime : Int?    = null
)
