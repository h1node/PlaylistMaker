package com.playlistmaker.presentation.ui.player


import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.playlistmaker.R
import com.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.playlistmaker.domain.models.Music
import com.playlistmaker.presentation.ui.player.viewmodel.AudioPlayerViewModel
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAudioPlayerBinding
    private val viewModel: AudioPlayerViewModel by viewModels {
        AudioPlayerViewModel.getViewModelFactory(application, intent?.getParcelableExtra("track"))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        setupToolbar()
        displayTrackDetails()
        observeViewModel()
        setupControls()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun displayTrackDetails() {
        val track = intent?.getParcelableExtra<Music>("track") ?: return
        with(binding) {
            songTitle.text = track.trackName
            artistName.text = track.artistName
            releaseDate.text = track.releaseDate?.let {
                SimpleDateFormat("yyyy", Locale.getDefault()).format(it)
            } ?: ""
            country.text = track.country
            collectionName.text = track.collectionName
            collectionName.visibility = getVisibility(track.collectionName)
            primaryGenreName.text = track.primaryGenreName
            trackTimeMills.text = track.trackTimeMillis?.let { formatTrackTime(it) } ?: "00:00"

            Glide.with(image)
                .load(track.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg"))
                .placeholder(R.drawable.placeholder)
                .into(image)
        }
    }

    private fun getVisibility(text: String?): Int {
        return if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    private fun formatTrackTime(millis: Int): String {
        return String.format("%02d:%02d", millis / 60000, (millis % 60000) / 1000)
    }

    private fun observeViewModel() {
        viewModel.observePlayerState().observe(this) { state ->
            binding.play.isEnabled = state != AudioPlayerViewModel.STATE_DEFAULT
            updateButtonVisibility(state)
        }

        viewModel.observeProgress().observe(this) { progress ->
            binding.trackProgress.text = progress
        }
    }

    private fun updateButtonVisibility(state: Int) {
        when (state) {
            AudioPlayerViewModel.STATE_PLAYING -> {
                binding.play.visibility = View.GONE
                binding.pause.visibility = View.VISIBLE
            }

            AudioPlayerViewModel.STATE_PAUSED, AudioPlayerViewModel.STATE_PREPARED -> {
                binding.play.visibility = View.VISIBLE
                binding.pause.visibility = View.GONE
            }

            else -> {
                binding.play.visibility = View.VISIBLE
                binding.pause.visibility = View.GONE
            }
        }
    }

    private fun setupControls() {
        binding.play.setOnClickListener { viewModel.playbackControl() }
        binding.pause.setOnClickListener { viewModel.playbackControl() }
    }

    override fun onPause() {
        super.onPause()
        if (viewModel.observePlayerState().value == AudioPlayerViewModel.STATE_PLAYING) {
            viewModel.playbackControl()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}