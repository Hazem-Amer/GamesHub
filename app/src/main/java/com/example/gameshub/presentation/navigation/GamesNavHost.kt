package com.example.gameshub.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.gameshub.presentation.details.GameDetailsScreen
import com.example.gameshub.presentation.details.GameDetailsViewModel
import com.example.gameshub.presentation.games.GamesListScreen
import com.example.gameshub.presentation.games.GamesViewModel

@Composable
fun GamesNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.GAMES_LIST,
        modifier = modifier
    ) {
        composable(route = Routes.GAMES_LIST) {
            val viewModel: GamesViewModel = hiltViewModel()
            GamesListScreen(
                viewModel = viewModel,
                onGameClick = { gameId -> navController.navigate(Routes.createGameDetailsRoute(gameId)) }
            )
        }
        composable(
            route = "${Routes.GAME_DETAILS}/{${Routes.GAME_ID_ARG}}",
            arguments = listOf(navArgument(Routes.GAME_ID_ARG) { type = NavType.IntType })
        ) {
            val viewModel: GameDetailsViewModel = hiltViewModel()
            GameDetailsScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

