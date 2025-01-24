package com.playlistmaker


import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.playlistmaker.data.itunesdb.Music
import com.playlistmaker.databinding.ActivityAuduoPlayerBinding
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuduoPlayerBinding
    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())
    private val progressTask = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING) {
                val currentPosition = mediaPlayer.currentPosition
                binding.trackProgress.text =
                    SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPosition)
                handler.postDelayed(this, 500)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAuduoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupToolbar()
        retrieveTrackDetails()

        binding.trackProgress.text = "00:00"
        preparePlayer()

        binding.play.setOnClickListener {
            playbackControl()
        }
        binding.pause.setOnClickListener {
            playbackControl()
        }
    }

    private fun preparePlayer() {
        val track = intent.getParcelableExtra<Music>("track")
        val previewUrl = track?.previewUrl
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            binding.play.isEnabled = true
            playerState = STATE_PREPARED
            handler.post(progressTask)
        }

        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            handler.removeCallbacks(progressTask)
            binding.play.visibility = View.VISIBLE
            binding.pause.visibility = View.GONE
            binding.trackProgress.text = "00:00"
        }
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED ->
                startPlayer()
        }
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        handler.removeCallbacks(progressTask)
        binding.play.visibility = View.VISIBLE
        binding.pause.visibility = View.GONE
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        handler.post(progressTask)
        binding.play.visibility = View.GONE
        binding.pause.visibility = View.VISIBLE
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(progressTask)
        mediaPlayer.release()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else super.onOptionsItemSelected(item)
    }

    private fun retrieveTrackDetails() {
        val jsonTrack = intent.getParcelableExtra<Music>("track")
        jsonTrack?.let {
            with(binding) {
                songTitle.text = it.trackName
                artistName.text = it.artistName
                releaseDate.text = it.releaseDate?.let { date ->
                    SimpleDateFormat("yyyy", Locale.getDefault()).format(date)
                }
                country.text = it.country
                collectionName.visibility = if (it.collectionName.isNullOrEmpty()) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
                collectionName.text = it.collectionName
                primaryGenreName.text = it.primaryGenreName
                trackTimeMills.text = it.trackTimeMillis?.let {
                    val minutes = it / 60000
                    val seconds = (it % 60000) / 1000
                    String.format("%02d:%02d", minutes, seconds)
                } ?: "00:00"

                Glide.with(image)
                    .load(it.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg"))
                    .placeholder(R.drawable.placeholder)
                    .into(image)
            }
        }
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
}