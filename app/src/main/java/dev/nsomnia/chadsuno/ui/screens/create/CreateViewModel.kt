package dev.nsomnia.chadsuno.ui.screens.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.nsomnia.chadsuno.data.repository.GenerationRepository
import dev.nsomnia.chadsuno.data.repository.SongRepository
import dev.nsomnia.chadsuno.domain.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateViewModel @Inject constructor(
    private val generationRepository: GenerationRepository,
    private val songRepository: SongRepository
) : ViewModel() {

    private val _selectedMode = MutableStateFlow(CreateMode.SIMPLE)
    val selectedMode: StateFlow<CreateMode> = _selectedMode.asStateFlow()

    private val _simplePrompt = MutableStateFlow("")
    val simplePrompt: StateFlow<String> = _simplePrompt.asStateFlow()

    private val _customTitle = MutableStateFlow("")
    val customTitle: StateFlow<String> = _customTitle.asStateFlow()

    private val _customLyrics = MutableStateFlow("")
    val customLyrics: StateFlow<String> = _customLyrics.asStateFlow()

    private val _customTags = MutableStateFlow("")
    val customTags: StateFlow<String> = _customTags.asStateFlow()

    private val _lyricsPrompt = MutableStateFlow("")
    val lyricsPrompt: StateFlow<String> = _lyricsPrompt.asStateFlow()

    private val _makeInstrumental = MutableStateFlow(false)
    val makeInstrumental: StateFlow<Boolean> = _makeInstrumental.asStateFlow()

    private val _waitAudio = MutableStateFlow(false)
    val waitAudio: StateFlow<Boolean> = _waitAudio.asStateFlow()

    private val _model = MutableStateFlow("chirp-v3-5")
    val model: StateFlow<String> = _model.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _generatedResult = MutableStateFlow<Result<List<Song>>?>(null)
    val generatedResult: StateFlow<Result<List<Song>>?> = _generatedResult.asStateFlow()

    private val _generatedLyrics = MutableStateFlow<String?>(null)
    val generatedLyrics: StateFlow<String?> = _generatedLyrics.asStateFlow()

    fun setMode(mode: CreateMode) {
        _selectedMode.value = mode
        _generatedResult.value = null
        _generatedLyrics.value = null
    }

    fun updateSimplePrompt(value: String) { _simplePrompt.value = value }
    fun updateCustomTitle(value: String) { _customTitle.value = value }
    fun updateCustomLyrics(value: String) { _customLyrics.value = value }
    fun updateCustomTags(value: String) { _customTags.value = value }
    fun updateLyricsPrompt(value: String) { _lyricsPrompt.value = value }
    fun updateMakeInstrumental(value: Boolean) { _makeInstrumental.value = value }
    fun updateWaitAudio(value: Boolean) { _waitAudio.value = value }
    fun updateModel(value: String) { _model.value = value }

    fun canGenerate(): Boolean {
        return when (_selectedMode.value) {
            CreateMode.SIMPLE -> _simplePrompt.value.isNotBlank()
            CreateMode.CUSTOM -> _customLyrics.value.isNotBlank() && 
                                 _customTags.value.isNotBlank() && 
                                 _customTitle.value.isNotBlank()
            CreateMode.LYRICS -> _lyricsPrompt.value.isNotBlank()
        }
    }

    fun generate() {
        viewModelScope.launch {
            _isGenerating.value = true
            _generatedResult.value = null

            val result = when (_selectedMode.value) {
                CreateMode.SIMPLE -> generationRepository.generateSimple(
                    prompt = _simplePrompt.value,
                    makeInstrumental = _makeInstrumental.value,
                    model = _model.value,
                    waitAudio = _waitAudio.value
                )
                CreateMode.CUSTOM -> generationRepository.generateCustom(
                    prompt = _customLyrics.value,
                    tags = _customTags.value,
                    title = _customTitle.value,
                    makeInstrumental = _makeInstrumental.value,
                    model = _model.value,
                    waitAudio = _waitAudio.value
                )
                CreateMode.LYRICS -> {
                    Result.failure(Exception("Use generateLyrics() for lyrics mode"))
                }
            }

            _generatedResult.value = result
            _isGenerating.value = false
        }
    }

    fun generateLyrics() {
        viewModelScope.launch {
            _isGenerating.value = true
            _generatedLyrics.value = null

            val result = generationRepository.generateLyrics(_lyricsPrompt.value)
            
            result.onSuccess { response ->
                var lyrics = response.text ?: ""
                var pollCount = 0
                while (response.status != "complete" && pollCount < 30) {
                    kotlinx.coroutines.delay(2000)
                    pollCount++
                }
                _generatedLyrics.value = lyrics.ifBlank { response.text }
            }
            
            result.onFailure { error ->
                _generatedLyrics.value = "Error: ${error.message}"
            }

            _isGenerating.value = false
        }
    }
}
