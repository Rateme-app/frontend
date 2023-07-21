package com.example.rate_me.api.models

import java.util.Date

data class Rating(
    val _id: String? = null,
    val description: String,
    val date: Date,
    val value: Float,
    val liker: User,
    val liked: User
)
