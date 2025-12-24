package com.example.handhophop.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

data class NekoImageDto(
    val id: Int,
    val url: String,
    val rating: String
)

interface NekoApi {
    @GET("images/{id}")
    suspend fun getImageById(@Path("id") id: Int): NekoImageDto
}
