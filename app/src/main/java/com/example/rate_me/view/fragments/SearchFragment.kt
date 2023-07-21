package com.example.rate_me.view.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rate_me.R
import com.example.rate_me.adapters.MusicAdapter
import com.example.rate_me.adapters.PostAdapter
import com.example.rate_me.adapters.UserAdapter
import com.example.rate_me.api.models.Music
import com.example.rate_me.api.models.Post
import com.example.rate_me.api.models.User
import com.example.rate_me.api.viewModel.MusicViewModel
import com.example.rate_me.api.viewModel.PostViewModel
import com.example.rate_me.api.viewModel.UserViewModel
import com.example.rate_me.utils.UserSession
import com.facebook.shimmer.ShimmerFrameLayout

class SearchFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var musicViewModel: MusicViewModel
    private lateinit var postViewModel: PostViewModel

    private lateinit var searchCardView: CardView
    private lateinit var searchView: SearchView
    private lateinit var postsRV: RecyclerView
    private lateinit var peopleRV: RecyclerView
    private lateinit var musicRV: RecyclerView
    private lateinit var postsSL: ShimmerFrameLayout
    private lateinit var peopleSL: ShimmerFrameLayout
    private lateinit var musicSL: ShimmerFrameLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        musicViewModel = ViewModelProvider(this)[MusicViewModel::class.java]
        postViewModel = ViewModelProvider(this)[PostViewModel::class.java]

        searchCardView = view.findViewById(R.id.searchCardView)
        searchView = view.findViewById(R.id.searchView)
        postsRV = view.findViewById(R.id.postsRV)
        peopleRV = view.findViewById(R.id.peopleRV)
        musicRV = view.findViewById(R.id.musicRV)
        postsSL = view.findViewById(R.id.postsSL)
        peopleSL = view.findViewById(R.id.peopleSL)
        musicSL = view.findViewById(R.id.musicSL)

        searchCardView.setOnClickListener {
            searchView.requestFocus()
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT)
        }

        searchView

        postsSL.startShimmer()
        peopleSL.startShimmer()
        musicSL.startShimmer()

        // Set up the search functionality
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                loadData(newText)
                return true
            }
        })

        return view
    }

    override fun onResume() {
        super.onResume()

        loadData("")
    }

    private fun loadData(searchText: String) {
        postsRV.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        postViewModel.getAll(requireContext()).observe(viewLifecycleOwner) {
            val filteredPosts = if (searchText.isNotEmpty()) {
                it.filter { post ->
                    post.title.contains(searchText, ignoreCase = true)
                }
            } else {
                it
            }

            postsRV.adapter = PostAdapter(filteredPosts as MutableList<Post>)
            postsSL.stopShimmer()
            postsSL.visibility = View.GONE
        }

        peopleRV.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        userViewModel.getAll(requireContext()).observe(viewLifecycleOwner) { userList ->
            val filteredUsers = if (searchText.isNotEmpty()) {
                userList.filter { user ->
                    user.username.contains(searchText, ignoreCase = true)
                }
            } else {
                userList
            }

            val currentUser = UserSession.getSession(requireContext())

            val filteredUserList: MutableList<User> =
                filteredUsers.filterNot { it._id == currentUser._id }.toMutableList()

            peopleRV.adapter = UserAdapter(filteredUserList)
            peopleSL.stopShimmer()
            peopleSL.visibility = View.GONE
        }


        musicRV.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        musicViewModel.getAll(requireContext()).observe(viewLifecycleOwner) {
            val filteredMusic = if (searchText.isNotEmpty()) {
                it.filter { music ->
                    music.title.contains(searchText, ignoreCase = true)
                }
            } else {
                it
            }

            musicRV.adapter = MusicAdapter(filteredMusic as MutableList<Music>)
            musicSL.stopShimmer()
            musicSL.visibility = View.GONE
        }
    }
}