package com.playlistmaker.presentation.ui.player.viewmodel

import android.app.Application
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.playlistmaker.domain.models.Music
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(
    application: Application,
    private val track: Music?
) : AndroidViewModel(application) {

    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())

    private val playerStateLiveData = MutableLiveData(STATE_DEFAULT)
    fun observePlayerState(): LiveData<Int> = playerStateLiveData

    private val progressLiveData = MutableLiveData("00:00")
    fun observeProgress(): LiveData<String> = progressLiveData

    private val progressTask = object : Runnable {
        override fun run() {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    val currentPosition = player.currentPosition
                    progressLiveData.postValue(
                        SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPosition)
                    )
                    handler.postDelayed(this, 500)
                }
            }
        }
    }

    init {
        preparePlayer()
    }

    private fun preparePlayer() {
        val previewUrl = track?.previewUrl ?: return
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(previewUrl)
                prepareAsync()
                setOnPreparedListener {
                    playerStateLiveData.postValue(STATE_PREPARED)
                    handler.post(progressTask)
                }
                setOnCompletionListener {
                    playerStateLiveData.postValue(STATE_PREPARED)
                    handler.removeCallbacks(progressTask)
                    progressLiveData.postValue("00:00")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun playbackControl() {
        when (playerStateLiveData.value) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    private fun startPlayer() {
        mediaPlayer?.start()
        playerStateLiveData.postValue(STATE_PLAYING)
        handler.post(progressTask)
    }

    private fun pausePlayer() {
        mediaPlayer?.pause()
        playerStateLiveData.postValue(STATE_PAUSED)
        handler.removeCallbacks(progressTask)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(progressTask)
        mediaPlayer?.release()
        mediaPlayer = null
    }

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3

        fun getViewModelFactory(application: Application, track: Music?) =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AudioPlayerViewModel(application, track) as T
                }
            }
    }
}