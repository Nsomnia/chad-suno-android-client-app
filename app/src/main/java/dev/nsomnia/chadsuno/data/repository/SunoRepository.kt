package dev.nsomnia.chadsuno.data.repository

import dev.nsomnia.chadsuno.data.remote.SunoApiService
import dev.nsomnia.chadsuno.domain.model.AlignedLyricWord
import dev.nsomnia.chadsuno.domain.model.PersonaResponse
import dev.nsomnia.chadsuno.domain.model.QuotaInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SunoRepository @Inject constructor(
    private val apiService: SunoApiService
) {
    suspend fun getQuota(): Result<QuotaInfo> {
        return try {
            val response = apiService.getLimit()
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("API Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAlignedLyrics(songId: String): Result<List<AlignedLyricWord>> {
        return try {
            val response = apiService.getAlignedLyrics(songId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("API Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPersona(personaId: String, page: Int = 1): Result<PersonaResponse> {
        return try {
            val response = apiService.getPersona(personaId, page)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("API Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
