package com.example.rate_me.api.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Post(
    val _id: String? = null,
    val title: String,
    val description: String,
    val videoFilename: String,

    @SerializedName("user")
    val user: User?,
    @SerializedName("likes")
    val likes: List<Like>?,
    @SerializedName("comments")
    val comments: List<Comment>?
) : Serializable
