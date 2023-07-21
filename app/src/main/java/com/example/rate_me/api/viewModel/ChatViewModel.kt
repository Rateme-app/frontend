package com.example.rate_me.api.viewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.rate_me.api.models.Conversation
import com.example.rate_me.api.models.Message
import com.example.rate_me.api.models.MessageWithoutPopulate
import com.example.rate_me.api.service.ApiBuilder
import com.example.rate_me.api.service.ChatService

class ChatViewModel : ViewModel() {

    private val chatService = ApiBuilder.buildService(ChatService::class.java)

    fun getMyConversations(sender: String, context: Context): LiveData<List<Conversation>> {
        val conversationsLiveData = MutableLiveData<List<Conversation>>()

        val call = chatService.getMyConversations(sender)
        call.enqueue(object : Callback<List<Conversation>> {
            override fun onResponse(
                call: Call<List<Conversation>>,
                response: Response<List<Conversation>>
            ) {
                if (response.isSuccessful) {
                    conversationsLiveData.value = response.body()
                } else {
                    Toast.makeText(
                        context,
                        "Network error : ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Conversation>>, t: Throwable) {
                Log.d("API", "${t.message}")
                Toast.makeText(
                    context,
                    "Api failure : ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        return conversationsLiveData
    }

    fun getMyMessages(
        conversationId: String,
        context: Context
    ): LiveData<List<MessageWithoutPopulate>> {
        val messagesLiveData = MutableLiveData<List<MessageWithoutPopulate>>()

        val call = chatService.getMyMessages(conversationId)
        call.enqueue(object : Callback<List<MessageWithoutPopulate>> {
            override fun onResponse(
                call: Call<List<MessageWithoutPopulate>>,
                response: Response<List<MessageWithoutPopulate>>
            ) {
                if (response.isSuccessful) {
                    messagesLiveData.value = response.body()
                } else {
                    Toast.makeText(
                        context,
                        "Network error : ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<MessageWithoutPopulate>>, t: Throwable) {
                Log.d("API", "${t.message}")
                Toast.makeText(
                    context,
                    "Api failure : ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        return messagesLiveData
    }

    fun createConversation(conversation: Conversation, context: Context): LiveData<Conversation> {
        val conversationLiveData = MutableLiveData<Conversation>()

        val call = chatService.createConversation(conversation)
        call.enqueue(object : Callback<Conversation> {
            override fun onResponse(call: Call<Conversation>, response: Response<Conversation>) {
                if (response.isSuccessful) {
                    println("SUCC")
                    conversationLiveData.value = response.body()
                } else {
                    Toast.makeText(
                        context,
                        "Network error : ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Conversation>, t: Throwable) {
                Log.d("API", "${t.message}")
                Toast.makeText(
                    context,
                    "Api failure : ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        return conversationLiveData
    }

    fun sendMessage(
        messageRequest: ChatService.MessageRequest,
        context: Context
    ): LiveData<MessageWithoutPopulate> {
        val messageLiveData = MutableLiveData<MessageWithoutPopulate>()

        val call = chatService.sendMessage(messageRequest)
        call.enqueue(object : Callback<MessageWithoutPopulate> {
            override fun onResponse(call: Call<MessageWithoutPopulate>, response: Response<MessageWithoutPopulate>) {
                if (response.isSuccessful) {
                    messageLiveData.value = response.body()
                } else {
                    Toast.makeText(
                        context,
                        "Network error : ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<MessageWithoutPopulate>, t: Throwable) {
                Log.d("API", "${t.message}")
                Toast.makeText(
                    context,
                    "Api failure : ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        return messageLiveData
    }

    fun deleteConversation(conversationId: String, context: Context): LiveData<Unit> {
        val deleteLiveData = MutableLiveData<Unit>()

        val call = chatService.deleteConversation(conversationId)
        call.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    deleteLiveData.value = Unit
                } else {
                    Toast.makeText(
                        context,
                        "Network error : ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.d("API", "${t.message}")
                Toast.makeText(
                    context,
                    "Api failure : ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        return deleteLiveData
    }
}
