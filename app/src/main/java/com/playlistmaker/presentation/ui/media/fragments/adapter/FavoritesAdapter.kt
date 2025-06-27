package com.playlistmaker.presentation.ui.media.fragments.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.playlistmaker.data.db.entity.MusicEntity
import com.playlistmaker.databinding.MusicItemBinding
import com.playlistmaker.presentation.ui.media.fragments.viewholder.FavoritesViewHolder


class FavoritesAdapter(
    private val onClick: (MusicEntity) -> Unit
) : RecyclerView.Adapter<FavoritesViewHolder>() {

    private var items: List<MusicEntity> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val binding = MusicItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FavoritesViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        holder.bind(items[position])
    }


    override fun getItemCount() = items.size

    fun submitList(newItems: List<MusicEntity>) {
        items = newItems
        notifyDataSetChanged()
    }
}