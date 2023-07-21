package com.example.rate_me.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rate_me.R
import com.example.rate_me.adapters.ConversationAdapter
import com.example.rate_me.api.models.Conversation
import com.example.rate_me.api.models.User
import com.example.rate_me.api.viewModel.ChatViewModel
import com.example.rate_me.utils.UserSession
import com.facebook.shimmer.ShimmerFrameLayout

class ChatFragment : Fragment() {

    private lateinit var chatViewModel: ChatViewModel

    private var conversationsRV: RecyclerView? = null
    private var shimmerFrameLayout: ShimmerFrameLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]

        // VIEW BINDING
        shimmerFrameLayout = view.findViewById(R.id.shimmerLayout)
        conversationsRV = view.findViewById(R.id.conversationsRV)

        shimmerFrameLayout!!.startShimmer()
        conversationsRV!!.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        initialize()

        return view
    }

    fun initialize() {
        val user: User = UserSession.getSession(requireContext())

        chatViewModel.getMyConversations(user._id!!, requireContext()).observe(viewLifecycleOwner) {
            conversationsRV!!.adapter = ConversationAdapter(it as MutableList<Conversation>, this)

            shimmerFrameLayout!!.stopShimmer()
            shimmerFrameLayout!!.visibility = View.GONE
        }
    }
}