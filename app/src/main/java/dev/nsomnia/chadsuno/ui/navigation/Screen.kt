package dev.nsomnia.chadsuno.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Library : Screen(
        route = "library",
        title = "Library",
        icon = Icons.Default.LibraryMusic
    )

    data object Create : Screen(
        route = "create",
        title = "Create",
        icon = Icons.Default.Create
    )

    data object Settings : Screen(
        route = "settings",
        title = "Settings",
        icon = Icons.Default.Settings
    )

    data object SongDetail : Screen(
        route = "song/{songId}",
        title = "Song",
        icon = Icons.Default.LibraryMusic
    ) {
        fun createRoute(songId: String) = "song/$songId"
    }

    data object Player : Screen(
        route = "player",
        title = "Player",
        icon = Icons.Default.LibraryMusic
    )
}

val bottomNavScreens = listOf(
    Screen.Library,
    Screen.Create,
    Screen.Settings
)
