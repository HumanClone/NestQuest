package com.opsc.nestquest.api.nestquest.models

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NQRetro {

    val baseUrl = "https://nestquestapi.azurewebsites.net"
    val clientSetup = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .writeTimeout(1, TimeUnit.MINUTES) // write timeout
        .readTimeout(1, TimeUnit.MINUTES) // read timeout
        .build()

    //object that will be used to send and retrieve data
    fun getInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(clientSetup)
            .build()
    }
}