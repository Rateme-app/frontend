package com.example.rate_me.view.activities

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rate_me.R
import com.example.rate_me.adapters.MessageAdapter
import com.example.rate_me.api.models.Conversation
import com.example.rate_me.api.models.MessageWithoutPopulate
import com.example.rate_me.api.models.User
import com.example.rate_me.api.service.ChatService
import com.example.rate_me.api.viewModel.ChatViewModel
import com.example.rate_me.utils.UserSession
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {

    private lateinit var chatViewModel: ChatViewModel

    private var messagesList: MutableList<MessageWithoutPopulate> = mutableListOf()
    private lateinit var chatRV: RecyclerView
    private lateinit var addButton: Button
    private lateinit var messageTIET: TextInputEditText
    private lateinit var chatTopTextView: TextView

    private var currentConversation: Conversation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Initialize ViewModel
        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]


        chatRV = findViewById(R.id.chatRV)
        addButton = findViewById(R.id.addButton)
        messageTIET = findViewById(R.id.messageTIET)
        chatTopTextView = findViewById(R.id.chatTopTextView)

        val sessionUser: User = UserSession.getSession(this)

        currentConversation = intent.getSerializableExtra("conversation") as Conversation?
        val currentUser: User? = currentConversation!!.receiver

        if (currentUser != null) {
            chatTopTextView.text = "Chat with ${currentUser.firstname} ${currentUser.lastname}"
        }

        val linearLayoutManager =
            LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        chatRV.layoutManager = linearLayoutManager
        chatRV.adapter = MessageAdapter(
            messagesList,
            currentConversation!!
        )

        getData()
        startUpdates()

        addButton.setOnClickListener {
            if (messageTIET.text.toString() != "") {
                val messageRequest = ChatService.MessageRequest(
                    description = messageTIET.text.toString(),
                    sender = sessionUser._id!!,
                    receiver = currentUser?._id!!
                )

                chatViewModel.sendMessage(messageRequest, this).observe(this) {
                }

                messageTIET.setText("")
            }
        }
    }

    private fun getData() {
        chatViewModel.getMyMessages(currentConversation!!._id!!, this).observe(this) {
            messagesList = it as MutableList<MessageWithoutPopulate>
            println(it.size)

            chatRV.adapter = MessageAdapter(messagesList, currentConversation!!)
            chatRV.scrollToPosition(messagesList.size - 1)
        }
    }

    private val scope = MainScope()
    private var job: Job? = null

    private fun startUpdates() {
        job = scope.launch {
            while (true) {
                getData()
                delay(2500)
            }
        }
    }

    private fun stopUpdates() {
        job?.cancel()
        job = null
    }

    override fun finish() {
        super.finish()
        stopUpdates()
    }
}