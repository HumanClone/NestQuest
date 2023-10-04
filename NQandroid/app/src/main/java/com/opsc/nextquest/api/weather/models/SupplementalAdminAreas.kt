package com.opsc.nextquest.api.weather.models

import com.google.gson.annotations.SerializedName


data class SupplementalAdminAreas (

    @SerializedName("Level"         ) var Level         : Int?    = null,
    @SerializedName("LocalizedName" ) var LocalizedName : String? = null,
    @SerializedName("EnglishName"   ) var EnglishName   : String? = null

)