package dev.nsomnia.chadsuno.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Query("SELECT * FROM songs ORDER BY createdAt DESC")
    fun getAllSongs(): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE id = :id")
    suspend fun getSongById(id: String): SongEntity?

    @Query("SELECT * FROM songs WHERE id IN (:ids)")
    suspend fun getSongsByIds(ids: List<String>): List<SongEntity>

    @Query("SELECT * FROM songs WHERE title LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchSongs(query: String): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE status = :status ORDER BY createdAt DESC")
    fun getSongsByStatus(status: String): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE tags LIKE '%' || :tag || '%' ORDER BY createdAt DESC")
    fun getSongsByTag(tag: String): Flow<List<SongEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: SongEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<SongEntity>)

    @Update
    suspend fun updateSong(song: SongEntity)

    @Delete
    suspend fun deleteSong(song: SongEntity)

    @Query("DELETE FROM songs WHERE id IN (:ids)")
    suspend fun deleteSongsByIds(ids: List<String>)

    @Query("DELETE FROM songs")
    suspend fun deleteAllSongs()

    @Query("SELECT COUNT(*) FROM songs")
    suspend fun getSongCount(): Int
}
