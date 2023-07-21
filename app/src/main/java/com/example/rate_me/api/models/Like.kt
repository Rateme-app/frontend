package com.example.rate_me.api.models

import java.io.Serializable
import java.util.Date

data class Like(
    var date: Date,
    var user: String?,
    var post: String?
) : Serializable
