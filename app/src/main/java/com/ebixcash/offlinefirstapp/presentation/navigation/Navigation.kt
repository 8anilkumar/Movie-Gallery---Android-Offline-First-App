package com.ebixcash.offlinefirstapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ebixcash.offlinefirstapp.presentation.ui.screens.AddMovieScreen
import com.ebixcash.offlinefirstapp.presentation.ui.screens.MovieListScreen

sealed class NavigationRoute(val route: String) {
    object MovieList : NavigationRoute("movie_list")
    object AddMovie : NavigationRoute("add_movie")
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.MovieList.route
    ) {
        composable(NavigationRoute.MovieList.route) {
            MovieListScreen(
                onAddMovieClick = {
                    navController.navigate(NavigationRoute.AddMovie.route)
                }
            )
        }

        composable(NavigationRoute.AddMovie.route) {
            AddMovieScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
