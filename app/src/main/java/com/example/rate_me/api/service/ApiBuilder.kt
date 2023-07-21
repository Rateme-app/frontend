package com.example.rate_me.api.service

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.rate_me.utils.Constants

object ApiBuilder {
    private val okHttpClient = OkHttpClient.Builder()
        // Add any custom OkHttpClient configurations if needed
        .build()

    private val retrofitBuilder = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)

    fun <T> buildService(serviceType: Class<T>): T {
        return retrofitBuilder.build().create(serviceType)
    }
}
