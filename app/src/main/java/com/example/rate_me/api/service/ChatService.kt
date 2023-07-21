package com.example.rate_me.api.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import com.example.rate_me.api.models.Conversation
import com.example.rate_me.api.models.MessageWithoutPopulate

interface ChatService {
    data class MessageRequest(
        val description: String,
        val sender: String,
        val receiver: String
    )

    @GET("/chat/my-conversations/{senderId}")
    fun getMyConversations(@Path("senderId") sender: String): Call<List<Conversation>>

    @GET("/chat/my-messages/{conversationId}")
    fun getMyMessages(@Path("conversationId") conversationId: String): Call<List<MessageWithoutPopulate>>

    @POST("/chat/create-conversation")
    fun createConversation(@Body conversation: Conversation): Call<Conversation>

    @POST("/chat/send-message")
    fun sendMessage(@Body messageRequest: MessageRequest): Call<MessageWithoutPopulate>

    @DELETE("/chat/{conversationId}")
    fun deleteConversation(@Path("conversationId") conversationId: String): Call<Unit>
}
