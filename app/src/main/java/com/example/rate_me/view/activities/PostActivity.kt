package com.example.rate_me.view.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.example.hotpet.utils.AlertMaker
import com.example.rate_me.R
import com.example.rate_me.api.models.Like
import com.example.rate_me.api.models.Post
import com.example.rate_me.api.models.User
import com.example.rate_me.api.viewModel.LikeViewModel
import com.example.rate_me.api.viewModel.PostViewModel
import com.example.rate_me.utils.Constants
import com.example.rate_me.utils.URIPathHelper
import com.example.rate_me.utils.UserSession
import com.example.rate_me.view.modals.CommentsModal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date

class PostActivity : AppCompatActivity() {

    private lateinit var postViewModel: PostViewModel
    private lateinit var likeViewModel: LikeViewModel

    private lateinit var videoView: VideoView
    private lateinit var postTitleTV: TextView
    private lateinit var postDescriptionTV: TextView
    private lateinit var deleteButton: TextView
    private lateinit var shareButton: ImageButton
    private lateinit var likeCountTV: TextView
    private lateinit var likeButton: ImageButton
    private lateinit var commentsButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        postViewModel = ViewModelProvider(this)[PostViewModel::class.java]
        likeViewModel = ViewModelProvider(this)[LikeViewModel::class.java]

        videoView = findViewById(R.id.videoView)
        postTitleTV = findViewById(R.id.postTitleTV)
        postDescriptionTV = findViewById(R.id.postDescriptionTV)
        deleteButton = findViewById(R.id.deleteButton)
        shareButton = findViewById(R.id.shareButton)
        likeCountTV = findViewById(R.id.likeCountTV)
        likeButton = findViewById(R.id.likeButton)
        commentsButton = findViewById(R.id.commentsButton)

        initialize()
    }

    private fun initialize() {
        val post = intent.getSerializableExtra("post") as Post

        videoView.setVideoURI(Uri.parse(Constants.BASE_URL_VIDEOS + post.videoFilename))
        videoView.setOnPreparedListener { mp ->
            mp.start()
            val videoRatio = mp.videoWidth / mp.videoHeight.toFloat()
            val screenRatio: Float = videoView.width / videoView.height.toFloat()
            val scale = videoRatio / screenRatio
            if (scale >= 1f) {
                videoView.scaleX = scale
            } else {
                videoView.scaleY = 1f / scale
            }
        }
        videoView.setOnCompletionListener { mp -> mp.start() }

        postTitleTV.text = post.title
        postDescriptionTV.text = post.description


        val userSession = UserSession.getSession(this)

        if (post.user?._id == userSession._id) {
            deleteButton.setOnClickListener {
                postViewModel.delete(post._id!!, this)
                    .observe(this) { finish() }
            }
        } else {
            deleteButton.visibility = View.GONE
        }


        shareButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val path = URIPathHelper().getTempVideoFilePathFromUrl(
                    Constants.BASE_URL_VIDEOS + post.videoFilename
                )

                if (path != null) {
                    val file = File(path)
                    if (file.exists()) {
                        val uri = FileProvider.getUriForFile(
                            baseContext,
                            "com.example.rate_me.fileprovider",
                            file
                        )

                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.type = "video/*"
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "title")
                        shareIntent.putExtra(Intent.EXTRA_TITLE, "title")
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)

                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                        startActivity(Intent.createChooser(shareIntent, "Share"))
                    } else {
                        println("File does not exist")
                    }
                } else {
                    println("Path is null")
                }
            }
        }

        var userHasLike = false
        var currentLikeCount = post.likes!!.size

        val user: User = UserSession.getSession(this)

        for (like in post.likes) {
            if (like.user == user._id) {
                likeButton.setImageResource(R.drawable.ic_favorite)
                userHasLike = true
            }
        }

        likeCountTV.text = currentLikeCount.toString()

        likeButton.setOnClickListener {

            if (userHasLike) {
                currentLikeCount -= 1
                likeCountTV.text = currentLikeCount.toString()
                likeButton.setImageResource(R.drawable.ic_favorite_border)
            } else {
                currentLikeCount += 1
                likeCountTV.text = currentLikeCount.toString()
                likeButton.setImageResource(R.drawable.ic_favorite)
            }
            userHasLike = !userHasLike

            likeViewModel.addOrRemove(
                Like(
                    date = Date(),
                    user = user._id,
                    post = post._id,
                ),
                this
            ).observe(this) {


            }
        }

        commentsButton.setOnClickListener {
            val commentsModal = CommentsModal().newInstance(post)
            commentsModal.show(supportFragmentManager, CommentsModal.TAG)
        }
    }
}