package com.example.rate_me.view.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rate_me.R
import com.example.rate_me.adapters.PostAdapter
import com.example.rate_me.api.models.Conversation
import com.example.rate_me.api.models.Post
import com.example.rate_me.api.models.Rating
import com.example.rate_me.api.models.User
import com.example.rate_me.api.viewModel.ChatViewModel
import com.example.rate_me.api.viewModel.PostViewModel
import com.example.rate_me.api.viewModel.RatingViewModel
import com.example.rate_me.api.viewModel.UserViewModel
import com.example.rate_me.utils.Constants
import com.example.rate_me.utils.ImageLoader
import com.example.rate_me.utils.UserSession
import com.facebook.shimmer.ShimmerFrameLayout
import java.util.Date

class ProfileActivity : AppCompatActivity() {

    private lateinit var ratingViewModel: RatingViewModel
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var postViewModel: PostViewModel

    private lateinit var fullName: TextView
    private lateinit var email: TextView
    private lateinit var sendMessageButton: Button
    private lateinit var profileIV: ImageView
    private lateinit var ratingBar: RatingBar
    private lateinit var averageRatingTV: TextView
    private lateinit var yourRatingTV: TextView
    private lateinit var postsRV: RecyclerView
    private lateinit var postsSL: ShimmerFrameLayout

    private lateinit var currentUser: User
    private lateinit var sessionUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        ratingViewModel = ViewModelProvider(this)[RatingViewModel::class.java]
        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        postViewModel = ViewModelProvider(this)[PostViewModel::class.java]

        fullName = findViewById(R.id.fullName)
        email = findViewById(R.id.email)
        sendMessageButton = findViewById(R.id.sendMessageButton)
        profileIV = findViewById(R.id.profileIV)
        ratingBar = findViewById(R.id.ratingBar)
        averageRatingTV = findViewById(R.id.averageRatingTV)
        yourRatingTV = findViewById(R.id.yourRatingTV)
        postsRV = findViewById(R.id.postsRV)
        postsSL = findViewById(R.id.postsSL)

        postsSL.startShimmer()

        sessionUser = UserSession.getSession(this)
        currentUser = intent.getSerializableExtra("user") as User

        initProfile()
    }

    @SuppressLint("SetTextI18n")
    fun initProfile() {

        ImageLoader.setImageFromUrl(
            profileIV, Constants.BASE_URL_IMAGES +
                    currentUser.imageFilename
        )
        fullName.text = "${currentUser.firstname} ${currentUser.lastname}"
        email.text = currentUser.email
        averageRatingTV.text = "This profile has no ratings yet"

        ratingViewModel.getAll(this).observe(this) { ratings ->
            val yourRating = ratings.firstOrNull { rating ->
                rating.liker._id == sessionUser._id && rating.liked._id == currentUser._id
            }
            ratingBar.rating = yourRating?.value ?: 0.0f

            val currentUserRatings = ratings.filter { rating ->
                rating.liked._id == currentUser._id
            }
            // Calculate the average rating
            val totalRating = currentUserRatings.sumOf { it.value.toDouble() }.toFloat()
            val numRatings = currentUserRatings.size
            val averageRating = if (numRatings > 0) totalRating / numRatings else 0f

            // Set the average rating to the RatingBar
            averageRatingTV.text = "Average profile rating : $averageRating"
        }


        sendMessageButton.setOnClickListener {
            println("SENDING MESSAGE")

            chatViewModel.createConversation(
                Conversation(
                    sender = sessionUser,
                    receiver = currentUser,
                    lastMessage = "",
                    lastMessageDate = Date(),
                ),
                this
            ).observe(this) {
                // Store the boolean value indicating the previous activity in shared preferences
                val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putBoolean(Constants.PREF_GO_TO_CHAT, true)
                editor.apply()

                finish()
                Toast.makeText(this, "New conversation has been added", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
            println("SENDING RATING FROM USER : $fromUser")

            if (fromUser) {
                val newRating = Rating(
                    description = "from ${sessionUser.email} to ${currentUser.email}",
                    date = Date(),
                    value = rating,
                    liker = sessionUser,
                    liked = currentUser
                )

                // Call a method in the ratingViewModel to add the rating
                ratingViewModel.addOrUpdate(newRating, this).observe(this) {
                    initProfile()
                }
            }
        }

        loadPosts()
    }

    private fun loadPosts() {
        postsRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        postViewModel.getAll(this).observe(this) {
            val filteredPosts = it.filter { post ->
                post.user?._id == currentUser._id
            }

            postsRV.adapter = PostAdapter(filteredPosts as MutableList<Post>)
            postsSL.stopShimmer()
            postsSL.visibility = View.GONE
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            UserSession.saveSession(this, currentUser)

            val intent = Intent(this@ProfileActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
        }
        return true
    }
}