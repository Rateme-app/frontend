package com.example.rate_me.api.viewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.rate_me.api.models.Comment
import com.example.rate_me.api.service.ApiBuilder
import com.example.rate_me.api.service.CommentService

class CommentViewModel : ViewModel() {

    private val commentService = ApiBuilder.buildService(CommentService::class.java)

    fun add(comment: Comment, context: Context): LiveData<Comment> {
        val commentLiveData = MutableLiveData<Comment>()

        val call = commentService.add(comment)
        call.enqueue(object : Callback<Comment> {
            override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                if (response.isSuccessful) {
                    commentLiveData.value = response.body()
                } else {
                    Toast.makeText(
                        context,
                        "Network error : ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Comment>, t: Throwable) {
                Log.d("API", "${t.message}")
                Toast.makeText(
                    context,
                    "Api failure : ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        return commentLiveData
    }
}
