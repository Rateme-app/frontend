package com.example.rate_me.api.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import com.example.rate_me.api.models.Rating

interface RatingService {

    @GET("/ratings")
    fun getAll(): Call<List<Rating>>

    @POST("/ratings")
    fun addOrUpdate(@Body rating: Rating): Call<Rating>
}
