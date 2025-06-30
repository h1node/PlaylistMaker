package com.playlistmaker.presentation.ui.media.fragments.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.playlistmaker.R
import com.playlistmaker.data.db.entity.MusicEntity
import com.playlistmaker.databinding.MusicItemBinding


class FavoritesViewHolder(
    private val binding: MusicItemBinding,
    private val onClick: (MusicEntity) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: MusicEntity) {
        with(binding) {
            musicTitle.text = item.trackName
            artistName.text = item.artistName
            trackProgress.text = item.trackTimeMillis?.let {
                String.format("%02d:%02d", it / 60000, (it % 60000) / 1000)
            } ?: "00:00"

            Glide.with(poster.context)
                .load(item.artworkUrl100)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .into(poster)

            root.setOnClickListener { onClick(item) }
        }
    }
}