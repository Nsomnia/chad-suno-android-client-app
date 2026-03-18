package dev.nsomnia.chadsuno.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.nsomnia.chadsuno.ui.screens.create.CreateScreen
import dev.nsomnia.chadsuno.ui.screens.library.LibraryScreen
import dev.nsomnia.chadsuno.ui.screens.settings.SettingsScreen
import dev.nsomnia.chadsuno.ui.screens.song.SongDetailScreen

@Composable
fun ChadSunoNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Library.route
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(300)
            )
        }
    ) {
        composable(Screen.Library.route) {
            LibraryScreen(
                onSongClick = { songId ->
                    navController.navigate(Screen.SongDetail.createRoute(songId))
                }
            )
        }
        
        composable(Screen.Create.route) {
            CreateScreen()
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
        
        composable(
            route = Screen.SongDetail.route,
            arguments = listOf(
                navArgument("songId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val songId = backStackEntry.arguments?.getString("songId") ?: ""
            SongDetailScreen(
                songId = songId,
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}
