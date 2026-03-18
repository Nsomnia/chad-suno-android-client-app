package dev.nsomnia.chadsuno.ui.screens.song

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.nsomnia.chadsuno.data.repository.GenerationRepository
import dev.nsomnia.chadsuno.data.repository.SongRepository
import dev.nsomnia.chadsuno.data.repository.SunoRepository
import dev.nsomnia.chadsuno.domain.model.AlignedLyricWord
import dev.nsomnia.chadsuno.domain.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongDetailViewModel @Inject constructor(
    private val songRepository: SongRepository,
    private val generationRepository: GenerationRepository,
    private val sunoRepository: SunoRepository
) : ViewModel() {

    private val _song = MutableStateFlow<Song?>(null)
    val song: StateFlow<Song?> = _song.asStateFlow()

    private val _alignedLyrics = MutableStateFlow<List<AlignedLyricWord>?>(null)
    val alignedLyrics: StateFlow<List<AlignedLyricWord>?> = _alignedLyrics.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _processingResult = MutableStateFlow<Result<Any>?>(null)
    val processingResult: StateFlow<Result<Any>?> = _processingResult.asStateFlow()

    fun loadSong(songId: String) {
        viewModelScope.launch {
            val localSong = songRepository.getSongById(songId)
            if (localSong != null) {
                _song.value = localSong
            } else {
                val result = songRepository.syncSongById(songId)
                result.onSuccess { _song.value = it }
                result.onFailure { _processingResult.value = Result.failure(it) }
            }
            
            loadAlignedLyrics(songId)
        }
    }

    private fun loadAlignedLyrics(songId: String) {
        viewModelScope.launch {
            val result = sunoRepository.getAlignedLyrics(songId)
            result.onSuccess { _alignedLyrics.value = it }
        }
    }

    fun extendSong(songId: String, prompt: String, continueAt: Int?, tags: String) {
        viewModelScope.launch {
            _isProcessing.value = true
            _processingResult.value = null
            
            val result = generationRepository.extendAudio(
                audioId = songId,
                prompt = prompt,
                continueAt = continueAt,
                tags = tags
            )
            
            _processingResult.value = result
            _isProcessing.value = false
        }
    }

    fun generateStems(songId: String) {
        viewModelScope.launch {
            _isProcessing.value = true
            _processingResult.value = null
            
            val result = generationRepository.generateStems(songId)
            _processingResult.value = result
            _isProcessing.value = false
        }
    }

    fun concatenateSong(songId: String) {
        viewModelScope.launch {
            _isProcessing.value = true
            _processingResult.value = null
            
            val result = generationRepository.concatenateClip(songId)
            _processingResult.value = result
            _isProcessing.value = false
        }
    }
}
