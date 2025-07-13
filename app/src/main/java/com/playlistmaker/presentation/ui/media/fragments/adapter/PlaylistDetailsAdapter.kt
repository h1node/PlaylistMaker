package com.playlistmaker.presentation.ui.media.fragments.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.playlistmaker.databinding.MusicItemBinding
import com.playlistmaker.domain.models.Music
import com.playlistmaker.presentation.ui.media.fragments.viewholder.TrackViewHolder


class PlaylistDetailsAdapter(
    private val onClick: (Music) -> Unit,
    private val onLongClick: (Music) -> Unit
) : RecyclerView.Adapter<TrackViewHolder>() {

    private var tracks: List<Music> = emptyList()

    fun submitList(newList: List<Music>) {
        tracks = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = MusicItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener { onClick(track) }
        holder.itemView.setOnLongClickListener {
            onLongClick(track)
            true
        }
    }

    override fun getItemCount(): Int = tracks.size
}