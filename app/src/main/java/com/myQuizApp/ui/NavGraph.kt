package com.myQuizApp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.myQuizApp.ui.quiz.QuizScreen
import com.myQuizApp.ui.quiz.QuizViewModel
import com.myQuizApp.ui.quiz.ResultsScreen

sealed class Screen(val route: String) {
    object Quiz : Screen("quiz")
    object Results : Screen("results")
}

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Quiz.route,
        modifier = modifier
    ) {
        composable(Screen.Quiz.route) {
            val viewModel: QuizViewModel = hiltViewModel()
            QuizScreen(
                viewModel = viewModel,
                onFinished = {
                    navController.navigate(Screen.Results.route) {
                        popUpTo(Screen.Quiz.route) { inclusive = false }
                    }
                }
            )
        }
        composable(Screen.Results.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Quiz.route)
            }
            val viewModel: QuizViewModel = hiltViewModel(parentEntry)
            
            ResultsScreen(
                viewModel = viewModel,
                onRestart = {
                    viewModel.restartQuiz()
                    navController.popBackStack(Screen.Quiz.route, inclusive = false)
                }
            )
        }
    }
}
