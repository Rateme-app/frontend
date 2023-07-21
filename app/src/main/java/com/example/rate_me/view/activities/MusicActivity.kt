package com.example.rate_me.view.activities

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.rate_me.R
import com.example.rate_me.api.models.Music
import com.example.rate_me.utils.Constants
import com.example.rate_me.utils.ImageLoader
import java.io.IOException

class MusicActivity : AppCompatActivity() {

    private lateinit var albumArtImageView: ImageView
    private lateinit var songTitleTextView: TextView
    private lateinit var artistTextView: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var previousButton: ImageButton
    private lateinit var playPauseButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var mediaPlayer: MediaPlayer
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music)

        val music = intent.getSerializableExtra("music") as Music

        // Initialize views
        albumArtImageView = findViewById(R.id.albumArtImageView)
        songTitleTextView = findViewById(R.id.songTitleTextView)
        artistTextView = findViewById(R.id.artistTextView)
        seekBar = findViewById(R.id.seekBar)
        previousButton = findViewById(R.id.previousButton)
        playPauseButton = findViewById(R.id.playPauseButton)
        nextButton = findViewById(R.id.nextButton)

        ImageLoader.setImageFromUrl(
            albumArtImageView,
            Constants.BASE_URL_MUSIC + music.imageFilename
        )

        songTitleTextView.text = music.title
        artistTextView.text = music.artist

        // Create the MediaPlayer with audio attributes
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        mediaPlayer = MediaPlayer()
        mediaPlayer.setAudioAttributes(audioAttributes)

        try {
            mediaPlayer.setDataSource("${Constants.BASE_URL_MUSIC}${music.filename}")
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                // Media player is prepared, you can start playing the music here if needed
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        playPauseButton.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                pauseMusic()
            } else {
                playMusic()
            }
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val totalDuration = mediaPlayer.duration
                    val newPosition = progress * totalDuration / 100
                    mediaPlayer.seekTo(newPosition)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        updateSeekBar()
    }

    private fun updateSeekBar() {
        val currentPosition = mediaPlayer.currentPosition
        val totalDuration = mediaPlayer.duration

        if (totalDuration > 0) {
            val progress = currentPosition * 100 / totalDuration
            seekBar.progress = progress
        }

        handler.postDelayed({
            updateSeekBar()
        }, 1000) // Update the SeekBar every second (adjust the delay as needed)
    }

    private fun playMusic() {
        mediaPlayer.start()
        playPauseButton.setImageResource(R.drawable.ic_pause) // Replace with your pause button drawable
    }

    private fun pauseMusic() {
        mediaPlayer.pause()
        playPauseButton.setImageResource(R.drawable.ic_play) // Replace with your play button drawable
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacksAndMessages(null)
    }
}