package com.playlistmaker


import android.icu.text.SimpleDateFormat
import android.os.Bundle
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
                    .transform()
                    .into(image)
            }
        }
    }
}