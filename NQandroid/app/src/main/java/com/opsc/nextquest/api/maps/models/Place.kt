package com.opsc.nextquest.api.maps.models

import com.google.android.gms.maps.model.LatLng

data class Place(
    val name: String,
    val latLng: LatLng,
    val address: LatLng,
    val rating: Float

)
