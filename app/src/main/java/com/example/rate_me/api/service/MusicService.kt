package com.example.rate_me.api.service

import retrofit2.Call
import retrofit2.http.GET
import com.example.rate_me.api.models.Music

interface MusicService {

    @GET("/music")
    fun getAll(): Call<List<Music>>
}