package dev.nsomnia.chadsuno.ui.screens.create

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.nsomnia.chadsuno.ui.theme.NeonGreen
import dev.nsomnia.chadsuno.ui.theme.TerminalBlack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScreen(
    viewModel: CreateViewModel = hiltViewModel()
) {
    val selectedMode by viewModel.selectedMode.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val generatedResult by viewModel.generatedResult.collectAsStateWithLifecycle()
    val generatedLyrics by viewModel.generatedLyrics.collectAsStateWithLifecycle()
    
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Create Music",
                        color = NeonGreen
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ModeSelector(
                selectedMode = selectedMode,
                onModeSelected = { viewModel.setMode(it) }
            )

            AnimatedVisibility(
                visible = selectedMode == CreateMode.SIMPLE,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                SimpleModeContent(
                    prompt = viewModel.simplePrompt.collectAsStateWithLifecycle().value,
                    onPromptChange = { viewModel.updateSimplePrompt(it) },
                    makeInstrumental = viewModel.makeInstrumental.collectAsStateWithLifecycle().value,
                    onMakeInstrumentalChange = { viewModel.updateMakeInstrumental(it) },
                    waitAudio = viewModel.waitAudio.collectAsStateWithLifecycle().value,
                    onWaitAudioChange = { viewModel.updateWaitAudio(it) },
                    model = viewModel.model.collectAsStateWithLifecycle().value,
                    onModelChange = { viewModel.updateModel(it) }
                )
            }

            AnimatedVisibility(
                visible = selectedMode == CreateMode.CUSTOM,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                CustomModeContent(
                    title = viewModel.customTitle.collectAsStateWithLifecycle().value,
                    onTitleChange = { viewModel.updateCustomTitle(it) },
                    lyrics = viewModel.customLyrics.collectAsStateWithLifecycle().value,
                    onLyricsChange = { viewModel.updateCustomLyrics(it) },
                    tags = viewModel.customTags.collectAsStateWithLifecycle().value,
                    onTagsChange = { viewModel.updateCustomTags(it) },
                    makeInstrumental = viewModel.makeInstrumental.collectAsStateWithLifecycle().value,
                    onMakeInstrumentalChange = { viewModel.updateMakeInstrumental(it) },
                    model = viewModel.model.collectAsStateWithLifecycle().value,
                    onModelChange = { viewModel.updateModel(it) }
                )
            }

            AnimatedVisibility(
                visible = selectedMode == CreateMode.LYRICS,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                LyricsModeContent(
                    prompt = viewModel.lyricsPrompt.collectAsStateWithLifecycle().value,
                    onPromptChange = { viewModel.updateLyricsPrompt(it) },
                    generatedLyrics = generatedLyrics,
                    isGenerating = isGenerating
                )
            }

            if (selectedMode != CreateMode.LYRICS) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = { viewModel.generate() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !isGenerating && viewModel.canGenerate(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonGreen,
                        contentColor = TerminalBlack,
                        disabledContainerColor = NeonGreen.copy(alpha = 0.3f),
                        disabledContentColor = TerminalBlack.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = TerminalBlack
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Generating...")
                    } else {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Generate", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            generatedResult?.let { result ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (result.isSuccess) 
                            NeonGreen.copy(alpha = 0.1f) 
                        else 
                            MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (result.isSuccess) "Generation Complete!" else "Generation Failed",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (result.isSuccess) NeonGreen else MaterialTheme.colorScheme.onErrorContainer
                        )
                        if (result.isSuccess) {
                            result.getOrNull()?.let { songs ->
                                Text(
                                    text = "${songs.size} song(s) generated",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        } else {
                            Text(
                                text = result.exceptionOrNull()?.message ?: "Unknown error",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun ModeSelector(
    selectedMode: CreateMode,
    onModeSelected: (CreateMode) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CreateMode.entries.forEach { mode ->
            FilterChip(
                selected = selectedMode == mode,
                onClick = { onModeSelected(mode) },
                label = { Text(mode.label) },
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = NeonGreen,
                    selectedLabelColor = TerminalBlack
                )
            )
        }
    }
}

@Composable
fun SimpleModeContent(
    prompt: String,
    onPromptChange: (String) -> Unit,
    makeInstrumental: Boolean,
    onMakeInstrumentalChange: (Boolean) -> Unit,
    waitAudio: Boolean,
    onWaitAudioChange: (Boolean) -> Unit,
    model: String,
    onModelChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = prompt,
            onValueChange = onPromptChange,
            label = { Text("Describe your music") },
            placeholder = { Text("A chill lo-fi beat with soft piano...") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp),
            maxLines = 5
        )

        ModelSelector(model = model, onModelChange = onModelChange)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = makeInstrumental,
                    onCheckedChange = onMakeInstrumentalChange,
                    colors = CheckboxDefaults.colors(checkedColor = NeonGreen)
                )
                Text("Instrumental")
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = waitAudio,
                    onCheckedChange = onWaitAudioChange,
                    colors = CheckboxDefaults.colors(checkedColor = NeonGreen)
                )
                Text("Wait for result")
            }
        }
    }
}

@Composable
fun CustomModeContent(
    title: String,
    onTitleChange: (String) -> Unit,
    lyrics: String,
    onLyricsChange: (String) -> Unit,
    tags: String,
    onTagsChange: (String) -> Unit,
    makeInstrumental: Boolean,
    onMakeInstrumentalChange: (Boolean) -> Unit,
    model: String,
    onModelChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Song Title") },
            placeholder = { Text("My Awesome Song") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = lyrics,
            onValueChange = onLyricsChange,
            label = { Text("Lyrics") },
            placeholder = { Text("[Verse 1]\nWrite your lyrics here...") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp),
            maxLines = 10
        )

        OutlinedTextField(
            value = tags,
            onValueChange = onTagsChange,
            label = { Text("Genre/Style Tags") },
            placeholder = { Text("pop, rock, male vocals, melancholic") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        ModelSelector(model = model, onModelChange = onModelChange)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = makeInstrumental,
                onCheckedChange = onMakeInstrumentalChange,
                colors = CheckboxDefaults.colors(checkedColor = NeonGreen)
            )
            Text("Instrumental (no vocals)")
        }
    }
}

@Composable
fun LyricsModeContent(
    prompt: String,
    onPromptChange: (String) -> Unit,
    generatedLyrics: String?,
    isGenerating: Boolean
) {
    val viewModel: CreateViewModel = hiltViewModel()
    
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = prompt,
            onValueChange = onPromptChange,
            label = { Text("Describe what you want lyrics about") },
            placeholder = { Text("A song about summer nights and young love...") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp),
            maxLines = 4
        )

        Button(
            onClick = { viewModel.generateLyrics() },
            modifier = Modifier.fillMaxWidth(),
            enabled = prompt.isNotBlank() && !isGenerating,
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonGreen,
                contentColor = TerminalBlack
            )
        ) {
            if (isGenerating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = TerminalBlack
                )
                Spacer(Modifier.width(8.dp))
                Text("Generating Lyrics...")
            } else {
                Icon(Icons.Default.Edit, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Generate Lyrics")
            }
        }

        generatedLyrics?.let { lyrics ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = NeonGreen.copy(alpha = 0.05f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Generated Lyrics",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonGreen
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = lyrics,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelSelector(
    model: String,
    onModelChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val models = listOf("chirp-v3-5" to "v3.5 (Latest)", "chirp-v3-0" to "v3.0")

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = models.find { it.first == model }?.second ?: model,
            onValueChange = {},
            readOnly = true,
            label = { Text("Model") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            models.forEach { (value, label) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onModelChange(value)
                        expanded = false
                    }
                )
            }
        }
    }
}

enum class CreateMode(val label: String) {
    SIMPLE("Simple"),
    CUSTOM("Custom"),
    LYRICS("Lyrics")
}
