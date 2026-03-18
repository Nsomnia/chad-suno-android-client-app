package dev.nsomnia.chadsuno.ui.screens.song

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import dev.nsomnia.chadsuno.ui.theme.NeonGreen
import dev.nsomnia.chadsuno.ui.theme.TerminalBlack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongDetailScreen(
    songId: String,
    onNavigateUp: () -> Unit,
    viewModel: SongDetailViewModel = hiltViewModel()
) {
    val song by viewModel.song.collectAsStateWithLifecycle()
    val alignedLyrics by viewModel.alignedLyrics.collectAsStateWithLifecycle()
    val isProcessing by viewModel.isProcessing.collectAsStateWithLifecycle()
    val processingResult by viewModel.processingResult.collectAsStateWithLifecycle()

    LaunchedEffect(songId) {
        viewModel.loadSong(songId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(song?.title ?: "Song Details", color = NeonGreen) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NeonGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        song?.let { currentSong ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SongHeader(song = currentSong)
                
                SongInfoCard(song = currentSong)
                
                LyricsCard(song = currentSong, alignedLyrics = alignedLyrics)

                ActionButtonsCard(
                    song = currentSong,
                    isProcessing = isProcessing,
                    onExtend = { prompt, continueAt, tags -> 
                        viewModel.extendSong(currentSong.id, prompt, continueAt, tags)
                    },
                    onGenerateStems = { viewModel.generateStems(currentSong.id) },
                    onConcatenate = { viewModel.concatenateSong(currentSong.id) }
                )

                processingResult?.let { result ->
                    ResultCard(result = result)
                }

                Spacer(Modifier.height(32.dp))
            }
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = NeonGreen)
            }
        }
    }
}

@Composable
fun SongHeader(song: Song) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = song.imageUrl,
                contentDescription = "Album Art",
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(Modifier.height(16.dp))
            
            Text(
                text = song.title ?: "Untitled",
                style = MaterialTheme.typography.headlineMedium,
                color = NeonGreen
            )
            
            song.tags?.let { tags ->
                Text(
                    text = tags,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun SongInfoCard(song: Song) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Details",
                style = MaterialTheme.typography.titleMedium,
                color = NeonGreen
            )
            Spacer(Modifier.height(12.dp))
            
            InfoRow("ID", song.id.take(12) + "...")
            InfoRow("Status", song.status.name)
            InfoRow("Model", song.modelName)
            song.duration?.let { InfoRow("Duration", it) }
            InfoRow("Created", song.createdAt)
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun LyricsCard(song: Song, alignedLyrics: List<dev.nsomnia.chadsuno.domain.model.AlignedLyricWord>?) {
    var showAligned by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lyrics",
                    style = MaterialTheme.typography.titleMedium,
                    color = NeonGreen
                )
                
                if (alignedLyrics != null) {
                    TextButton(onClick = { showAligned = !showAligned }) {
                        Text(if (showAligned) "Show Plain" else "Show Timestamped")
                    }
                }
            }
            
            Spacer(Modifier.height(12.dp))
            
            song.lyric?.let { lyrics ->
                if (showAligned && alignedLyrics != null) {
                    alignedLyrics.forEach { word ->
                        Text(
                            text = "${word.word} [${String.format("%.2f", word.startS)}s]",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(vertical = 1.dp)
                        )
                    }
                } else {
                    Text(
                        text = lyrics,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } ?: Text(
                text = "No lyrics available",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ActionButtonsCard(
    song: Song,
    isProcessing: Boolean,
    onExtend: (String, Int?, String) -> Unit,
    onGenerateStems: () -> Unit,
    onConcatenate: () -> Unit
) {
    var showExtendDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Actions",
                style = MaterialTheme.typography.titleMedium,
                color = NeonGreen
            )
            
            Spacer(Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActionButton(
                    text = "Extend",
                    icon = Icons.Default.Add,
                    enabled = !isProcessing,
                    onClick = { showExtendDialog = true },
                    modifier = Modifier.weight(1f)
                )
                
                ActionButton(
                    text = "Stems",
                    icon = Icons.Default.GraphicEq,
                    enabled = !isProcessing,
                    onClick = onGenerateStems,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(Modifier.height(8.dp))
            
            ActionButton(
                text = "Concatenate (Full Song)",
                icon = Icons.Default.Merge,
                enabled = !isProcessing,
                onClick = onConcatenate,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    
    if (showExtendDialog) {
        ExtendSongDialog(
            onDismiss = { showExtendDialog = false },
            onExtend = { prompt, continueAt, tags ->
                onExtend(prompt, continueAt, tags)
                showExtendDialog = false
            }
        )
    }
}

@Composable
fun ActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = NeonGreen,
            contentColor = TerminalBlack,
            disabledContainerColor = NeonGreen.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(4.dp))
        Text(text)
    }
}

@Composable
fun ResultCard(result: Result<Any>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (result.isSuccess) NeonGreen.copy(alpha = 0.1f) 
                             else MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (result.isSuccess) "Operation Complete" else "Operation Failed",
                style = MaterialTheme.typography.titleMedium,
                color = if (result.isSuccess) NeonGreen else MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(Modifier.height(8.dp))
            result.onSuccess {
                Text("New content generated successfully!", color = MaterialTheme.colorScheme.onSurface)
            }
            result.onFailure { error ->
                Text("Error: ${error.message}", color = MaterialTheme.colorScheme.onErrorContainer)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtendSongDialog(
    onDismiss: () -> Unit,
    onExtend: (String, Int?, String) -> Unit
) {
    var prompt by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    var continueAtText by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Extend Song") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    label = { Text("Additional Prompt (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("Style Tags (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = continueAtText,
                    onValueChange = { continueAtText = it },
                    label = { Text("Continue At (seconds, optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val continueAt = continueAtText.toIntOrNull()
                    onExtend(prompt, continueAt, tags)
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = TerminalBlack)
            ) {
                Text("Extend")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
