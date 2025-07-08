package com.playlistmaker.presentation.ui.media.fragments.viewholder

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.playlistmaker.R
import com.playlistmaker.databinding.ItemPlaylistBinding
import com.playlistmaker.domain.models.Playlist
import java.io.File


class PlaylistViewHolder(

    private val binding: ItemPlaylistBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Playlist, onClick: (Playlist) -> Unit) {
        binding.tvName.text = item.name

        val countText = binding.root.context.resources.getQuantityString(
            R.plurals.tracks_count,
            item.trackCount,
            item.trackCount
        )
        binding.tvCount.text = countText

        if (item.coverUri != null) {
            val file = File(binding.root.context.filesDir, item.coverUri)
            binding.imgCover.setImageURI(Uri.fromFile(file))
        } else {
            binding.imgCover.setImageResource(R.drawable.placeholder)
        }

        binding.root.setOnClickListener { onClick(item) }
    }
}