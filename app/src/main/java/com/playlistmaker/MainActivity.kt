package com.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.playlistmaker.view.rv_adapter.MusicRVAdapter


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val settingsButton = findViewById<Button>(R.id.setting_button)
        val searchButton = findViewById<Button>(R.id.search_button)
        val mediaLibraryButton = findViewById<Button>(R.id.media_library_button)

        settingsButton.setOnClickListener {
            val settingDisplay = Intent(this, SettingsActivity::class.java)
            startActivity(settingDisplay)
        }

        mediaLibraryButton.setOnClickListener {
            val mediaLibraryDisplay = Intent(this, MediaLibrary::class.java)
            startActivity(mediaLibraryDisplay)
        }

        searchButton.setOnClickListener {
            val searchDisplay = Intent(this, SearchActivity::class.java)
            startActivity(searchDisplay)
        }
    }
}