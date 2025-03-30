package com.playlistmaker.presentation.ui.media.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.playlistmaker.databinding.FragmentFavoritesTrackBinding


class FavoritesTrackFragment : Fragment() {
    private lateinit var binding: FragmentFavoritesTrackBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesTrackBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        private const val TRACK = "trackName"

        fun newInstance(trackName: String) = FavoritesTrackFragment().apply {
            arguments = Bundle().apply {
                putString(TRACK, trackName)
            }
        }
    }
}