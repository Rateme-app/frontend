package com.example.rate_me.utils

import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import java.util.concurrent.Executors

object ImageLoader {

    fun setImageFromUrl(imageView: ImageView, url: String) {
        Executors.newSingleThreadExecutor().execute {
            try {
                val image = BitmapFactory.decodeStream(java.net.URL(url).openStream())

                Handler(Looper.getMainLooper()).post {
                    imageView.setImageBitmap(image)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}