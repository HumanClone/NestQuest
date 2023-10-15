package com.opsc.nestquest.api.nestquest.models

import com.google.gson.annotations.SerializedName

data class Picture(
    @SerializedName("pictureId" ) var pictureId: String?,
    @SerializedName("userId" ) var userId: String?,
    @SerializedName("description" )var description: String?
)
