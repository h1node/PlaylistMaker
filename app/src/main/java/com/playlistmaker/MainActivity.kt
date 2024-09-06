package com.playlistmaker

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.playlistmaker.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }


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