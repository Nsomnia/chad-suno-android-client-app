package dev.nsomnia.chadsuno.ui.screens.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.nsomnia.chadsuno.ui.theme.NeonGreen
import dev.nsomnia.chadsuno.ui.theme.NeonGreenDim
import dev.nsomnia.chadsuno.ui.theme.TerminalBlack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val clipboardManager = LocalClipboardManager.current
    val cookie by viewModel.cookie.collectAsStateWithLifecycle()
    val isValid by viewModel.isValid.collectAsStateWithLifecycle()
    val isVerifying by viewModel.isVerifying.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    var showCookie by remember { mutableStateOf(false) }
    var cookieInput by remember { mutableStateOf(cookie ?: "") }

    LaunchedEffect(cookie) {
        cookieInput = cookie ?: ""
    }

    LaunchedEffect(isValid) {
        if (isValid) {
            onComplete()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.MusicNote,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = NeonGreen
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "ChadSuno",
            style = MaterialTheme.typography.headlineLarge,
            color = NeonGreen
        )

        Text(
            text = "The Superior Suno Client",
            style = MaterialTheme.typography.titleMedium,
            color = NeonGreenDim
        )

        Spacer(Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Connect Your Suno Account",
                    style = MaterialTheme.typography.titleLarge,
                    color = NeonGreen
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "To use ChadSuno, you need to authenticate with your Suno account. Choose one of these methods:",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = cookieInput,
            onValueChange = {
                cookieInput = it
                viewModel.updateCookie(it)
            },
            label = { Text("Paste Cookie / Bearer Token") },
            placeholder = { Text("Paste your authentication token here") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5,
            visualTransformation = if (showCookie) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                Row {
                    IconButton(onClick = {
                        val clipText = clipboardManager.getText()?.text ?: ""
                        if (clipText.isNotBlank()) {
                            cookieInput = clipText
                            viewModel.updateCookie(clipText)
                        }
                    }) {
                        Icon(Icons.Default.ContentPaste, "Paste from clipboard", tint = NeonGreen)
                    }
                    IconButton(onClick = { showCookie = !showCookie }) {
                        Icon(
                            if (showCookie) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (showCookie) "Hide" else "Show",
                            tint = NeonGreen
                        )
                    }
                }
            },
            isError = error != null,
            supportingText = error?.let { { Text(it, color = MaterialTheme.colorScheme.error) } }
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { viewModel.verifyAndSave() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = cookieInput.isNotBlank() && !isVerifying,
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonGreen,
                contentColor = TerminalBlack,
                disabledContainerColor = NeonGreen.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isVerifying) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = TerminalBlack
                )
                Spacer(Modifier.width(8.dp))
                Text("Verifying...")
            } else {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Connect", style = MaterialTheme.typography.titleMedium)
            }
        }

        Spacer(Modifier.height(32.dp))

        HowToGetTokenCard()

        Spacer(Modifier.height(24.dp))

        Text(
            text = "\"I use arch, btw.\"",
            style = MaterialTheme.typography.labelSmall,
            color = NeonGreenDim
        )
    }
}

@Composable
fun HowToGetTokenCard() {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "How to get your token?",
                    style = MaterialTheme.typography.titleMedium,
                    color = NeonGreen
                )
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = NeonGreen
                    )
                }
            }

            if (expanded) {
                Spacer(Modifier.height(12.dp))

                Text(
                    text = """
Method 1: From Browser (Recommended)
1. Open suno.com in Chrome/Firefox
2. Log in to your account
3. Press F12 (Developer Tools)
4. Go to "Network" tab
5. Refresh page or create a song
6. Click any request to suno.com
7. Find "Cookie" in Request Headers
8. Copy the entire cookie value

Method 2: From Official App
1. Use a packet capture app
2. Look for requests to suno.com
3. Extract the Authorization header

Method 3: Full Cookie String
Paste the entire cookie string from your browser.
ChadSuno will extract what it needs.
                    """.trimIndent(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
