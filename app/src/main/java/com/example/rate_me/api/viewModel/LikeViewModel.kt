package com.example.rate_me.api.viewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rate_me.api.models.Like
import com.example.rate_me.api.models.Post
import com.example.rate_me.api.service.ApiBuilder
import com.example.rate_me.api.service.LikeService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LikeViewModel : ViewModel() {

    private val likeService = ApiBuilder.buildService(LikeService::class.java)

    fun addOrRemove(like: Like, context: Context): LiveData<Boolean> {
        val isGood = MutableLiveData<Boolean>()
        isGood.value = false

        val call = likeService.addOrRemove(like)
        call.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    isGood.value = true
                } else {
                    Toast.makeText(
                        context,
                        "Network error : ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.d("API", "${t.message}")
                Toast.makeText(
                    context,
                    "Api failure : ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        return isGood
    }
}
