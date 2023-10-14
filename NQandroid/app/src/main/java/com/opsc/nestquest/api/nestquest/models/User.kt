package com.opsc.nestquest.api.nestquest.models

data class User(
   var UserId:String?,
   var Name:String?,
   var Email:String?,
   var birdSightingIds:List<String>? ,
   var darkTheme:Boolean?,
   var maxDistance:Float?,
   var metricSystem:Boolean?
)

