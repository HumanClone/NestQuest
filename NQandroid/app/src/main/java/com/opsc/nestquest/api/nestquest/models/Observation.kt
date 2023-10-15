package com.opsc.nestquest.api.nestquest.models


import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class Observation(
    @SerializedName("birdSightingId" ) var birdSightingId : String? = null,
    @SerializedName("userId"         ) var userId         : String? = null,
    @SerializedName("birdId"         ) var birdId         : String? = null,
    @SerializedName("dateSeen"       ) var dateSeen       : String? = null,
    @SerializedName("coordinates"    ) var coordinates    : String? = null,
    @SerializedName("picture"        ) var picture        : String? = null,
    @SerializedName("description"    ) var description    : String? = null

)

