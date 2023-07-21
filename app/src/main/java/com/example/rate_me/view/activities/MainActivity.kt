package com.example.rate_me.view.activities

import android.Manifest
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.rate_me.R
import com.example.rate_me.utils.Constants
import com.example.rate_me.view.fragments.ChatFragment
import com.example.rate_me.view.fragments.MyProfileFragment
import com.example.rate_me.view.fragments.PostsFragment
import com.example.rate_me.view.fragments.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private var bottomNavigation: BottomNavigationView? = null
    private var fab: FloatingActionButton? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                1
            )

        setSupportActionBar(findViewById(R.id.my_toolbar))

        bottomNavigation = findViewById(R.id.bottom_navigation)

        fab = findViewById(R.id.fab)
        fab!!.setOnClickListener {
            val intent = Intent(this@MainActivity, CreateActivity::class.java)
            startActivity(
                intent,
                ActivityOptions.makeCustomAnimation(
                    baseContext,
                    R.anim.slide_in_up,
                    R.anim.slide_out_up
                ).toBundle()
            )
        }

        bottomNavigation!!.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_page -> {
                    supportActionBar?.title = "Posts for you"
                    setFragment(PostsFragment())
                    true
                }

                R.id.search_page -> {
                    supportActionBar?.title = "Search anything"
                    setFragment(SearchFragment())
                    true
                }

                R.id.chat_page -> {
                    supportActionBar?.title = "Talk to friends"
                    setFragment(ChatFragment())
                    true
                }

                R.id.profile_page -> {
                    supportActionBar?.title = "Profile"
                    setFragment(MyProfileFragment())
                    true
                }

                else -> false
            }
        }

        setFragment(PostsFragment())
    }

    override fun onResume() {
        super.onResume()

        // Retrieve the boolean value indicating the previous activity from shared preferences
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val previousActivity = sharedPreferences.getBoolean(Constants.PREF_GO_TO_CHAT, false)

        if (previousActivity) {
            bottomNavigation!!.selectedItemId = R.id.chat_page
            supportActionBar?.title = "Talk to friends"
            setFragment(ChatFragment())
        }

        // Reset the boolean value
        val editor = sharedPreferences.edit()
        editor.putBoolean(Constants.PREF_GO_TO_CHAT, false)
        editor.apply()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(intent)
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainer, fragment)
                .setCustomAnimations(
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
                ).commit()

        }
    }
}