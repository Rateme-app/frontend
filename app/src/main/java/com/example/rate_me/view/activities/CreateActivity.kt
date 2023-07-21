package com.example.rate_me.view.activities

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.hotpet.utils.AlertMaker
import com.example.rate_me.R
import com.example.rate_me.api.viewModel.PostViewModel
import com.example.rate_me.utils.URIPathHelper
import com.example.rate_me.utils.UserSession
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.Calendar

class CreateActivity : AppCompatActivity() {

    private lateinit var postViewModel: PostViewModel

    private lateinit var postIV: ImageView
    private lateinit var titleTF: TextInputEditText
    private lateinit var descriptionTF: TextInputEditText
    private lateinit var addPostBtn: Button
    private lateinit var chooseVideoButton: FloatingActionButton

    private var videoUri: Uri? = null

    companion object {
        const val GALLERY_RESULT = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        postViewModel = ViewModelProvider(this)[PostViewModel::class.java]

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            123
        )

        postIV = findViewById(R.id.postIV)
        titleTF = findViewById(R.id.titleTF)
        descriptionTF = findViewById(R.id.descriptionTF)
        addPostBtn = findViewById(R.id.addPostBtn)
        chooseVideoButton = findViewById(R.id.chooseVideoButton)

        chooseVideoButton.setOnClickListener {
            openSystemGalleryToSelectAVideo()
        }

        addPostBtn.setOnClickListener {
            if (inputControl()) {
                addPost()
            }
        }
    }

    private fun addPost() {
        val pathFromUri = videoUri?.let { URIPathHelper().getPath(this, it) }

        if (pathFromUri != null) {
            val sourceFile = File(pathFromUri)

            val timeString: String = java.lang.String.valueOf(Calendar.getInstance().timeInMillis)

            val requestBody: RequestBody = sourceFile.asRequestBody("*/*".toMediaTypeOrNull())
            val fileToUpload = MultipartBody.Part
                .createFormData("video", sourceFile.name, requestBody)
            val titleData = MultipartBody.Part.createFormData("title", titleTF.text.toString())
            val descriptionData =
                MultipartBody.Part.createFormData("description", descriptionTF.text.toString())
            val userIdData =
                MultipartBody.Part.createFormData("userId", UserSession.getSession(this)._id!!)
            val filename: RequestBody = timeString.toRequestBody("text/plain".toMediaTypeOrNull())

            postViewModel.add(fileToUpload, titleData, descriptionData, userIdData, filename, this)
                .observe(this) {
                    finish()
                }
        } else {
            Toast.makeText(this, "Video invalid", Toast.LENGTH_SHORT).show()
        }
    }

    private fun inputControl(): Boolean {
        if (titleTF.text.toString() == "") {
            AlertMaker.makeAlert(this, "Warning", "Title can't be empty")
            return false
        }

        if (descriptionTF.text.toString() == "") {
            AlertMaker.makeAlert(this, "Warning", "Description can't be empty")
            return false
        }

        return true
    }

    private fun openSystemGalleryToSelectAVideo() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "video/*")

        try {
            @Suppress("DEPRECATION")
            startActivityForResult(intent, GALLERY_RESULT)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(
                baseContext,
                "No Gallery APP installed",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == GALLERY_RESULT) {
            videoUri = data?.data ?: return
            val bitmap = getVideoThumbnail(baseContext, videoUri!!)
            postIV.setImageBitmap(bitmap)
            postIV.scaleType = ImageView.ScaleType.CENTER_CROP
        }

        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getVideoThumbnail(context: Context, videoUri: Uri): Bitmap? {
        val mMMR = MediaMetadataRetriever()
        mMMR.setDataSource(context, videoUri)

        return mMMR.getScaledFrameAtTime(
            -1,
            MediaMetadataRetriever.OPTION_CLOSEST_SYNC,
            500,
            500
        )
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_out_down, R.anim.slide_in_down)
    }
}