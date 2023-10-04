package com.opsc.nextquest.api.weather.models

import com.google.gson.annotations.SerializedName


data class TimeZone (

    @SerializedName("Code"             ) var Code             : String?  = null,
    @SerializedName("Name"             ) var Name             : String?  = null,
    @SerializedName("GmtOffset"        ) var GmtOffset        : Int?     = null,
    @SerializedName("IsDaylightSaving" ) var IsDaylightSaving : Boolean? = null,
    @SerializedName("NextOffsetChange" ) var NextOffsetChange : String?  = null

)