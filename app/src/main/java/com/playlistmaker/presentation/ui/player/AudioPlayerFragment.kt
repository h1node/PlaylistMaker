package com.playlistmaker.presentation.ui.player

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import com.playlistmaker.R
import com.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.playlistmaker.domain.models.Music
import com.playlistmaker.domain.models.Playlist
import com.playlistmaker.presentation.ui.media.fragments.adapter.PlaylistSheetAdapter
import com.playlistmaker.presentation.ui.player.viewmodel.AudioPlayerViewModel
import com.playlistmaker.presentation.ui.player.viewmodel.PlayerState
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.Locale


class AudioPlayerFragment : Fragment() {
    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!

    private lateinit var track: Music
    private var currentTargetPlaylist: Playlist? = null

    private val viewModel: AudioPlayerViewModel by viewModel {
        parametersOf(track)
    }

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var sheetAdapter: PlaylistSheetAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        track = requireArguments().getParcelable(TRACK_KEY)
            ?: throw IllegalStateException("Track must be passed in arguments")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        displayTrackDetails()
        observeViewModel()
        setupControls()
        initBottomSheet()
    }

    private fun setupToolbar() {
        (requireActivity() as? AppCompatActivity)?.let { activity ->
            activity.setSupportActionBar(binding.toolbar)
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            binding.toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun displayTrackDetails() {
        with(binding) {
            songTitle.text = track.trackName
            artistName.text = track.artistName
            releaseDate.text = track.releaseDate
                ?.let { SimpleDateFormat(YEAR_FORMAT, Locale.getDefault()).format(it) }
                ?: ""
            country.text = track.country

            collectionName.apply {
                text = track.collectionName
                visibility = if (track.collectionName.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
            primaryGenreName.text = track.primaryGenreName
            trackTimeMills.text = track.trackTimeMillis
                ?.let { formatTrackTime(it) }
                ?: DEFAULT_TRACK_TIME

            Glide.with(image)
                .load(track.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg"))
                .placeholder(R.drawable.placeholder)
                .into(image)
        }
    }

    private fun observeViewModel() {
        viewModel.observePlayerState().observe(viewLifecycleOwner) { state ->
            updatePlayPauseButton(state)
            binding.trackProgress.text = state.progress
        }
        viewModel.isFavoriteLiveData.observe(viewLifecycleOwner) { isFav ->
            binding.favoriteButton.setImageResource(
                if (isFav) R.drawable.fab_like else R.drawable.fab_favorite
            )
        }
    }

    private fun setupControls() {
        binding.playPauseButton.setOnClickListener { viewModel.playbackControl() }
        binding.favoriteButton.setOnClickListener { viewModel.onFavoriteClicked() }
    }

    private fun initBottomSheet() {
        val overlay = binding.root.findViewById<View>(R.id.overlay)
        val sheetLayout = binding.root.findViewById<LinearLayout>(R.id.playlists_bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(sheetLayout).apply {
            state = STATE_HIDDEN
            isHideable = true
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                overlay.visibility = if (newState == STATE_HIDDEN) GONE else VISIBLE
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                overlay.alpha = slideOffset.coerceIn(0f, 1f)
            }
        })

        val rvSheet = binding.root.findViewById<RecyclerView>(R.id.rvPlaylistsSheet)
        rvSheet.layoutManager = LinearLayoutManager(requireContext())
        sheetAdapter = PlaylistSheetAdapter { playlist ->
            currentTargetPlaylist = playlist
            viewModel.addTrackToPlaylist(playlist.id)
        }
        rvSheet.adapter = sheetAdapter

        viewModel.addResult.observe(viewLifecycleOwner) { added ->
            val playlistName = currentTargetPlaylist?.name ?: return@observe
            if (added) {
                Toast.makeText(
                    requireContext(),
                    "Добавлено в плейлист \"$playlistName\"",
                    Toast.LENGTH_SHORT
                ).show()
                bottomSheetBehavior.state = STATE_HIDDEN
            } else {
                Toast.makeText(
                    requireContext(),
                    "Трек уже добавлен в плейлист \"$playlistName\"",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.plusButton.setOnClickListener {
            viewModel.loadPlaylists()
            lifecycleScope.launch {
                viewModel.playlists.collect { list ->
                    sheetAdapter.submitList(list)
                }
            }
            bottomSheetBehavior.state = STATE_EXPANDED
        }

        binding.root.findViewById<Button>(R.id.btnNewPlaylistFromSheet)
            .setOnClickListener {
                bottomSheetBehavior.state = STATE_HIDDEN
                findNavController().navigate(R.id.action_audioPlayer_to_createPlayListFragment)
            }
    }

    private fun updatePlayPauseButton(state: PlayerState) {
        val icon = if (state is PlayerState.Playing) R.drawable.pause else R.drawable.play
        binding.playPauseButton.setImageResource(icon)
        binding.playPauseButton.isEnabled = state.isPlayButtonEnabled
    }

    private fun formatTrackTime(millis: Int): String =
        String.format("%02d:%02d", millis / 60_000, (millis % 60_000) / 1000)

    override fun onPause() {
        super.onPause()
        viewModel.observePlayerState().value
            ?.takeIf { it is PlayerState.Playing }
            ?.let { viewModel.playbackControl() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TRACK_KEY = "track"
        private const val YEAR_FORMAT = "yyyy"
        private const val DEFAULT_TRACK_TIME = "00:00"
    }
}