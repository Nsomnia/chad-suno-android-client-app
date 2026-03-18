package dev.nsomnia.chadsuno.ui.screens.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.nsomnia.chadsuno.data.repository.SongRepository
import dev.nsomnia.chadsuno.domain.model.Song
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val songRepository: SongRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _selectedSongs = MutableStateFlow<Set<String>>(emptySet())
    val selectedSongs: StateFlow<Set<String>> = _selectedSongs.asStateFlow()

    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode: StateFlow<Boolean> = _isSelectionMode.asStateFlow()

    val songs: StateFlow<List<Song>> = searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                songRepository.getAllSongs()
            } else {
                songRepository.searchSongs(query)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            songRepository.refreshSongsFromApi()
            _isRefreshing.value = false
        }
    }

    fun toggleSelection(songId: String) {
        val current = _selectedSongs.value.toMutableSet()
        if (current.contains(songId)) {
            current.remove(songId)
        } else {
            current.add(songId)
        }
        _selectedSongs.value = current
        _isSelectionMode.value = current.isNotEmpty()
    }

    fun selectAll() {
        viewModelScope.launch {
            val allSongIds = songs.value.map { it.id }.toSet()
            _selectedSongs.value = allSongIds
            _isSelectionMode.value = true
        }
    }

    fun clearSelection() {
        _selectedSongs.value = emptySet()
        _isSelectionMode.value = false
    }

    fun deleteSelected() {
        viewModelScope.launch {
            val toDelete = _selectedSongs.value.toList()
            songRepository.deleteSongs(toDelete)
            clearSelection()
        }
    }
}
