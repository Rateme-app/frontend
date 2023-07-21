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
import com.example.rate_me.api.models.Rating
import com.example.rate_me.api.service.ApiBuilder
import com.example.rate_me.api.service.RatingService

class RatingViewModel : ViewModel() {

    private val ratingService = ApiBuilder.buildService(RatingService::class.java)

    fun getAll(context: Context): LiveData<List<Rating>> {
        val liveData = MutableLiveData<List<Rating>>()

        ratingService.getAll().enqueue(object : Callback<List<Rating>> {
            override fun onResponse(call: Call<List<Rating>>, response: Response<List<Rating>>) {
                if (response.isSuccessful) {
                    liveData.value = response.body()
                } else {
                    Toast.makeText(
                        context,
                        "Network error : ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Rating>>, t: Throwable) {
                Log.d("API", "${t.message}")
                Toast.makeText(
                    context,
                    "Api failure : ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        return liveData
    }

    fun addOrUpdate(rating: Rating, context: Context): LiveData<Rating> {
        val liveData = MutableLiveData<Rating>()

        ratingService.addOrUpdate(rating).enqueue(object : Callback<Rating> {
            override fun onResponse(call: Call<Rating>, response: Response<Rating>) {
                if (response.isSuccessful) {
                    liveData.value = response.body()
                } else {
                    Toast.makeText(
                        context,
                        "Network error : ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Rating>, t: Throwable) {
                Log.d("API", "${t.message}")
                Toast.makeText(
                    context,
                    "Api failure : ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        return liveData
    }
}
