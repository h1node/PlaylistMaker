package com.playlistmaker.presentation.ui.player

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.playlistmaker.R
import com.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.playlistmaker.domain.models.Music
import com.playlistmaker.presentation.ui.player.viewmodel.AudioPlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.Locale


class AudioPlayerFragment : Fragment() {
    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AudioPlayerViewModel by viewModel {
        parametersOf(requireArguments().getParcelable<Music>("track"))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        displayTrackDetails()
        observeViewModel()
        setupControls()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupToolbar() {
        val activity = requireActivity()
        if (activity is AppCompatActivity) {
            activity.setSupportActionBar(binding.toolbar)
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

            binding.toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    companion object {
        const val TRACK_KEY = "track"
        const val YEAR_FORMAT = "yyyy"
        const val DEFAULT_TRACK_TIME = "00:00"
    }

    private fun displayTrackDetails() {
        val track = requireArguments().getParcelable<Music>(TRACK_KEY) ?: return

        with(binding) {
            songTitle.text = track.trackName
            artistName.text = track.artistName
            releaseDate.text = track.releaseDate?.let {
                SimpleDateFormat(YEAR_FORMAT, Locale.getDefault()).format(it)
            } ?: ""

            country.text = track.country

            collectionName.apply {
                text = track.collectionName
                visibility = getVisibility(track.collectionName)
            }

            primaryGenreName.text = track.primaryGenreName
            trackTimeMills.text =
                track.trackTimeMillis?.let { formatTrackTime(it) } ?: DEFAULT_TRACK_TIME

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
        viewModel.observePlayerState().observe(viewLifecycleOwner) { state ->
            binding.play.isEnabled = state != AudioPlayerViewModel.STATE_DEFAULT
            updateButtonVisibility(state)
        }

        viewModel.observeProgress().observe(viewLifecycleOwner) { progress ->
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
}