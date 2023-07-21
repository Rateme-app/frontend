package com.example.rate_me.api.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import com.example.rate_me.api.models.Comment

interface CommentService {
    @POST("/comment")
    fun add(@Body comment: Comment): Call<Comment>
}