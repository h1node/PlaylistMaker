package com.playlistmaker.presentation.ui.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.playlistmaker.R
import com.playlistmaker.domain.models.Music
import com.playlistmaker.presentation.ui.search.viewholder.MusicViewHolder

class MusicRVAdapter(
    private val clickListener: (Music) -> Unit
) :
    RecyclerView.Adapter<MusicViewHolder>() {

    var items: List<Music> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        return MusicViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.music_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        holder.bind(items[position])
        holder.itemView.findViewById<View>(R.id.item_container).setOnClickListener {
            clickListener(items[position])
        }
    }

    override fun getItemCount() = items.size
}

