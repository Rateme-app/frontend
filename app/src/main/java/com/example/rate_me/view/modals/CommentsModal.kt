package com.example.rate_me.view.modals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.example.rate_me.R
import com.example.rate_me.adapters.CommentAdapter
import com.example.rate_me.api.models.Comment
import com.example.rate_me.api.models.Post
import com.example.rate_me.api.models.User
import com.example.rate_me.api.viewModel.CommentViewModel
import com.example.rate_me.utils.UserSession
import java.util.Calendar

class CommentsModal : BottomSheetDialogFragment() {

    private lateinit var commentViewModel: CommentViewModel

    companion object {
        const val TAG = "CommentsModal"
    }

    fun newInstance(
        post: Post?
    ): CommentsModal {
        val args = Bundle()
        args.putSerializable("post", post)
        val fragment = CommentsModal()
        fragment.arguments = args
        return fragment
    }

    private var commentsRV: RecyclerView? = null
    private var addButton: Button? = null
    private var commentTIET: TextInputEditText? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.modal_comments, container, false)

        commentViewModel = ViewModelProvider(this)[CommentViewModel::class.java]

        commentsRV = view.findViewById(R.id.commentsRV)
        addButton = view.findViewById(R.id.addButton)
        commentTIET = view.findViewById(R.id.commentTIET)

        val post = arguments?.getSerializable("post") as Post?

        val commentsList: MutableList<Comment> = post!!.comments as MutableList<Comment>

        commentsRV!!.adapter = CommentAdapter(commentsList)
        commentsRV!!.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val user: User = UserSession.getSession(requireContext())

        addButton!!.setOnClickListener {
            val comment = Comment(
                Calendar.getInstance().time,
                commentTIET!!.text.toString(),
                post._id,
                user
            )

            commentViewModel.add(comment, requireContext()).observe(this) {
                commentsList.add(comment)
                commentTIET!!.setText("")
                commentsRV!!.adapter?.notifyDataSetChanged()
            }
        }

        return view
    }
}