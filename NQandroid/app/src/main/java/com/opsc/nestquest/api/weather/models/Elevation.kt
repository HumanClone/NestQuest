package com.opsc.nestquest.api.weather.models

import com.google.gson.annotations.SerializedName


data class Elevation (

    @SerializedName("Metric"   ) var Metric   : Metric?   = Metric(),
    @SerializedName("Imperial" ) var Imperial : Imperial? = Imperial()

)