package dev.nsomnia.chadsuno.data.repository

import dev.nsomnia.chadsuno.data.remote.SunoApiService
import dev.nsomnia.chadsuno.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GenerationRepository @Inject constructor(
    private val apiService: SunoApiService
) {
    suspend fun generateSimple(
        prompt: String,
        makeInstrumental: Boolean = false,
        model: String = "chirp-v3-5",
        waitAudio: Boolean = false
    ): Result<List<Song>> {
        return try {
            val response = apiService.generate(
                GenerateRequest(
                    prompt = prompt,
                    makeInstrumental = makeInstrumental,
                    model = model,
                    waitAudio = waitAudio
                )
            )
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("API Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateCustom(
        prompt: String,
        tags: String,
        title: String,
        makeInstrumental: Boolean = false,
        model: String = "chirp-v3-5",
        waitAudio: Boolean = false,
        negativeTags: String? = null
    ): Result<List<Song>> {
        return try {
            val response = apiService.customGenerate(
                CustomGenerateRequest(
                    prompt = prompt,
                    tags = tags,
                    title = title,
                    makeInstrumental = makeInstrumental,
                    model = model,
                    waitAudio = waitAudio,
                    negativeTags = negativeTags
                )
            )
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("API Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateLyrics(prompt: String): Result<LyricsResponse> {
        return try {
            val response = apiService.generateLyrics(GenerateLyricsRequest(prompt))
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("API Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun extendAudio(
        audioId: String,
        prompt: String = "",
        continueAt: Int? = null,
        tags: String = "",
        negativeTags: String = "",
        title: String = "",
        model: String = "chirp-v3-5",
        waitAudio: Boolean = false
    ): Result<List<Song>> {
        return try {
            val response = apiService.extendAudio(
                ExtendAudioRequest(
                    audioId = audioId,
                    prompt = prompt,
                    continueAt = continueAt,
                    tags = tags,
                    negativeTags = negativeTags,
                    title = title,
                    model = model,
                    waitAudio = waitAudio
                )
            )
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("API Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateStems(audioId: String): Result<List<Song>> {
        return try {
            val response = apiService.generateStems(GenerateStemsRequest(audioId))
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("API Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun concatenateClip(clipId: String): Result<Song> {
        return try {
            val response = apiService.concat(ConcatRequest(clipId))
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
