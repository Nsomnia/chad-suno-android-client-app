package dev.nsomnia.chadsuno.domain.model

import com.google.gson.annotations.SerializedName

/**
 * Audio/Song information from Suno API
 * Matches the AudioInfo interface from 3party/suno-api
 */
data class Song(
    val id: String,
    val title: String? = null,
    @SerializedName("image_url")
    val imageUrl: String? = null,
    val lyric: String? = null,
    @SerializedName("audio_url")
    val audioUrl: String? = null,
    @SerializedName("video_url")
    val videoUrl: String? = null,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("model_name")
    val modelName: String,
    val status: SongStatus,
    @SerializedName("gpt_description_prompt")
    val gptDescriptionPrompt: String? = null,
    val prompt: String? = null,
    val tags: String? = null,
    @SerializedName("negative_tags")
    val negativeTags: String? = null,
    val duration: String? = null,
    @SerializedName("error_message")
    val errorMessage: String? = null,
    val type: String? = null
)

enum class SongStatus {
    @SerializedName("submitted")
    SUBMITTED,
    @SerializedName("queued")
    QUEUED,
    @SerializedName("streaming")
    STREAMING,
    @SerializedName("complete")
    COMPLETE,
    @SerializedName("error")
    ERROR;

    companion object {
        fun fromString(value: String): SongStatus = when (value.lowercase()) {
            "submitted" -> SUBMITTED
            "queued" -> QUEUED
            "streaming" -> STREAMING
            "complete" -> COMPLETE
            "error" -> ERROR
            else -> ERROR
        }
    }
}

/**
 * Quota/Credit information from Suno API
 */
data class QuotaInfo(
    @SerializedName("credits_left")
    val creditsLeft: Int,
    val period: String,
    @SerializedName("monthly_limit")
    val monthlyLimit: Int,
    @SerializedName("monthly_usage")
    val monthlyUsage: Int
)

/**
 * Request models for API calls
 */
data class GenerateRequest(
    val prompt: String,
    @SerializedName("make_instrumental")
    val makeInstrumental: Boolean = false,
    val model: String = "chirp-v3-5",
    @SerializedName("wait_audio")
    val waitAudio: Boolean = false
)

data class CustomGenerateRequest(
    val prompt: String,
    val tags: String,
    val title: String,
    @SerializedName("make_instrumental")
    val makeInstrumental: Boolean = false,
    val model: String = "chirp-v3-5",
    @SerializedName("wait_audio")
    val waitAudio: Boolean = false,
    @SerializedName("negative_tags")
    val negativeTags: String? = null
)

data class GenerateLyricsRequest(
    val prompt: String
)

data class ExtendAudioRequest(
    @SerializedName("audio_id")
    val audioId: String,
    val prompt: String = "",
    @SerializedName("continue_at")
    val continueAt: Int? = null,
    val tags: String = "",
    @SerializedName("negative_tags")
    val negativeTags: String = "",
    val title: String = "",
    val model: String = "chirp-v3-5",
    @SerializedName("wait_audio")
    val waitAudio: Boolean = false
)

data class GenerateStemsRequest(
    @SerializedName("audio_id")
    val audioId: String
)

data class ConcatRequest(
    @SerializedName("clip_id")
    val clipId: String
)

/**
 * Response for lyrics generation
 */
data class LyricsResponse(
    val id: String,
    val status: String,
    val text: String? = null
)

/**
 * Aligned lyrics for karaoke-style display
 */
data class AlignedLyricWord(
    val word: String,
    @SerializedName("start_s")
    val startS: Double,
    @SerializedName("end_s")
    val endS: Double,
    val success: Boolean,
    @SerializedName("p_align")
    val pAlign: Double? = null
)

/**
 * Persona/Artist information
 */
data class PersonaResponse(
    val persona: Persona,
    @SerializedName("total_results")
    val totalResults: Int,
    @SerializedName("current_page")
    val currentPage: Int,
    @SerializedName("is_following")
    val isFollowing: Boolean
)

data class Persona(
    val id: String,
    val name: String,
    val description: String,
    @SerializedName("image_s3_id")
    val imageS3Id: String,
    @SerializedName("root_clip_id")
    val rootClipId: String,
    @SerializedName("user_display_name")
    val userDisplayName: String,
    @SerializedName("user_handle")
    val userHandle: String,
    @SerializedName("user_image_url")
    val userImageUrl: String,
    @SerializedName("is_suno_persona")
    val isSunoPersona: Boolean,
    @SerializedName("is_trashed")
    val isTrashed: Boolean,
    @SerializedName("is_owned")
    val isOwned: Boolean,
    @SerializedName("is_public")
    val isPublic: Boolean,
    @SerializedName("is_public_approved")
    val isPublicApproved: Boolean,
    @SerializedName("is_loved")
    val isLoved: Boolean,
    @SerializedName("upvote_count")
    val upvoteCount: Int,
    @SerializedName("clip_count")
    val clipCount: Int
)
