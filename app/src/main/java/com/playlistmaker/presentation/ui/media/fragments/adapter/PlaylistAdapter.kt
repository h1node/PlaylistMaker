package com.playlistmaker.presentation.ui.media.fragments.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.playlistmaker.databinding.ItemPlaylistBinding
import com.playlistmaker.domain.models.Playlist
import com.playlistmaker.presentation.ui.media.fragments.viewholder.PlaylistViewHolder


class PlaylistAdapter(
    private val onClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistViewHolder>() {

    private var playlists: List<Playlist> = emptyList()

    fun submitList(newList: List<Playlist>) {
        playlists = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = ItemPlaylistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position], onClick)
    }

    override fun getItemCount(): Int = playlists.size
}