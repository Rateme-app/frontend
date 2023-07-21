package com.example.rate_me.view.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.hotpet.utils.AlertMaker
import com.example.rate_me.R
import com.example.rate_me.api.service.UserService
import com.example.rate_me.api.viewModel.UserViewModel
import com.example.rate_me.utils.AlertUtils
import com.example.rate_me.utils.UserSession
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel

    // keytool -keystore %USERPROFILE%\.android\debug.keystore -list -v
    private lateinit var googleSignInButton: SignInButton
    private val rcSignIn = 9001

    private lateinit var emailTF: TextInputEditText
    private lateinit var passwordTF: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var forgotPasswordButton: Button
    private lateinit var signupButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        googleSignInButton = findViewById(R.id.googleSignInButton)
        googleSignInButton.setSize(SignInButton.SIZE_STANDARD)
        emailTF = findViewById(R.id.emailTF)
        passwordTF = findViewById(R.id.passwordTF)
        loginButton = findViewById(R.id.loginButton)
        forgotPasswordButton = findViewById(R.id.forgotPasswordButton)
        signupButton = findViewById(R.id.signupButton)

        loginButton.setOnClickListener {
            if (inputControl()) {
                login()
            }
        }

        signupButton.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        forgotPasswordButton.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotFirstActivity::class.java)
            startActivity(intent)
        }

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        googleSignInButton = findViewById(R.id.googleSignInButton)
        googleSignInButton.setSize(SignInButton.SIZE_STANDARD)
        googleSignInButton.setOnClickListener {
            val signInIntent: Intent = googleSignInClient.signInIntent
            @Suppress("DEPRECATION")
            startActivityForResult(signInIntent, rcSignIn)
        }
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == rcSignIn) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            println("Login as ${account.email}")
            account.email?.let {
                val givenName = account.givenName
                val familyName = account.familyName

                userViewModel.loginWithSocial(it, givenName!!, familyName!!, this)
                    .observe(this) { user ->
                        if (user != null) {
                            UserSession.saveSession(this, user)

                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
            }
        } catch (e: ApiException) {
            Log.w("GSI", "signInResult:failed code=" + e.statusCode)
        }
    }


    private fun login() {
        userViewModel.login(
            UserService.LoginBody(
                emailTF.text.toString(),
                passwordTF.text.toString()
            ),
            this
        ).observe(this) { user ->
            if (user != null) {
                UserSession.saveSession(this, user)

                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                AlertUtils.showAlert(
                    this@LoginActivity,
                    "Login Failed",
                    "Invalid credentials"
                )
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

        return true
    }

}