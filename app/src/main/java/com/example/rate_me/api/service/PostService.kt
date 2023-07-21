package com.example.rate_me.api.service

import com.example.rate_me.api.models.Post
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface PostService {

    @GET("/post")
    fun getAll(): Call<List<Post>>

    @Multipart
    @POST("/post")
    fun add(
        @Part file: MultipartBody.Part,
        @Part title: MultipartBody.Part,
        @Part description: MultipartBody.Part,
        @Part userIdData: MultipartBody.Part,
        @Part("filename") name: RequestBody
    ): Call<Post>

    @DELETE("/post/{id}")
    fun delete(@Path("id") id: String?): Call<Unit>
}