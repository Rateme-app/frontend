package com.example.rate_me.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.VideoView
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.rate_me.R
import com.example.rate_me.api.models.Like
import com.example.rate_me.api.models.Post
import com.example.rate_me.api.models.User
import com.example.rate_me.api.viewModel.LikeViewModel
import com.example.rate_me.api.viewModel.PostViewModel
import com.example.rate_me.utils.Constants
import com.example.rate_me.utils.URIPathHelper
import com.example.rate_me.utils.UserSession
import com.example.rate_me.view.activities.MainActivity
import com.example.rate_me.view.fragments.PostsFragment
import com.example.rate_me.view.modals.CommentsModal
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date

class FullPostAdapter(
    private var items: List<Post>,
    private val parentFragment: PostsFragment
) :

    RecyclerView.Adapter<FullPostAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bindView(items[position], parentFragment)
    }

    override fun getItemCount(): Int = items.size

    class PostViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        private val videoView: VideoView = itemView.findViewById(R.id.videoView)
        private val postTitleTV: TextView = itemView.findViewById(R.id.postTitleTV)
        private val postDescriptionTV: TextView = itemView.findViewById(R.id.postDescriptionTV)
        private val deleteButton: TextView = itemView.findViewById(R.id.deleteButton)
        private val likeCountTV: TextView = itemView.findViewById(R.id.likeCountTV)
        private val likeButton: ImageButton = itemView.findViewById(R.id.likeButton)
        private val commentsButton: ImageButton = itemView.findViewById(R.id.commentsButton)
        private val shareButton: ImageButton = itemView.findViewById(R.id.shareButton)

        fun bindView(post: Post, parentFragment: PostsFragment) {

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


            val userSession = UserSession.getSession(itemView.context)

            if (post.user?._id == userSession._id) {
                deleteButton.setOnClickListener {
                    val postViewModel: PostViewModel =
                        ViewModelProvider(itemView.findViewTreeViewModelStoreOwner()!!)[PostViewModel::class.java]

                    postViewModel.delete(post._id!!, itemView.context)
                        .observe(itemView.findViewTreeLifecycleOwner()!!) {
                            parentFragment.loadData()
                            Snackbar.make(itemView, "Post Deleted", Snackbar.LENGTH_SHORT)
                                .show()
                        }
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
                                itemView.context,
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

                            itemView.context.startActivity(
                                Intent.createChooser(
                                    shareIntent,
                                    "Share"
                                )
                            )
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

            val user: User = UserSession.getSession(itemView.context)

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

                val likeViewModel: LikeViewModel =
                    ViewModelProvider(itemView.findViewTreeViewModelStoreOwner()!!)[LikeViewModel::class.java]
                likeViewModel.addOrRemove(
                    Like(
                        date = Date(),
                        user = user._id,
                        post = post._id,
                    ),
                    itemView.context
                ).observe(itemView.findViewTreeLifecycleOwner()!!) {


                }
            }

            commentsButton.setOnClickListener {
                val commentsModal = CommentsModal().newInstance(post)
                commentsModal.show(
                    (itemView.context as? MainActivity)!!.supportFragmentManager,
                    CommentsModal.TAG
                )
            }
        }
    }
}