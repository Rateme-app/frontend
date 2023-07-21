package com.example.rate_me.api.models

import java.io.Serializable
import java.util.Date

data class Conversation(
    var _id: String? = null,
    var sender: User?,
    var receiver: User?,
    var lastMessage: String,
    var lastMessageDate: Date
) : Serializable
