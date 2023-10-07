package com.opsc.nestquest.api.weather.models

import com.google.gson.annotations.SerializedName


data class Imperial (

    @SerializedName("Value"    ) var Value    : Double?    = null,
    @SerializedName("Unit"     ) var Unit     : String? = null,
    @SerializedName("UnitType" ) var UnitType : Int?    = null

)