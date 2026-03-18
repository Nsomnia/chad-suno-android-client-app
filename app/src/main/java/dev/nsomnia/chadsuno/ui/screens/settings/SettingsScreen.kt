package dev.nsomnia.chadsuno.ui.screens.settings

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.nsomnia.chadsuno.domain.model.QuotaInfo
import dev.nsomnia.chadsuno.ui.theme.NeonGreen
import dev.nsomnia.chadsuno.ui.theme.TerminalBlack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val cookie by viewModel.cookie.collectAsStateWithLifecycle()
    val apiBaseUrl by viewModel.apiBaseUrl.collectAsStateWithLifecycle()
    val quota by viewModel.quota.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val saveSuccess by viewModel.saveSuccess.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            QuotaCard(quota = quota, isLoading = isLoading, onRefresh = { viewModel.refreshQuota() })

            AuthenticationSection(
                cookie = cookie,
                onCookieChange = { viewModel.updateCookie(it) },
                onSave = { viewModel.saveSettings() },
                saveSuccess = saveSuccess
            )

            ApiConfigSection(
                apiBaseUrl = apiBaseUrl ?: "https://suno.gcui.ai/",
                onApiBaseUrlChange = { viewModel.updateApiBaseUrl(it) },
                onSave = { viewModel.saveSettings() }
            )

            AboutCard()
        }
    }
}

@Composable
fun QuotaCard(
    quota: QuotaInfo?,
    isLoading: Boolean,
    onRefresh: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = NeonGreen.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Credits & Quota",
                    style = MaterialTheme.typography.titleMedium,
                    color = NeonGreen
                )
                IconButton(onClick = onRefresh, enabled = !isLoading) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = NeonGreen
                        )
                    } else {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = NeonGreen
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (quota != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    QuotaItem(
                        label = "Credits Left",
                        value = quota.creditsLeft.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    QuotaItem(
                        label = "Monthly Usage",
                        value = "${quota.monthlyUsage}/${quota.monthlyLimit}",
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Period: ${quota.period}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else if (isLoading) {
                Text(
                    text = "Loading quota information...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = "Configure authentication to see quota",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun QuotaItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = NeonGreen
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun AuthenticationSection(
    cookie: String?,
    onCookieChange: (String) -> Unit,
    onSave: () -> Unit,
    saveSuccess: Boolean?
) {
    var showCookie by remember { mutableStateOf(false) }
    var cookieValue by remember { mutableStateOf(cookie ?: "") }

    LaunchedEffect(cookie) {
        cookieValue = cookie ?: ""
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Authentication",
                style = MaterialTheme.typography.titleMedium,
                color = NeonGreen
            )
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = cookieValue,
                onValueChange = {
                    cookieValue = it
                    onCookieChange(it)
                },
                label = { Text("Suno Cookie / Bearer Token") },
                placeholder = { Text("Paste your __session cookie or full cookie string") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                visualTransformation = if (showCookie) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showCookie = !showCookie }) {
                        Icon(
                            if (showCookie) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (showCookie) "Hide" else "Show"
                        )
                    }
                }
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Get your cookie from suno.com/create:\n1. Open DevTools (F12)\n2. Go to Network tab\n3. Find a request with ?__clerk_api_version\n4. Copy the Cookie header value",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonGreen,
                    contentColor = TerminalBlack
                )
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Save Authentication")
            }

            saveSuccess?.let { success ->
                Spacer(Modifier.height(8.dp))
                Text(
                    text = if (success) "Settings saved successfully!" else "Failed to save settings",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (success) NeonGreen else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun ApiConfigSection(
    apiBaseUrl: String,
    onApiBaseUrlChange: (String) -> Unit,
    onSave: () -> Unit
) {
    var urlValue by remember { mutableStateOf(apiBaseUrl) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "API Configuration",
                style = MaterialTheme.typography.titleMedium,
                color = NeonGreen
            )
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = urlValue,
                onValueChange = {
                    urlValue = it
                    onApiBaseUrlChange(it)
                },
                label = { Text("API Base URL") },
                placeholder = { Text("https://suno.gcui.ai/") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                singleLine = true
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Use a self-hosted suno-api instance or the public demo",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AboutCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ChadSuno",
                style = MaterialTheme.typography.headlineMedium,
                color = NeonGreen
            )
            Text(
                text = "v1.0.0",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "The superior Suno music client.\nBuilt by Chads, for Chads.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "\"I use arch, btw.\"",
                style = MaterialTheme.typography.labelSmall,
                color = NeonGreen.copy(alpha = 0.7f)
            )
        }
    }
}
