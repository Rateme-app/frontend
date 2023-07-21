package com.example.rate_me.api.models

import java.io.Serializable
import java.util.Date

data class Comment(
    var date: Date,
    var description: String?,
    var post: String?,
    var user: User?
) : Serializable