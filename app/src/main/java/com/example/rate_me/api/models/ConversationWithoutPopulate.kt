package com.example.rate_me.api.models

import java.io.Serializable
import java.util.Date

data class ConversationWithoutPopulate(
    val _id: String,
    val lastMessage: String,
    val lastMessageDate: Date,
    val sender: String?,
    val receiver: String?
) : Serializable
