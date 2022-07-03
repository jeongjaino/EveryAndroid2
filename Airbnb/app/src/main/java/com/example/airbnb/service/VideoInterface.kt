package com.example.airbnb.service

import com.example.airbnb.dto.VideoDto
import retrofit2.Call
import retrofit2.http.GET

interface VideoInterface {

    @GET("/v3/3143ac03-dc75-4062-8aee-1462f2b0d184")
    fun listVideos(): Call<VideoDto>
}