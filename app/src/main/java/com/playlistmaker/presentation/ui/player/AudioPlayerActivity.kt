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
import com.playlistmaker.databinding.ActivityAuduoPlayerBinding
import com.playlistmaker.domain.models.Music
import com.playlistmaker.presentation.ui.player.viewmodel.AudioPlayerViewModel
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuduoPlayerBinding
    private val viewModel: AudioPlayerViewModel by viewModels {
        AudioPlayerViewModel.getViewModelFactory(application, intent.getParcelableExtra("track"))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAuduoPlayerBinding.inflate(layoutInflater)
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
        val track = intent.getParcelableExtra<Music>("track") ?: return
        with(binding) {
            songTitle.text = track.trackName
            artistName.text = track.artistName
            releaseDate.text =
                track.releaseDate?.let { SimpleDateFormat("yyyy", Locale.getDefault()).format(it) }
                    ?: ""
            country.text = track.country
            collectionName.text = track.collectionName
            collectionName.visibility =
                if (track.collectionName.isNullOrEmpty()) View.GONE else View.VISIBLE
            primaryGenreName.text = track.primaryGenreName
            trackTimeMills.text = track.trackTimeMillis?.let {
                String.format("%02d:%02d", it / 60000, (it % 60000) / 1000)
            } ?: "00:00"

            Glide.with(image)
                .load(track.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg"))
                .placeholder(R.drawable.placeholder)
                .into(image)
        }
    }

    private fun observeViewModel() {
        viewModel.observePlayerState().observe(this) { state ->
            binding.play.isEnabled = state != AudioPlayerViewModel.STATE_DEFAULT
        }

        viewModel.observeProgress().observe(this) { progress ->
            binding.trackProgress.text = progress
        }

        viewModel.observePlayButtonVisibility().observe(this) { visibility ->
            binding.play.visibility = visibility
        }

        viewModel.observePauseButtonVisibility().observe(this) { visibility ->
            binding.pause.visibility = visibility
        }
    }

    private fun setupControls() {
        binding.play.setOnClickListener { viewModel.playbackControl() }
        binding.pause.setOnClickListener { viewModel.playbackControl() }
    }

    override fun onPause() {
        super.onPause()
        viewModel.playbackControl()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}