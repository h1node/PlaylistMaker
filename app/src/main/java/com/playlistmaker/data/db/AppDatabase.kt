package com.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.playlistmaker.data.db.dao.MusicDao
import com.playlistmaker.data.db.dao.PlaylistDao
import com.playlistmaker.data.db.dao.PlaylistMusicDao
import com.playlistmaker.data.db.entity.MusicEntity
import com.playlistmaker.data.db.entity.PlaylistEntity
import com.playlistmaker.data.db.entity.PlaylistMusicEntity


@Database(
    entities = [MusicEntity::class, PlaylistEntity::class, PlaylistMusicEntity::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun musicDao(): MusicDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistMusicDao(): PlaylistMusicDao
}