package com.opsc.nestquest.api.weather.models

import com.google.gson.annotations.SerializedName


data class Country (

    @SerializedName("ID"            ) var ID            : String? = null,
    @SerializedName("LocalizedName" ) var LocalizedName : String? = null,
    @SerializedName("EnglishName"   ) var EnglishName   : String? = null

)