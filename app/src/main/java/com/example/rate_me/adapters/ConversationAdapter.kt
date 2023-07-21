package com.example.rate_me.adapters;

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.rate_me.R
import com.example.rate_me.api.models.Conversation
import com.example.rate_me.api.viewModel.ChatViewModel
import com.example.rate_me.utils.Constants
import com.example.rate_me.utils.ImageLoader
import com.example.rate_me.view.activities.ChatActivity
import com.example.rate_me.view.fragments.ChatFragment

class ConversationAdapter(
    private var items: MutableList<Conversation>,
    private var fragment: ChatFragment
) :
    RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_conversation, parent, false)
        return ConversationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bindView(items[position], fragment)
    }

    override fun getItemCount(): Int = items.size

    class ConversationViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        private val profilePictureIV: ImageView = itemView.findViewById(R.id.profilePictureIV)
        private val conversationNameTV: TextView = itemView.findViewById(R.id.titleTV)
        private val lastMessageTV: TextView = itemView.findViewById(R.id.descriptionTV)
        private val deleteConversationButton: TextView =
            itemView.findViewById(R.id.deleteConversationButton)

        fun bindView(conversation: Conversation, parentFragment: ChatFragment) {

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ChatActivity::class.java)
                intent.putExtra("conversation", conversation)
                itemView.context.startActivity(intent)
            }

            if (conversation.receiver != null) {
                ImageLoader.setImageFromUrl(
                    profilePictureIV,
                    Constants.BASE_URL_IMAGES + conversation.receiver!!.imageFilename
                )
                conversationNameTV.text =
                    "${conversation.receiver!!.firstname} ${conversation.receiver!!.firstname}"
                lastMessageTV.text = conversation.lastMessage
            }

            deleteConversationButton.setOnClickListener {

                val chatViewModel: ChatViewModel =
                    ViewModelProvider(itemView.findViewTreeViewModelStoreOwner()!!)[ChatViewModel::class.java]

                chatViewModel.deleteConversation(conversation._id!!, itemView.context)
                    .observe(itemView.findViewTreeLifecycleOwner()!!) {
                        parentFragment.initialize()
                    }
            }
        }
    }
}