package com.example.rate_me.view.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rate_me.R
import com.example.rate_me.adapters.PostAdapter
import com.example.rate_me.api.models.Post
import com.example.rate_me.api.models.User
import com.example.rate_me.api.viewModel.PostViewModel
import com.example.rate_me.api.viewModel.RatingViewModel
import com.example.rate_me.utils.Constants
import com.example.rate_me.utils.ImageLoader
import com.example.rate_me.utils.UserSession
import com.example.rate_me.view.activities.EditProfileActivity
import com.example.rate_me.view.activities.LoginActivity
import com.facebook.shimmer.ShimmerFrameLayout

class MyProfileFragment : Fragment() {

    private lateinit var ratingViewModel: RatingViewModel
    private lateinit var postViewModel: PostViewModel

    private lateinit var fullName: TextView
    private lateinit var email: TextView
    private lateinit var logoutButton: Button
    private lateinit var editProfileButton: Button
    private lateinit var profileIV: ImageView
    private lateinit var averageRatingTV: TextView
    private lateinit var postsRV: RecyclerView
    private lateinit var postsSL: ShimmerFrameLayout

    private lateinit var sessionUser: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_my_profile, container, false)

        ratingViewModel = ViewModelProvider(this)[RatingViewModel::class.java]
        postViewModel = ViewModelProvider(this)[PostViewModel::class.java]

        fullName = view.findViewById(R.id.fullName)
        email = view.findViewById(R.id.email)
        logoutButton = view.findViewById(R.id.logoutButton)
        editProfileButton = view.findViewById(R.id.editProfileButton)
        profileIV = view.findViewById(R.id.profileIV)
        averageRatingTV = view.findViewById(R.id.averageRatingTV)
        postsRV = view.findViewById(R.id.postsRV)
        postsSL = view.findViewById(R.id.postsSL)

        postsSL.startShimmer()

        return view
    }

    override fun onResume() {
        super.onResume()
        initProfile()
    }

    @SuppressLint("SetTextI18n")
    fun initProfile() {
        sessionUser = UserSession.getSession(requireContext())

        ImageLoader.setImageFromUrl(
            profileIV, Constants.BASE_URL_IMAGES +
                    sessionUser.imageFilename
        )
        fullName.text = "${sessionUser.firstname} ${sessionUser.lastname}"
        email.text = sessionUser.email
        averageRatingTV.text = "This profile has no ratings yet"

        ratingViewModel.getAll(requireContext()).observe(this) { ratings ->
            val currentUserRatings = ratings.filter { rating ->
                rating.liked._id == sessionUser._id
            }

            // Calculate the average rating
            val totalRating = currentUserRatings.sumOf { it.value.toDouble() }.toFloat()
            val numRatings = currentUserRatings.size
            val averageRating = if (numRatings > 0) totalRating / numRatings else 0f

            // Set the average rating to the RatingBar
            averageRatingTV.text = "Average profile rating : $averageRating"
        }


        editProfileButton.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
        }

        logoutButton.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(getString(R.string.logout))
            builder.setMessage(R.string.logout_message)
            builder.setPositiveButton("Yes") { _, _ ->
                logout()
            }
            builder.setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            builder.create().show()
        }

        loadPosts()
    }

    private fun loadPosts() {
        postsRV.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        postViewModel.getAll(requireContext()).observe(this) {
            val filteredPosts = it.filter { post ->
                post.user?._id == sessionUser._id
            }

            postsRV.adapter = PostAdapter(filteredPosts as MutableList<Post>)
            postsSL.stopShimmer()
            postsSL.visibility = View.GONE
        }
    }

    private fun logout() {
        UserSession.removeSession(requireContext())

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}