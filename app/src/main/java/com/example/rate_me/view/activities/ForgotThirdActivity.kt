package com.example.rate_me.view.activities

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.hotpet.utils.AlertMaker
import com.example.rate_me.R
import com.example.rate_me.api.service.UserService
import com.example.rate_me.api.viewModel.UserViewModel
import com.google.android.material.textfield.TextInputEditText

class ForgotThirdActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel

    private lateinit var passwordTF: TextInputEditText
    private lateinit var passwordConfirmationTF: TextInputEditText
    private lateinit var nextBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_third)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        passwordTF = findViewById(R.id.passwordTF)
        passwordConfirmationTF = findViewById(R.id.passwordConfirmationTF)
        nextBtn = findViewById(R.id.nextBtn)

        val email = intent.getStringExtra("EMAIL")


        nextBtn.setOnClickListener {
            if (inputControl()) {
                userViewModel.updatePassword(
                    UserService.UpdatePasswordBody(
                        email!!,
                        passwordTF.text.toString()
                    ),
                    this
                ).observe(this) {
                    Toast.makeText(baseContext, "Password changed successfully", Toast.LENGTH_LONG)
                        .show()
                    finish()
                }
            }
        }
    }

    private fun inputControl(): Boolean {
        if (passwordTF.text.toString() == "") {
            AlertMaker.makeAlert(this, "Warning", "Password can't be empty")
            return false
        }

        if (passwordTF.text.toString().length < 4) {
            AlertMaker.makeAlert(this, "Warning", "Password must have at least 4 characters")
            return false
        }

        if (passwordTF.text.toString() != passwordConfirmationTF.text.toString()) {
            AlertMaker.makeAlert(this, "Warning", "Password and confirmation should match")
            return false
        }

        return true
    }
}