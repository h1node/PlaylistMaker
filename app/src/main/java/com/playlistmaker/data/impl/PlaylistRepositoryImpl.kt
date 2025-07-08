package com.playlistmaker.data.impl

import com.playlistmaker.data.db.dao.PlaylistDao
import com.playlistmaker.data.db.dao.PlaylistMusicDao
import com.playlistmaker.data.db.entity.PlaylistEntity
import com.playlistmaker.data.db.entity.PlaylistMusicEntity
import com.playlistmaker.domain.models.Music
import com.playlistmaker.domain.models.Playlist
import com.playlistmaker.domain.repositories.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map


class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistMusicDao: PlaylistMusicDao
) : PlaylistRepository {

    override suspend fun savePlaylist(playlist: Playlist) {
        val entity = PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            coverFileName = playlist.coverUri?.substringAfterLast('/')
        )
        playlistDao.save(entity)
    }

    private fun PlaylistEntity.toPlaylistFlow(): Flow<Playlist> =
        playlistMusicDao
            .countTracksInPlaylist(id)
            .map { cnt ->
                Playlist(
                    id = id,
                    name = name,
                    description = description,
                    coverUri = coverFileName?.let { "playlist_covers/$it" },
                    trackCount = cnt
                )
            }

    override fun getAllPlaylists(): Flow<List<Playlist>> =
        playlistDao.getAllPlaylists()
            .flatMapLatest { entities ->
                if (entities.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    val flows: List<Flow<Playlist>> = entities.map { it.toPlaylistFlow() }
                    combine(flows) { playlistsArray ->
                        playlistsArray.toList()
                    }
                }
            }

    override suspend fun addTrackToPlaylist(playlistId: Long, track: Music): Boolean {
        val existingIds = playlistMusicDao.getTrackIdsForPlaylist(playlistId)
        if (track.trackId in existingIds) return false

        val pmEntity = PlaylistMusicEntity(
            playlistId = playlistId,
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            primaryGenreName = track.primaryGenreName,
            releaseDate = track.releaseDate,
            country = track.country,
            previewUrl = track.previewUrl
        )
        playlistMusicDao.insertTrack(pmEntity)
        return true
    }
}

