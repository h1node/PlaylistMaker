package com.playlistmaker.presentation.ui.media.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.playlistmaker.R
import com.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.playlistmaker.databinding.PlaylistMenuBottomSheetBinding
import com.playlistmaker.domain.models.Music
import com.playlistmaker.domain.models.Playlist
import com.playlistmaker.presentation.ui.media.fragments.adapter.PlaylistDetailsAdapter
import com.playlistmaker.presentation.ui.media.viewmodel.PlaylistDetailsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File


class PlaylistDetailsFragment : Fragment() {
    private var _binding: FragmentPlaylistDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var tracksAdapter: PlaylistDetailsAdapter
    private lateinit var menuSheetBehavior: BottomSheetBehavior<LinearLayout>

    private val playlistId: Long by lazy {
        PlaylistDetailsFragmentArgs.fromBundle(requireArguments()).playlistId
    }
    private val viewModel: PlaylistDetailsViewModel by viewModel {
        parametersOf(playlistId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupToolbar()
        setupBottomSheet()
        setupTracksAdapter()
        setupMenuBottomSheet()
        setupShareButton()

        lifecycleScope.launchWhenStarted {
            viewModel.playlist.collect { playlist ->
                playlist?.let {
                    showPlaylistDetails(it)
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.tracks.collect { tracks ->
                tracksAdapter.submitList(tracks)
                updateTrackStats(tracks)
            }
        }
    }

    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(binding.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = ""
        }
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupBottomSheet() {
        val behavior = BottomSheetBehavior.from(binding.bottomSheet)
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        behavior.peekHeight = resources.getDimensionPixelSize(R.dimen.bottom_sheet_height)
        behavior.isHideable = false
    }

    private fun setupTracksAdapter() {
        binding.rvPlaylistTracks.layoutManager = LinearLayoutManager(requireContext())
        tracksAdapter = PlaylistDetailsAdapter(
            onClick = { track ->
                val action = PlaylistDetailsFragmentDirections
                    .actionPlaylistDetailsFragmentToAudioPlayerFragment(track)
                findNavController().navigate(action)
            },
            onLongClick = { track -> showDeleteTrackDialog(track) }
        )
        binding.rvPlaylistTracks.adapter = tracksAdapter
    }

    private fun setupMenuBottomSheet() {
        val menuBinding = PlaylistMenuBottomSheetBinding.bind(binding.menuBottomSheet)

        menuSheetBehavior = BottomSheetBehavior.from(binding.menuBottomSheet)
        menuSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        menuSheetBehavior.isHideable = true

        val overlay = View(requireContext()).apply {
            setBackgroundColor(0x99000000.toInt())
            visibility = View.GONE
        }
        (binding.root as ViewGroup).addView(
            overlay,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        overlay.bringToFront()
        binding.menuBottomSheet.bringToFront()

        menuSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                overlay.visibility =
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) View.GONE else View.VISIBLE
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                overlay.alpha = slideOffset
            }
        })

        overlay.setOnClickListener {
            menuSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        menuBinding.btnEdit.setOnClickListener {
            menuSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            val action = PlaylistDetailsFragmentDirections
                .actionPlaylistDetailsFragmentToEditPlaylistFragment(viewModel.playlist.value!!)
            findNavController().navigate(action)
        }

        binding.menuButton.setOnClickListener {
            val playlist = viewModel.playlist.value
            val trackCount = viewModel.tracks.value.size

            playlist?.let {
                menuBinding.tvPlaylistName.text = it.name
                menuBinding.tvTracksCount.text = "$trackCount треков"
            }

            menuSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        menuBinding.btnShare.setOnClickListener {
            menuSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            doShare()
        }

        menuBinding.btnDelete.setOnClickListener {
            menuSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            showDeletePlaylistDialog()
        }
    }

    private fun doShare() {
        val playlist = viewModel.playlist.value ?: run {
            Toast.makeText(
                requireContext(),
                getString(R.string.not_available),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val tracks = viewModel.tracks.value
        if (tracks.isEmpty()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.no_tracks_to_share),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val text = buildShareText(playlist, tracks)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(intent, getString(R.string.share_playlist)))
    }

    private fun buildShareText(playlist: Playlist, tracks: List<Music>): String {
        val builder = StringBuilder()
        val resources = requireContext().resources

        builder.append(playlist.name).append("\n")
        playlist.description?.takeIf { it.isNotBlank() }?.let {
            builder.append(it).append("\n")
        }
        val trackCount = tracks.size
        val trackCountText =
            resources.getQuantityString(R.plurals.tracks_count, trackCount, trackCount)
        builder.append(trackCountText).append("\n")

        tracks.forEachIndexed { index, track ->
            builder.append(
                "${index + 1}. ${track.artistName} - ${track.trackName} (${
                    formatTrackTime(track.trackTimeMillis)
                })\n"
            )
        }
        return builder.toString()
    }

    private fun formatTrackTime(millis: Int?): String {
        if (millis == null) return "00:00"
        val min = millis / 60000
        val sec = (millis % 60000) / 1000
        return String.format("%02d:%02d", min, sec)
    }

    private fun setupShareButton() {
        binding.shareButton?.setOnClickListener {
            doShare()
        }
    }

    private fun updateTrackStats(tracks: List<Music>) {
        val minutes = tracks.sumOf { it.trackTimeMillis ?: 0 } / 60000
        val trackCount = tracks.size
        val trackCountText =
            resources.getQuantityString(R.plurals.tracks_count, trackCount, trackCount)

        binding.playlistStats.text = "$minutes минут • $trackCountText"
    }

    private fun showDeletePlaylistDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_playlist)
            .setMessage(getString(R.string.confirm_delete_playlist))
            .setNegativeButton(R.string.no, null)
            .setPositiveButton(R.string.yes) { _, _ ->
                lifecycleScope.launch {
                    viewModel.deletePlaylist()
                    findNavController().popBackStack()
                }
            }
            .show()
    }

    private fun showDeleteTrackDialog(track: Music) {
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.delete_track))
            .setNegativeButton(R.string.no, null)
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.deleteTrack(track)
            }
            .show()
    }

    private fun showPlaylistDetails(playlist: Playlist) {
        binding.playlistName.text = playlist.name
        binding.playlistDescription.text = playlist.description ?: ""
        binding.playlistDescription.visibility =
            if (playlist.description.isNullOrBlank()) View.GONE else View.VISIBLE

        if (playlist.coverUri != null) {
            val file = File(requireContext().filesDir, playlist.coverUri)
            binding.coverPlaceholder.setImageURI(Uri.fromFile(file))
            binding.coverStub.visibility = View.GONE
        } else {
            binding.coverPlaceholder.setImageResource(R.drawable.placeholder)
            binding.coverStub.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshPlaylist()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}