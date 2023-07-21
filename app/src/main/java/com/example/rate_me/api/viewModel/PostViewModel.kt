package com.example.rate_me.api.viewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rate_me.api.models.Post
import com.example.rate_me.api.service.ApiBuilder
import com.example.rate_me.api.service.PostService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostViewModel : ViewModel() {

    private val postService = ApiBuilder.buildService(PostService::class.java)

    fun getAll(context: Context): LiveData<List<Post>> {
        val postsLiveData = MutableLiveData<List<Post>>()

        val call = postService.getAll()
        call.enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (response.isSuccessful) {
                    postsLiveData.value = response.body()
                } else {
                    Toast.makeText(
                        context,
                        "Network error : ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Log.d("API", "${t.message}")
                Toast.makeText(
                    context,
                    "Api failure : ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        return postsLiveData
    }

    fun add(
        fileToUpload: MultipartBody.Part,
        titleData: MultipartBody.Part,
        descriptionData: MultipartBody.Part,
        userIdData: MultipartBody.Part,
        filename: RequestBody,
        context: Context
    ): LiveData<Post> {
        val resultLiveData = MutableLiveData<Post>()

        postService.add(fileToUpload, titleData, descriptionData, userIdData, filename)
            .enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (response.isSuccessful && response.code() == 200) {
                        resultLiveData.value = response.body()
                    } else {
                        Toast.makeText(
                            context,
                            "Network error : ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    Log.d("API", "${t.message}")
                    Toast.makeText(
                        context,
                        "Api failure : ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        return resultLiveData
    }

    fun delete(postId: String, context: Context): LiveData<Unit> {
        val resultLiveData = MutableLiveData<Unit>()

        val call = postService.delete(postId)
        call.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    resultLiveData.value = Unit
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

        return resultLiveData
    }


}
