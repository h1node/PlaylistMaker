package com.playlistmaker.presentation.ui.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.playlistmaker.R
import com.playlistmaker.databinding.FragmentMediaLibraryBinding


class MediaLibrary : Fragment() {
    private var _binding: FragmentMediaLibraryBinding? = null
    private val binding get() = _binding!!

    private lateinit var tabLayoutMediator: TabLayoutMediator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMediaLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViewPager()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabLayoutMediator.detach()
        _binding = null
    }

    private fun setUpViewPager() {
        binding.viewPager.adapter = MediaViewPagerAdapter(childFragmentManager, lifecycle)
        tabLayoutMediator =
            TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.favorites_tracks)
                    1 -> tab.text = getString(R.string.playlist)
                }
            }
        tabLayoutMediator.attach()
    }
}