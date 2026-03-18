package dev.nsomnia.chadsuno.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.nsomnia.chadsuno.ui.navigation.ChadSunoNavGraph
import dev.nsomnia.chadsuno.ui.screens.onboarding.OnboardingScreen
import dev.nsomnia.chadsuno.ui.theme.ChadSunoTheme

@Composable
fun MainContent(
    viewModel: MainViewModel = hiltViewModel()
) {
    val isOnboardingComplete by viewModel.isOnboardingComplete.collectAsStateWithLifecycle()
    val hasValidToken by viewModel.hasValidToken.collectAsStateWithLifecycle()

    ChadSunoTheme(darkTheme = true) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when {
                isOnboardingComplete == false -> {
                    OnboardingScreen(
                        onComplete = { viewModel.completeOnboarding() }
                    )
                }
                hasValidToken == false -> {
                    OnboardingScreen(
                        onComplete = { viewModel.validateToken() }
                    )
                }
                else -> {
                    ChadSunoNavGraph()
                }
            }
        }
    }
}
