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
import com.example.rate_me.api.models.Music
import com.example.rate_me.api.service.ApiBuilder
import com.example.rate_me.api.service.MusicService

class MusicViewModel : ViewModel() {

    private val musicService = ApiBuilder.buildService(MusicService::class.java)

    fun getAll(context: Context): LiveData<List<Music>> {
        val musicsLiveData = MutableLiveData<List<Music>>()

        val call = musicService.getAll()
        call.enqueue(object : Callback<List<Music>> {
            override fun onResponse(call: Call<List<Music>>, response: Response<List<Music>>) {
                if (response.isSuccessful) {
                    musicsLiveData.value = response.body()
                } else {
                    Toast.makeText(
                        context,
                        "Network error : ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Music>>, t: Throwable) {
                Log.d("API", "${t.message}")
                Toast.makeText(
                    context,
                    "Api failure : ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        return musicsLiveData
    }
}