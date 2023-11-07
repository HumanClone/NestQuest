package com.opsc.nestquest.Objects

import com.google.android.gms.maps.model.LatLng
import com.opsc.nestquest.api.ebird.models.HotspotView
import com.opsc.nestquest.api.nestquest.models.Observation
import com.opsc.nestquest.api.nestquest.models.User

object UserData
{

    var lat:Double=0.0
    var lng:Double=0.0
    var destLat:Double=0.0
    var destLng:Double=0.0
    var user: User=User(null,null,null,null,null,null,true)
    var observations= mutableListOf<Observation>()
    var spots:List<HotspotView> = listOf()
    var weather="Weather Forecast"
    var icon:Int=0



}
