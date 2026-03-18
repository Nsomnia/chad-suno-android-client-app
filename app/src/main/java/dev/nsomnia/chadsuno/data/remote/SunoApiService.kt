package dev.nsomnia.chadsuno.data.remote

import dev.nsomnia.chadsuno.domain.model.*
import retrofit2.Response
import retrofit2.http.*

interface SunoApiService {

    @GET("api/get_limit")
    suspend fun getLimit(): Response<QuotaInfo>

    @GET("api/get")
    suspend fun getSongs(
        @Query("ids") ids: String? = null,
        @Query("page") page: String? = null
    ): Response<List<Song>>

    @GET("api/clip")
    suspend fun getClip(
        @Query("id") clipId: String
    ): Response<Song>

    @GET("api/get_aligned_lyrics")
    suspend fun getAlignedLyrics(
        @Query("song_id") songId: String
    ): Response<List<AlignedLyricWord>>

    @GET("api/persona")
    suspend fun getPersona(
        @Query("id") personaId: String,
        @Query("page") page: Int? = 1
    ): Response<PersonaResponse>

    @POST("api/generate")
    suspend fun generate(
        @Body request: GenerateRequest
    ): Response<List<Song>>

    @POST("api/custom_generate")
    suspend fun customGenerate(
        @Body request: CustomGenerateRequest
    ): Response<List<Song>>

    @POST("api/generate_lyrics")
    suspend fun generateLyrics(
        @Body request: GenerateLyricsRequest
    ): Response<LyricsResponse>

    @POST("api/extend_audio")
    suspend fun extendAudio(
        @Body request: ExtendAudioRequest
    ): Response<List<Song>>

    @POST("api/generate_stems")
    suspend fun generateStems(
        @Body request: GenerateStemsRequest
    ): Response<List<Song>>

    @POST("api/concat")
    suspend fun concat(
        @Body request: ConcatRequest
    ): Response<Song>
}
