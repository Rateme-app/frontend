package com.example.rate_me.adapters;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rate_me.R
import com.example.rate_me.api.models.Comment

class CommentAdapter(var items: MutableList<Comment>) :
    RecyclerView.Adapter<CommentAdapter.ConversationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_comment, parent, false)
        return ConversationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bindView(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ConversationViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        private val conversationNameTV: TextView = itemView.findViewById(R.id.titleTV)
        private val lastMessageTV: TextView = itemView.findViewById(R.id.descriptionTV)

        fun bindView(item: Comment) {

            itemView.setOnClickListener {

            }

            conversationNameTV.text = "User : ${item.user?.firstname} ${item.user?.lastname}"
            lastMessageTV.text = item.description
        }
    }
}