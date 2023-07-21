package com.example.rate_me.view.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.hotpet.utils.AlertMaker
import com.example.rate_me.R
import com.example.rate_me.api.models.User
import com.example.rate_me.api.viewModel.UserViewModel
import com.example.rate_me.utils.Constants
import com.example.rate_me.utils.ImageLoader
import com.example.rate_me.utils.URIPathHelper
import com.example.rate_me.utils.UserSession
import com.google.android.material.textfield.TextInputEditText
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.Calendar


class EditProfileActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel

    private lateinit var profileIV: ImageView
    private lateinit var usernameTF: TextInputEditText
    private lateinit var firstnameTF: TextInputEditText
    private lateinit var lastnameTF: TextInputEditText
    private lateinit var editProfileButton: Button
    private lateinit var editProfilePictureButton: Button

    private lateinit var imageSelectionLauncher: ActivityResultLauncher<Intent>

    private lateinit var sessionUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        profileIV = findViewById(R.id.profileIV)
        usernameTF = findViewById(R.id.usernameTF)
        firstnameTF = findViewById(R.id.firstnameTF)
        lastnameTF = findViewById(R.id.lastnameTF)
        editProfileButton = findViewById(R.id.editProfileButton)
        editProfilePictureButton = findViewById(R.id.editProfilePictureButton)

        sessionUser = UserSession.getSession(this)

        imageSelectionLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val selectedImageUri = result.data?.data
                    val pathFromUri = selectedImageUri?.let { URIPathHelper().getPath(this, it) }

                    if (pathFromUri != null) {
                        val sourceFile = File(pathFromUri)

                        val requestBody: RequestBody =
                            sourceFile.asRequestBody("*/*".toMediaTypeOrNull())
                        val timeString: String =
                            java.lang.String.valueOf(Calendar.getInstance().timeInMillis)
                        val filename: RequestBody =
                            timeString.toRequestBody("text/plain".toMediaTypeOrNull())
                        val fileToUpload = MultipartBody.Part
                            .createFormData("picture", sourceFile.name, requestBody)
                        val userId = MultipartBody.Part.createFormData("userId", sessionUser._id!!)


                        userViewModel.updateProfilePicture(userId, filename, fileToUpload, this)
                            .observe(this) {
                                UserSession.saveSession(this, it)
                                sessionUser = it
                                initialize()
                            }

                    } else {
                        Toast.makeText(this, "Invalid file", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        initialize()
    }

    private fun initialize() {

        ImageLoader.setImageFromUrl(
            profileIV,
            Constants.BASE_URL_IMAGES + sessionUser.imageFilename
        )

        usernameTF.setText(sessionUser.username)
        firstnameTF.setText(sessionUser.firstname)
        lastnameTF.setText(sessionUser.lastname)

        editProfileButton.setOnClickListener {
            sessionUser.username = usernameTF.text.toString()
            sessionUser.firstname = firstnameTF.text.toString()
            sessionUser.lastname = lastnameTF.text.toString()

            if (inputControl()) {
                userViewModel.updateProfile(sessionUser, this).observe(this) {
                    UserSession.saveSession(this, it)
                    finish()
                }
            }
        }

        editProfilePictureButton.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imageSelectionLauncher.launch(intent)
        }
    }

    private fun inputControl(): Boolean {
        if (usernameTF.text.toString() == "") {
            AlertMaker.makeAlert(this, "Warning", "Username can't be empty")
            return false
        }

        if (firstnameTF.text.toString() == "") {
            AlertMaker.makeAlert(this, "Warning", "Firstname can't be empty")
            return false
        }

        if (lastnameTF.text.toString() == "") {
            AlertMaker.makeAlert(this, "Warning", "Lastname can't be empty")
            return false
        }

        return true
    }
}