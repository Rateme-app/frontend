package com.example.rate_me.api.models

import java.io.Serializable
import java.util.Date

data class User(
    var _id: String? = null,
    var username: String,
    var email: String,
    var password: String,
    var firstname: String? = null,
    var lastname: String? = null,
    var birthdate: Date? = null,
    var gender: String? = null,
    var bio: String? = null,
    var imageFilename: String? = null,
    var role: String? = null,
    var isVerified: Boolean? = null
) : Serializable
