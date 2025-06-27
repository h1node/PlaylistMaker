package com.playlistmaker.presentation.ui.media.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.playlistmaker.R
import com.playlistmaker.data.db.entity.MusicEntity
import com.playlistmaker.data.db.toMusic
import com.playlistmaker.databinding.FragmentFavoritesTrackBinding
import com.playlistmaker.presentation.ui.media.FavoritesState
import com.playlistmaker.presentation.ui.media.fragments.adapter.FavoritesAdapter
import com.playlistmaker.presentation.ui.media.viewmodel.FavoritesViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class FavoritesTrackFragment : Fragment() {

    private var _binding: FragmentFavoritesTrackBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoritesViewModel by viewModel()
    private lateinit var adapter: FavoritesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesTrackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setupRecyclerView() {
        adapter = FavoritesAdapter { trackEntity ->
            val music = trackEntity.toMusic()
            val bundle = Bundle().apply {
                putParcelable(KEY_TRACK, music)
            }
            findNavController().navigate(
                R.id.audioPlayerFragment,
                bundle
            )
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FavoritesTrackFragment.adapter
        }
    }


    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favoritesState.collect { state ->
                    when (state) {
                        is FavoritesState.Empty -> showEmptyState()
                        is FavoritesState.Loaded -> showTracks(state.tracks)
                    }
                }
            }
        }
    }

    private fun showEmptyState() {
        binding.recyclerView.visibility = View.GONE
        binding.emptyStateView.visibility = View.VISIBLE
        binding.swipeRefreshLayout.isEnabled = false
    }

    private fun showTracks(tracks: List<MusicEntity>) {
        binding.emptyStateView.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
        binding.swipeRefreshLayout.isEnabled = true
        adapter.submitList(tracks)
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val KEY_TRACK = "track"
        fun newInstance() = FavoritesTrackFragment()
    }
}
