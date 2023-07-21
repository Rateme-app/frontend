package com.example.rate_me.api.models

import java.io.Serializable
import java.util.Date

data class MessageWithoutPopulate(
    val _id: String,
    val description: String,
    val date: Date,
    val senderConversation: ConversationWithoutPopulate?,
    val receiverConversation: ConversationWithoutPopulate?
) : Serializable
