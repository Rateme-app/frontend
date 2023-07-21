package com.example.rate_me.api.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import com.example.rate_me.api.models.Like
import com.example.rate_me.api.models.Post

interface LikeService {

    @POST("/like")
    fun addOrRemove(@Body like: Like): Call<Unit>
}