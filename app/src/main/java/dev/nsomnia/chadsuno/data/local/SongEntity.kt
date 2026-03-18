package dev.nsomnia.chadsuno.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.nsomnia.chadsuno.domain.model.SongStatus

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey
    val id: String,
    val title: String?,
    val imageUrl: String?,
    val lyric: String?,
    val audioUrl: String?,
    val videoUrl: String?,
    val createdAt: String,
    val modelName: String,
    val status: String,
    val gptDescriptionPrompt: String?,
    val prompt: String?,
    val tags: String?,
    val negativeTags: String?,
    val duration: String?,
    val errorMessage: String?,
    val type: String?,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    fun toDomainModel(): dev.nsomnia.chadsuno.domain.model.Song {
        return dev.nsomnia.chadsuno.domain.model.Song(
            id = id,
            title = title,
            imageUrl = imageUrl,
            lyric = lyric,
            audioUrl = audioUrl,
            videoUrl = videoUrl,
            createdAt = createdAt,
            modelName = modelName,
            status = SongStatus.fromString(status),
            gptDescriptionPrompt = gptDescriptionPrompt,
            prompt = prompt,
            tags = tags,
            negativeTags = negativeTags,
            duration = duration,
            errorMessage = errorMessage,
            type = type
        )
    }

    companion object {
        fun fromDomainModel(song: dev.nsomnia.chadsuno.domain.model.Song): SongEntity {
            return SongEntity(
                id = song.id,
                title = song.title,
                imageUrl = song.imageUrl,
                lyric = song.lyric,
                audioUrl = song.audioUrl,
                videoUrl = song.videoUrl,
                createdAt = song.createdAt,
                modelName = song.modelName,
                status = song.status.name,
                gptDescriptionPrompt = song.gptDescriptionPrompt,
                prompt = song.prompt,
                tags = song.tags,
                negativeTags = song.negativeTags,
                duration = song.duration,
                errorMessage = song.errorMessage,
                type = song.type
            )
        }
    }
}
