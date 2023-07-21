package com.example.rate_me.view.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.hotpet.utils.AlertMaker
import com.example.rate_me.R
import com.example.rate_me.api.service.UserService
import com.example.rate_me.api.viewModel.UserViewModel
import com.google.android.material.textfield.TextInputEditText

class ForgotFirstActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel

    private lateinit var emailTF: TextInputEditText
    private lateinit var nextBtn: Button
    private var resetCode: Int = (1000 until 9999).random()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_first)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        nextBtn = findViewById(R.id.nextBtn)
        emailTF = findViewById(R.id.emailTF)

        println("RESET CODE IS : $resetCode")

        nextBtn.setOnClickListener {
            if (inputControl()) {
                userViewModel.forgotPassword(
                    UserService.ForgotPasswordBody(
                        resetCode.toString(),
                        emailTF.text.toString()
                    ),
                    this
                ).observe(this) {
                    val intent = Intent(baseContext, ForgotSecondActivity::class.java)
                    intent.putExtra("RESET_CODE", resetCode)
                    intent.putExtra("EMAIL", emailTF.text.toString())
                    startActivity(intent)

                    finish()
                }
            }
        }
    }

    private fun inputControl(): Boolean {
        if (emailTF.text.toString() == "") {
            AlertMaker.makeAlert(this, "Warning", "Email can't be empty")
            return false
        }

        val emailPattern = Regex("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b")
        if (!emailTF.text.toString().matches(emailPattern)) {
            AlertMaker.makeAlert(this, "Warning", "Invalid email format")
            return false
        }

        return true
    }
}