package com.playlistmaker.presentation.ui.media.fragments.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.playlistmaker.R
import com.playlistmaker.databinding.ItemPlaylistSheetBinding
import com.playlistmaker.domain.models.Playlist
import java.io.File


class PlaylistSheetAdapter(
    private val onClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistSheetAdapter.VH>() {

    private var items = listOf<Playlist>()

    fun submitList(list: List<Playlist>) {
        items = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemPlaylistSheetBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(items[position], onClick)

    override fun getItemCount(): Int = items.size

    inner class VH(private val b: ItemPlaylistSheetBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(pl: Playlist, click: (Playlist) -> Unit) {
            b.tvPlaylistNameSheet.text = pl.name
            b.tvTrackCountSheet.text =
                b.root.context.resources.getQuantityString(
                    R.plurals.tracks_count,
                    pl.trackCount,
                    pl.trackCount
                )
            if (pl.coverUri != null) {
                val file = File(b.root.context.filesDir, pl.coverUri)
                b.imgCoverSmall.setImageURI(Uri.fromFile(file))
            } else {
                b.imgCoverSmall.setImageResource(R.drawable.placeholder)
            }
            b.root.setOnClickListener { click(pl) }
        }
    }
}