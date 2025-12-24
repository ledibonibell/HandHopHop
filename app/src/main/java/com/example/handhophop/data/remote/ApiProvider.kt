package com.example.handhophop.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiProvider {
    private const val BASE_URL = "https://api.nekosapi.com/v4/"

    val nekoApi: NekoApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NekoApi::class.java)
    }
}
