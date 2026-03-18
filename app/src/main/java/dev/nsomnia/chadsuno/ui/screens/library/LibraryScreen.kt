package dev.nsomnia.chadsuno.ui.screens.library

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import dev.nsomnia.chadsuno.domain.model.Song
import dev.nsomnia.chadsuno.domain.model.SongStatus
import dev.nsomnia.chadsuno.ui.theme.NeonGreen
import dev.nsomnia.chadsuno.ui.theme.NeonGreenDim
import dev.nsomnia.chadsuno.ui.theme.TerminalBlack
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onSongClick: (String) -> Unit,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val songs by viewModel.songs.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val selectedSongs by viewModel.selectedSongs.collectAsStateWithLifecycle()
    val isSelectionMode by viewModel.isSelectionMode.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    Scaffold(
        topBar = {
            LibraryTopBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                isSelectionMode = isSelectionMode,
                selectedCount = selectedSongs.size,
                onSelectAll = { viewModel.selectAll() },
                onClearSelection = { viewModel.clearSelection() },
                onDeleteSelected = { viewModel.deleteSelected() }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !isSelectionMode,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                FloatingActionButton(
                    onClick = { viewModel.refresh() },
                    containerColor = NeonGreen,
                    contentColor = TerminalBlack
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isRefreshing) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = NeonGreen
                )
            } else if (songs.isEmpty()) {
                EmptyLibraryState()
            } else {
                SongList(
                    songs = songs,
                    listState = listState,
                    selectedSongs = selectedSongs,
                    isSelectionMode = isSelectionMode,
                    onSongClick = onSongClick,
                    onSongLongClick = { songId -> viewModel.toggleSelection(songId) },
                    onSongSelect = { songId -> viewModel.toggleSelection(songId) }
                )
            }
        }

        if (isSelectionMode && selectedSongs.isNotEmpty()) {
            SelectionBottomBar(
                selectedCount = selectedSongs.size,
                onDelete = { viewModel.deleteSelected() },
                onClear = { viewModel.clearSelection() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isSelectionMode: Boolean,
    selectedCount: Int,
    onSelectAll: () -> Unit,
    onClearSelection: () -> Unit,
    onDeleteSelected: () -> Unit
) {
    TopAppBar(
        title = {
            if (isSelectionMode) {
                Text("$selectedCount selected")
            } else {
                Text("ChadSuno Library")
            }
        },
        actions = {
            if (isSelectionMode) {
                IconButton(onClick = onSelectAll) {
                    Icon(Icons.Default.SelectAll, contentDescription = "Select All")
                }
                IconButton(onClick = onDeleteSelected) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
                IconButton(onClick = onClearSelection) {
                    Icon(Icons.Default.Close, contentDescription = "Clear")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = NeonGreen
        )
    )
}

@Composable
fun EmptyLibraryState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.LibraryMusic,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = NeonGreenDim
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No songs yet",
                style = MaterialTheme.typography.headlineSmall,
                color = NeonGreenDim
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Pull down to refresh or create some bangers!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongList(
    songs: List<Song>,
    listState: LazyListState,
    selectedSongs: Set<String>,
    isSelectionMode: Boolean,
    onSongClick: (String) -> Unit,
    onSongLongClick: (String) -> Unit,
    onSongSelect: (String) -> Unit
) {
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(songs, key = { it.id }) { song ->
            SongItem(
                song = song,
                isSelected = selectedSongs.contains(song.id),
                isSelectionMode = isSelectionMode,
                onClick = {
                    if (isSelectionMode) {
                        onSongSelect(song.id)
                    } else {
                        onSongClick(song.id)
                    }
                },
                onLongClick = { onSongLongClick(song.id) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongItem(
    song: Song,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val cardColors = if (isSelected) {
        CardDefaults.cardColors(containerColor = NeonGreen.copy(alpha = 0.15f))
    } else {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (!isSelectionMode) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier.clickable(onClick = onClick)
                }
            ),
        colors = cardColors,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onClick() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = NeonGreen,
                        uncheckedColor = NeonGreenDim
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            AsyncImage(
                model = song.imageUrl,
                contentDescription = "Album art",
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title ?: "Untitled",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (isSelected) NeonGreen else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusChip(status = song.status)
                    song.duration?.let { duration ->
                        Text(
                            text = duration,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                song.tags?.let { tags ->
                    if (tags.isNotBlank()) {
                        Text(
                            text = tags.take(50),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (!isSelectionMode) {
                IconButton(onClick = onClick) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = NeonGreen
                    )
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: SongStatus) {
    val (backgroundColor, textColor) = when (status) {
        SongStatus.COMPLETE -> NeonGreen.copy(alpha = 0.2f) to NeonGreen
        SongStatus.STREAMING -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        SongStatus.QUEUED -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        SongStatus.SUBMITTED -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        SongStatus.ERROR -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    }

    Surface(
        shape = RoundedCornerShape(4.dp),
        color = backgroundColor,
        modifier = Modifier.height(20.dp)
    ) {
        Text(
            text = status.name.lowercase().replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun SelectionBottomBar(
    selectedCount: Int,
    onDelete: () -> Unit,
    onClear: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$selectedCount song(s) selected",
                style = MaterialTheme.typography.bodyMedium
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onClear) {
                    Text("Cancel")
                }
                FilledTonalButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Delete")
                }
            }
        }
    }
}
