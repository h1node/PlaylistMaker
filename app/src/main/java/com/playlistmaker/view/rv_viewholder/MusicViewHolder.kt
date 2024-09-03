package com.playlistmaker.view.rv_viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.playlistmaker.R
import com.playlistmaker.Track


class MusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val trackName: TextView = itemView.findViewById(R.id.music_title)
    private val artistName: TextView = itemView.findViewById(R.id.artist_name)
    private val trackTime: TextView = itemView.findViewById(R.id.time)
    private val poster: ImageView = itemView.findViewById(R.id.poster)

    fun bind(track: Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = track.trackTime
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .centerCrop()
            .placeholder(R.drawable.musicians)
            .into(poster)
    }
}