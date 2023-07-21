package com.example.rate_me.adapters;

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rate_me.R
import com.example.rate_me.api.models.Conversation
import com.example.rate_me.api.models.MessageWithoutPopulate
import com.example.rate_me.api.models.User
import com.example.rate_me.utils.UserSession


class MessageAdapter(
    var items: MutableList<MessageWithoutPopulate>,
    var conversation: Conversation
) :
    RecyclerView.Adapter<MessageAdapter.ConversationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_message, parent, false)
        return ConversationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bindView(items[position], conversation)
    }

    override fun getItemCount(): Int = items.size

    class ConversationViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        private val lastMessageTV: TextView = itemView.findViewById(R.id.descriptionTV)
        private val userNameTV: TextView = itemView.findViewById(R.id.userNameTV)

        fun bindView(message: MessageWithoutPopulate, currentConversation: Conversation) {

            val sessionUser: User = UserSession.getSession(itemView.context)

            val conversation = message.senderConversation ?: message.receiverConversation

            if (conversation != null) {
                val isCurrentUserSender = conversation.sender == sessionUser._id

                if (isCurrentUserSender) {
                    (itemView as LinearLayout).gravity = Gravity.END
                    userNameTV.text =
                        "${currentConversation.sender?.firstname} ${currentConversation.sender?.lastname}"
                } else {
                    (itemView as LinearLayout).gravity = Gravity.START
                    userNameTV.text =
                        "${currentConversation.receiver?.firstname} ${currentConversation.receiver?.lastname}"
                }
            }

            itemView.setOnClickListener {

            }

            lastMessageTV.text = message.description

        }
    }
}