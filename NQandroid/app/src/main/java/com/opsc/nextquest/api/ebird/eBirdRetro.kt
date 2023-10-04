package com.opsc.nextquest.api.ebird

import com.opsc.nextquest.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
object eBirdRetro
{
    val baseUrl = "https://api.ebird.org/v2/ref/"
    val clientSetup = OkHttpClient.Builder().apply {
            addInterceptor(
                Interceptor { chain ->
                    val builder = chain.request().newBuilder()
                    builder.header("x-ebirdapitoken", BuildConfig.EBIRD_API_KEY)
                    return@Interceptor chain.proceed(builder.build())
                }
            )
        }
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