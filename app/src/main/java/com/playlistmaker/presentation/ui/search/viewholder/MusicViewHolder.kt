package com.playlistmaker.presentation.ui.search.viewholder

import android.content.Context
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.playlistmaker.R
import com.playlistmaker.databinding.MusicItemBinding
import com.playlistmaker.domain.models.Music


class MusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val binding = MusicItemBinding.bind(itemView)

    fun bind(music: Music) = with(binding) {
        musicTitle.text = music.trackName
        artistName.text = music.artistName
        trackProgress.text = music.trackTimeMillis?.let { formatTrackTime(it) }

        Glide.with(itemView)
            .load(music.artworkUrl100)
            .centerCrop()
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(dpToPx(4f, itemView.context)))
            .into(poster)
    }

    private fun formatTrackTime(milliseconds: Int): String {
        val seconds = (milliseconds / 1000) % 60
        val minutes = (milliseconds / 1000) / 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics
        ).toInt()

    }
}