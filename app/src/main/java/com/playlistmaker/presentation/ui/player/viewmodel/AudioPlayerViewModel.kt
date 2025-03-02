package com.playlistmaker.presentation.ui.player.viewmodel

import android.app.Application
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.playlistmaker.domain.models.Music
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(
    application: Application,
    private val track: Music?
) : AndroidViewModel(application) {

    private val mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())

    private val playerStateLiveData = MutableLiveData<Int>(STATE_DEFAULT)
    fun observePlayerState(): LiveData<Int> = playerStateLiveData

    private val progressLiveData = MutableLiveData<String>("00:00")
    fun observeProgress(): LiveData<String> = progressLiveData

    private val playButtonVisibilityLiveData = MutableLiveData(View.VISIBLE)
    fun observePlayButtonVisibility(): LiveData<Int> = playButtonVisibilityLiveData

    private val pauseButtonVisibilityLiveData = MutableLiveData(View.GONE)
    fun observePauseButtonVisibility(): LiveData<Int> = pauseButtonVisibilityLiveData

    private val progressTask = object : Runnable {
        override fun run() {
            if (playerStateLiveData.value == STATE_PLAYING) {
                val currentPosition = mediaPlayer.currentPosition
                progressLiveData.postValue(
                    SimpleDateFormat("mm:ss", Locale.getDefault()).format(
                        currentPosition
                    )
                )
                handler.postDelayed(this, 500)
            }
        }
    }

    init {
        preparePlayer()
    }

    private fun preparePlayer() {
        val previewUrl = track?.previewUrl ?: return
        try {
            mediaPlayer.setDataSource(previewUrl)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                playerStateLiveData.postValue(STATE_PREPARED)
                handler.post(progressTask)
            }
            mediaPlayer.setOnCompletionListener {
                playerStateLiveData.postValue(STATE_PREPARED)
                handler.removeCallbacks(progressTask)
                progressLiveData.postValue("00:00")
                playButtonVisibilityLiveData.postValue(View.VISIBLE)
                pauseButtonVisibilityLiveData.postValue(View.GONE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playbackControl() {
        when (playerStateLiveData.value) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerStateLiveData.postValue(STATE_PLAYING)
        handler.post(progressTask)
        playButtonVisibilityLiveData.postValue(View.GONE)
        pauseButtonVisibilityLiveData.postValue(View.VISIBLE)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerStateLiveData.postValue(STATE_PAUSED)
        handler.removeCallbacks(progressTask)
        playButtonVisibilityLiveData.postValue(View.VISIBLE)
        pauseButtonVisibilityLiveData.postValue(View.GONE)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(progressTask)
        mediaPlayer.release()
    }

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3

        fun getViewModelFactory(
            application: Application,
            track: Music?
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AudioPlayerViewModel(application, track)
            }
        }
    }
}