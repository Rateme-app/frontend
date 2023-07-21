package com.example.rate_me.view.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.hotpet.utils.AlertMaker
import com.example.rate_me.R
import com.example.rate_me.api.models.User
import com.example.rate_me.api.viewModel.UserViewModel
import com.google.android.material.textfield.TextInputEditText


class RegisterActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel

    private lateinit var emailTF: TextInputEditText
    private lateinit var passwordTF: TextInputEditText
    private lateinit var usernameTF: TextInputEditText
    private lateinit var firstnameTF: TextInputEditText
    private lateinit var lastnameTF: TextInputEditText
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        emailTF = findViewById(R.id.emailTF)
        passwordTF = findViewById(R.id.passwordTF)
        usernameTF = findViewById(R.id.usernameTF)
        firstnameTF = findViewById(R.id.firstnameTF)
        lastnameTF = findViewById(R.id.lastnameTF)
        registerButton = findViewById(R.id.registerButton)

        registerButton.setOnClickListener {

            if (inputControl()) {
                userViewModel.register(
                    User(
                        email = emailTF.text.toString(),
                        password = passwordTF.text.toString(),
                        username = usernameTF.text.toString(),
                        firstname = firstnameTF.text.toString(),
                        lastname = lastnameTF.text.toString(),
                    ),
                    this
                ).observe(this) {
                    val intent =
                        Intent(this@RegisterActivity, LoginActivity::class.java)
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

        if (passwordTF.text.toString() == "") {
            AlertMaker.makeAlert(this, "Warning", "Password can't be empty")
            return false
        }

        if (passwordTF.text.toString().length < 4) {
            AlertMaker.makeAlert(this, "Warning", "Password must have at least 4 characters")
            return false
        }

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