package com.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.playlistmaker.data.db.dao.MusicDao
import com.playlistmaker.data.db.entity.MusicEntity


@Database(entities = [MusicEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun musicDao(): MusicDao
}