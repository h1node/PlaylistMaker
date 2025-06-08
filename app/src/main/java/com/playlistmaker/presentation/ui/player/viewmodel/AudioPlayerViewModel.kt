package com.playlistmaker.presentation.ui.player.viewmodel

import android.app.Application
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.playlistmaker.domain.models.Music
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class AudioPlayerViewModel(
    application: Application,
    private val track: Music?
) : AndroidViewModel(application) {

    private var mediaPlayer: MediaPlayer? = null
    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private var progressJob: Job? = null

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
                    playerStateLiveData.postValue(PlayerState.Prepared())
                }
                setOnCompletionListener {
                    stopProgressUpdates()
                    mediaPlayer?.seekTo(0)
                    playerStateLiveData.postValue(PlayerState.Prepared())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun playbackControl() {
        when (playerStateLiveData.value) {
            is PlayerState.Playing -> pausePlayer()
            is PlayerState.Prepared, is PlayerState.Paused -> startPlayer()
            else -> Unit
        }
    }

    private fun startPlayer() {
        mediaPlayer?.start()
        startProgressUpdates()
        playerStateLiveData.postValue(
            PlayerState.Playing(
                formatProgress(
                    mediaPlayer?.currentPosition ?: 0
                )
            )
        )
    }

    private fun pausePlayer() {
        mediaPlayer?.pause()
        stopProgressUpdates()
        playerStateLiveData.postValue(
            PlayerState.Paused(
                formatProgress(
                    mediaPlayer?.currentPosition ?: 0
                )
            )
        )
    }

    private fun startProgressUpdates() {
        stopProgressUpdates()
        progressJob = viewModelScope.launch {
            while (isActive) {
                delay(300)
                val position = mediaPlayer?.currentPosition ?: 0
                val formattedProgress = formatProgress(position)
                playerStateLiveData.postValue(
                    PlayerState.Playing(formattedProgress)
                )
            }
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun formatProgress(millis: Int): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / 1000) / 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onCleared() {
        super.onCleared()
        stopProgressUpdates()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}