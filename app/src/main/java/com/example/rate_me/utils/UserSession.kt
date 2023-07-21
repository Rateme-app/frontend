package com.example.rate_me.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.google.gson.Gson
import com.example.rate_me.api.models.User
import com.example.rate_me.api.viewModel.UserViewModel

object UserSession {

    private const val SHARED_PREF_SESSION = "SESSION"

    fun checkSessionExist(context: Context): Boolean {
        val sharedPreferences =
            context.getSharedPreferences(SHARED_PREF_SESSION, Activity.MODE_PRIVATE)
        return sharedPreferences.getString("USER_DATA", null) != null
    }

    fun getSession(context: Context): User {
        val sharedPreferences =
            context.getSharedPreferences(SHARED_PREF_SESSION, Activity.MODE_PRIVATE)
        val userData = sharedPreferences.getString("USER_DATA", null)
        return Gson().fromJson(userData, User::class.java)!!
    }

    fun saveSession(context: Context, user: User) {
        val sharedPreferences =
            context.getSharedPreferences(SHARED_PREF_SESSION, Activity.MODE_PRIVATE)
        val sharedPreferencesEditor: SharedPreferences.Editor =
            sharedPreferences.edit()
        sharedPreferencesEditor.putString("USER_DATA", Gson().toJson(user))
        sharedPreferencesEditor.apply()
    }

    fun removeSession(context: Context) {
        val sharedPreferences =
            context.getSharedPreferences(
                SHARED_PREF_SESSION,
                Context.MODE_PRIVATE
            )
        val sharedPreferencesEditor: SharedPreferences.Editor = sharedPreferences.edit()
        sharedPreferencesEditor.clear().apply()
    }

    fun refreshSession(
        context: Context,
        viewModelStoreOwner: ViewModelStoreOwner,
        lifecycleOwner: LifecycleOwner
    ) {
        val userViewModel = ViewModelProvider(viewModelStoreOwner)[UserViewModel::class.java]

        try {
            userViewModel.getById(getSession(context)._id!!, context).observe(lifecycleOwner) {
                saveSession(context, it)
            }
        } catch (e: NullPointerException) {
            removeSession(context)
        }
    }
}