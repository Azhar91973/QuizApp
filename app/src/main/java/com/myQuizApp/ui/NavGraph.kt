package com.myQuizApp.ui

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.myQuizApp.ui.question.QuizScreen
import com.myQuizApp.ui.question.QuizViewModel
import com.myQuizApp.ui.question.ResultsScreen
import com.myQuizApp.ui.quizCategory.QuizCategoryScreen
import com.myQuizApp.ui.quizCategory.QuizCategoryViewModel

sealed class Screen(val route: String) {
    object QuizCategory : Screen("quizCategory")
    object Quiz : Screen("quiz/{categoryId}/{questionUrl}")
    object Results : Screen("results")
}

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Screen.QuizCategory.route,
        modifier = modifier
    ) {
        composable(Screen.QuizCategory.route) {
            val viewModel: QuizCategoryViewModel = hiltViewModel()
            QuizCategoryScreen(viewModel = viewModel) { categoryId, questionUrl ->
                navController.navigate("quiz/${Uri.encode(categoryId)}/${Uri.encode(questionUrl)}")
            }
        }

        composable(
            Screen.Quiz.route
        ) { backStackEntry ->

            val categoryId = Uri.decode(backStackEntry.arguments?.getString("categoryId")) ?: ""
            val questionUrl = Uri.decode(backStackEntry.arguments?.getString("questionUrl")) ?: ""
            val viewModel: QuizViewModel = hiltViewModel()
            QuizScreen(
                viewModel = viewModel,
                categoryId = categoryId,
                questionUrl = questionUrl,
                onFinished = {
                    navController.navigate(Screen.Results.route) {
                        popUpTo(Screen.Quiz.route) { inclusive = false }
                    }
                })
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
                },
                onBackToCategories = {
                    navController.popBackStack(Screen.QuizCategory.route, inclusive = false)
                }
            )
        }
    }
}
