package com.playlistmaker.presentation.ui.media

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.playlistmaker.presentation.ui.media.fragments.FavoritesTrackFragment
import com.playlistmaker.presentation.ui.media.fragments.PlayListFragment


class MediaViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FavoritesTrackFragment.newInstance()
            else -> PlayListFragment.newInstance((position + 1).toString())

        }
    }
}