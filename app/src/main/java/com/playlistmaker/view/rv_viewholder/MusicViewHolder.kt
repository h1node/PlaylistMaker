package com.playlistmaker.view.rv_viewholder

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.playlistmaker.R
import com.playlistmaker.Track
import com.playlistmaker.databinding.MusicItemBinding


class MusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val binding = MusicItemBinding.bind(itemView)

    fun bind(track: Track) = with(binding) {
        musicTitle.text = track.trackName
        artistName.text = track.artistName
        time.text = track.trackTime
        Glide.with(itemView).load(track.artworkUrl100)
            .centerCrop()
            .placeholder(R.drawable.image)
            .transform(RoundedCorners(dpToPx(2f, itemView.context)))
            .into(poster)
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics
        ).toInt()
    }
}