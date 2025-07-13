package com.playlistmaker.data.impl

import com.playlistmaker.data.db.dao.MusicDao
import com.playlistmaker.data.db.dao.PlaylistDao
import com.playlistmaker.data.db.dao.PlaylistMusicDao
import com.playlistmaker.data.db.entity.PlaylistEntity
import com.playlistmaker.data.db.entity.PlaylistMusicEntity
import com.playlistmaker.domain.models.Music
import com.playlistmaker.domain.models.Playlist
import com.playlistmaker.domain.repositories.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map


class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistMusicDao: PlaylistMusicDao,
    private val musicDao: MusicDao
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

    override suspend fun getPlaylistById(id: Long): Playlist? {
        val entity = playlistDao.getPlaylistById(id) ?: return null
        val trackCount = playlistMusicDao.countTracksInPlaylist(id).first()
        return Playlist(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            coverUri = entity.coverFileName?.let { "playlist_covers/$it" },
            trackCount = trackCount
        )
    }

    override suspend fun getTracksForPlaylist(playlistId: Long): List<Music> {
        val trackEntities = playlistMusicDao.getTracksForPlaylist(playlistId)
        return trackEntities.map {
            Music(
                trackId = it.trackId,
                trackName = it.trackName,
                artistName = it.artistName,
                trackTimeMillis = it.trackTimeMillis,
                artworkUrl100 = it.artworkUrl100,
                collectionName = it.collectionName,
                releaseDate = it.releaseDate,
                primaryGenreName = it.primaryGenreName,
                country = it.country,
                previewUrl = it.previewUrl,
                isFavorite = false
            )
        }
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        playlistMusicDao.deleteTrackFromPlaylist(playlistId, trackId)
        val allTracks = playlistMusicDao.getAllPlaylistTracks()
        val isStillUsed = allTracks.any { it.trackId == trackId }
        if (!isStillUsed) {
            musicDao.deleteTrackById(trackId)
        }
    }

    override suspend fun deleteTrackIfUnused(trackId: Long) {
        val allTracks = playlistMusicDao.getAllPlaylistTracks()
        val isStillUsed = allTracks.any { it.trackId == trackId }
        if (!isStillUsed) {
            musicDao.deleteTrackById(trackId)
        }
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        playlistMusicDao.deleteAllTracksFromPlaylist(playlistId)
        playlistDao.deletePlaylistById(playlistId)

        val usedTrackIds = playlistMusicDao.getAllPlaylistTracks().map { it.trackId }.toSet()
        val allTracks = musicDao.getAllTracks()
        allTracks.filter { it.trackId !in usedTrackIds }
            .forEach { musicDao.deleteTrackById(it.trackId) }
    }
}


