package com.example.musicapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.musicapp.ui.screens.DetailScreen
import com.example.musicapp.ui.screens.HomeScreen

object Routes {
    const val HOME = "home"
    const val DETAIL = "detail/{albumId}"
    fun detail(id: String) = "detail/$id"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(onAlbumClick = { album ->
                navController.navigate(Routes.detail(album.id))
            })
        }
        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("albumId") { type = NavType.StringType })
        ) { backStackEntry ->
            val albumId = backStackEntry.arguments?.getString("albumId").orEmpty()
            DetailScreen(albumId = albumId, onBack = { navController.popBackStack() })
        }
    }
}
