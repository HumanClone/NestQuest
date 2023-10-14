package com.opsc.nestquest.api.nestquest

import com.opsc.nestquest.api.nestquest.models.Observation
import com.opsc.nestquest.api.nestquest.models.Picture
import com.opsc.nestquest.api.nestquest.models.User
import retrofit2.http.GET
import retrofit2.Call
import retrofit2.http.*

interface NQAPI {

    @GET("/User/GetUser")
    suspend fun getUser(@Query("UserId")userId:String):Call<User>

    @GET("/User/GetAllUsers")
    suspend fun getusers():List<User>

    @GET("/BirdSighting")
    suspend fun getObserve(@Query("userId")userId: String):List<Observation>




    @Headers("Content-Type: application/json")
    @POST("/BirdSighting/AddBirdSighting")
    fun addObserve(@Body observation: Observation):Call<Observation>

    @Headers("Content-Type: application/json")
    @POST("/User/AddUser")
    fun addUser(@Body user:User): Call<User>

    @Headers("Content-Type: application/json")
    @POST("Picture/AddPicture")
    fun addPic(@Body picture: Picture?): Call<Picture>


    @Headers("Content-Type: application/json")
    @POST("/User/EditUser")
    fun editUser(@Query("UserId")userid:String,@Body user:User): Call<User>
}