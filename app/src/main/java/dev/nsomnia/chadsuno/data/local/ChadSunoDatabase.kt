package dev.nsomnia.chadsuno.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SongEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ChadSunoDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
}
