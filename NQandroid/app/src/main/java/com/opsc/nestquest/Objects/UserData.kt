package com.opsc.nestquest.Objects

import com.google.android.gms.maps.model.LatLng
import com.opsc.nestquest.api.nestquest.models.Observation
import com.opsc.nestquest.api.nestquest.models.User

object UserData
{

    var lat:Double=0.0
    var lng:Double=0.0
    var destLat:Double=0.0
    var destLng:Double=0.0
//    var distance:Double=10.0
//    var metric:Boolean=true
    var user: User=User(null,null,null,null,null,null,null)
    val observations= mutableListOf<Observation>()


}
