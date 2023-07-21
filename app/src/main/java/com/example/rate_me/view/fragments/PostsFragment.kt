package com.example.rate_me.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.rate_me.R
import com.example.rate_me.adapters.FullPostAdapter
import com.example.rate_me.api.viewModel.PostViewModel
import com.facebook.shimmer.ShimmerFrameLayout

class PostsFragment : Fragment() {

    private lateinit var postViewModel: PostViewModel

    private var viewPagerVideos: ViewPager2? = null
    private var postsSL: ShimmerFrameLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_posts, container, false)

        postViewModel = ViewModelProvider(this)[PostViewModel::class.java]

        // FIND VIEWS
        viewPagerVideos = view.findViewById(R.id.viewPagerVideos)
        postsSL = view.findViewById(R.id.postsSL)

        return view
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    fun loadData() {
        postsSL!!.startShimmer()

        postViewModel.getAll(requireContext()).observe(viewLifecycleOwner) {
            viewPagerVideos!!.adapter = FullPostAdapter(it.reversed(), this)

            postsSL!!.stopShimmer()
            postsSL!!.visibility = View.GONE
        }
    }
}