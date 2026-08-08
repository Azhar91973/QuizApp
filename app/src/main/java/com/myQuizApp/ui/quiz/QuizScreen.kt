package com.myQuizApp.ui.quiz

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.myQuizApp.data.model.QuizQuestionModel
import com.myQuizApp.utils.Result


@Composable
fun QuizScreen(modifier: Modifier, viewModel: QuizViewModel = hiltViewModel()) {
    val quizResult = viewModel.quizResponse.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadQuiz()
    }

    when (val result = quizResult.value) {
        is Result.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(strokeWidth = 2.dp)
            }
        }

        is Result.Success -> {
            val questions = result.data
            if (questions.isEmpty()) {
                Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No questions found")
                }
            } else {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(questions) { question ->
                        QuizQuestionItem(question)
                    }
                }
            }
        }

        is Result.Error -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Error: ${result.message}", color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun QuizQuestionItem(question: QuizQuestionModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = question.question ?: "",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        question.options?.forEachIndexed { index, option ->
            Text(
                text = "${index + 1}. $option",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}
