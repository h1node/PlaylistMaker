package com.playlistmaker.presentation.ui.media.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.playlistmaker.R
import com.playlistmaker.databinding.FragmentPlayListBinding
import com.playlistmaker.presentation.ui.media.MediaLibraryDirections
import com.playlistmaker.presentation.ui.media.fragments.adapter.PlaylistAdapter
import com.playlistmaker.presentation.ui.media.viewmodel.PlaylistListViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlayListFragment : Fragment() {
    private var _binding: FragmentPlayListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistListViewModel by viewModel()
    private lateinit var adapter: PlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        observePlaylists()

        binding.btnAddPlaylist.setOnClickListener {
            findNavController().navigate(
                R.id.action_mediaLibrary_to_createPlayListFragment
            )
        }
    }

    private fun initRecyclerView() {
        adapter = PlaylistAdapter { playlist ->
            val action =
                MediaLibraryDirections.actionMediaLibraryToPlaylistDetailsFragment(playlist.id)
            findNavController().navigate(action)
        }
        binding.rvPlaylists.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = this@PlayListFragment.adapter
        }
    }

    private fun observePlaylists() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.playlists.collect { list ->
                val hasData = list.isNotEmpty()
                binding.rvPlaylists.isVisible = hasData
                binding.placeholderEmpty.isVisible = !hasData

                if (hasData) {
                    adapter.submitList(list)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}