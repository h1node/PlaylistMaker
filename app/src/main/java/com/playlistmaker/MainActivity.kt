package com.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.playlistmaker.databinding.ActivityMainBinding
import com.playlistmaker.view.rv_adapter.MusicRVAdapter


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.settingButton.setOnClickListener {
            val settingDisplay = Intent(this, SettingsActivity::class.java)
            startActivity(settingDisplay)
        }

        binding.mediaLibraryButton.setOnClickListener {
            val mediaLibraryDisplay = Intent(this, MediaLibrary::class.java)
            startActivity(mediaLibraryDisplay)
        }

        binding.searchButton.setOnClickListener {
            val searchDisplay = Intent(this, SearchActivity::class.java)
            startActivity(searchDisplay)
        }
    }
}