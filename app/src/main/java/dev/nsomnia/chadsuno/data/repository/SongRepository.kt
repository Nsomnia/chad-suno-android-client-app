package dev.nsomnia.chadsuno.data.repository

import dev.nsomnia.chadsuno.data.local.SongDao
import dev.nsomnia.chadsuno.data.local.SongEntity
import dev.nsomnia.chadsuno.data.remote.SunoApiClient
import dev.nsomnia.chadsuno.data.remote.SunoApiService
import dev.nsomnia.chadsuno.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SongRepository @Inject constructor(
    private val songDao: SongDao,
    private val apiService: SunoApiService
) {
    fun getAllSongs(): Flow<List<Song>> {
        return songDao.getAllSongs().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    fun searchSongs(query: String): Flow<List<Song>> {
        return songDao.searchSongs(query).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    fun getSongsByStatus(status: SongStatus): Flow<List<Song>> {
        return songDao.getSongsByStatus(status.name).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    fun getSongsByTag(tag: String): Flow<List<Song>> {
        return songDao.getSongsByTag(tag).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    suspend fun getSongById(id: String): Song? {
        return songDao.getSongById(id)?.toDomainModel()
    }

    suspend fun refreshSongsFromApi(): Result<Unit> {
        return try {
            val response = apiService.getSongs()
            if (response.isSuccessful) {
                val songs = response.body() ?: emptyList()
                songDao.insertSongs(songs.map { SongEntity.fromDomainModel(it) })
                Result.success(Unit)
            } else {
                Result.failure(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncSongById(id: String): Result<Song> {
        return try {
            val response = apiService.getClip(id)
            if (response.isSuccessful) {
                val song = response.body()!!
                songDao.insertSong(SongEntity.fromDomainModel(song))
                Result.success(song)
            } else {
                Result.failure(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteSongs(ids: List<String>) {
        songDao.deleteSongsByIds(ids)
    }

    suspend fun clearCache() {
        songDao.deleteAllSongs()
    }
}
