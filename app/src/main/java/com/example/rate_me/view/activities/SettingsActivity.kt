package com.example.rate_me.view.activities

import android.app.UiModeManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import com.example.rate_me.R
import com.example.rate_me.api.models.User
import com.example.rate_me.api.viewModel.UserViewModel
import com.example.rate_me.utils.UserSession
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions


class SettingsActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel

    private lateinit var darkModeSwitch: SwitchCompat
    private lateinit var uiModeManager: UiModeManager

    private lateinit var generateQRButton: Button
    private lateinit var scanQRButton: Button
    private lateinit var qrIV: ImageView

    private lateinit var sessionUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        uiModeManager = getSystemService(UI_MODE_SERVICE) as UiModeManager
        sessionUser = UserSession.getSession(this)

        generateQRButton = findViewById(R.id.generateQRButton)
        scanQRButton = findViewById(R.id.scanQRButton)
        qrIV = findViewById(R.id.qrIV)
        darkModeSwitch = findViewById(R.id.darkModeSwitch)


        darkModeSwitch.setOnClickListener {
            if (darkModeSwitch.isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        generateQRButton.setOnClickListener {
            qrIV.setImageBitmap(getQrCodeBitmap())
        }

        val barcodeLauncher = registerForActivityResult(
            ScanContract()
        ) { result: ScanIntentResult ->
            if (result.contents == null) {
                Toast.makeText(this@SettingsActivity, "Cancelled", Toast.LENGTH_LONG).show()
            } else {

                val userId = result.contents.replace("rate-me://", "")
                println("Scanned user ID : $userId")

                userViewModel.getById(userId, this).observe(this) {
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra("user", it)
                    startActivity(intent)
                }
            }
        }

        scanQRButton.setOnClickListener {
            val options = ScanOptions()
            options.setOrientationLocked(false)
            barcodeLauncher.launch(options)
        }
    }

    private fun getQrCodeBitmap(): Bitmap {
        val qrCodeContent = "rate-me://" + sessionUser._id

        val size = 512 //pixels
        val bits = QRCodeWriter().encode(qrCodeContent, BarcodeFormat.QR_CODE, size, size)
        return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).also {
            for (x in 0 until size) {
                for (y in 0 until size) {
                    it.setPixel(x, y, if (bits[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        }
    }
}