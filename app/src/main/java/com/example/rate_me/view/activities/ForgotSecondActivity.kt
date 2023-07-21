package com.example.rate_me.view.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.hotpet.utils.AlertMaker
import com.example.rate_me.R
import com.google.android.material.textfield.TextInputEditText

class ForgotSecondActivity : AppCompatActivity() {

    private lateinit var resetCodeTF: TextInputEditText
    private lateinit var nextBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_second)

        resetCodeTF = findViewById(R.id.resetCodeTF)
        nextBtn = findViewById(R.id.nextBtn)

        val resetCode = intent.getIntExtra("RESET_CODE", 0).toString()
        val email = intent.getStringExtra("EMAIL")

        nextBtn.setOnClickListener {
            if (inputControl()) {
                if (resetCode == resetCodeTF.text.toString()) {
                    val intent = Intent(baseContext, ForgotThirdActivity::class.java)
                    intent.putExtra("EMAIL", email!!)
                    startActivity(intent)

                    finish()
                } else {
                    AlertMaker.makeAlert(this, "Warning", "Reset code wrong")
                }
            }
        }
    }


    private fun inputControl(): Boolean {
        if (resetCodeTF.text.toString() == "") {
            AlertMaker.makeAlert(this, "Warning", "Reset code can't be empty")
            return false
        }

        return true
    }
}