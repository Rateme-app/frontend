package com.example.rate_me.api.models

import java.io.Serializable

data class Message(
    val _id: String? = null,
    val description: String,
    val senderConversation: Conversation,
    val receiverConversation: Conversation
) : Serializable