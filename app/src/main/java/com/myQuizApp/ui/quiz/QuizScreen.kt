package com.myQuizApp.ui.quiz

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.myQuizApp.data.model.QuizQuestionModel
import com.myQuizApp.utils.Result

@Composable
fun QuizScreen(modifier: Modifier, viewModel: QuizViewModel = hiltViewModel()) {
    val quizResult by viewModel.quizResponse.collectAsStateWithLifecycle()
    val currentIndex by viewModel.currentIndex.collectAsStateWithLifecycle()
    val userSelections by viewModel.userSelections.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (val result = quizResult) {
            is Result.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(strokeWidth = 2.dp)
                }
            }

            is Result.Success -> {
                val questions = result.data
                if (questions.isNotEmpty()) {
                    QuizContent(
                        question = questions[currentIndex],
                        currentIndex = currentIndex,
                        totalQuestions = questions.size,
                        selectedOption = userSelections[currentIndex],
                        onOptionSelected = { optionIndex ->
                            viewModel.selectOption(currentIndex, optionIndex)
                        },
                        onNext = { viewModel.nextQuestion(questions.size) },
                        onPrevious = { viewModel.previousQuestion() })
                } else {
                    Text("No questions available", modifier = Modifier.align(Alignment.Center))
                }
            }

            is Result.Error -> {
                Text(
                    text = "Error: ${result.message}",
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun QuizContent(
    question: QuizQuestionModel,
    currentIndex: Int,
    totalQuestions: Int,
    selectedOption: Int?,
    onOptionSelected: (Int) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Question: ${currentIndex + 1}/$totalQuestions",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Quit",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { /* Handle Quit */ })
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = question.question ?: "",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            question.options?.forEachIndexed { index, option ->
                val isSelected = selectedOption == index
                OptionItem(
                    text = option, isSelected = isSelected, onClick = { onOptionSelected(index) })
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.clickable { /* Expand Result */ },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "See Result",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = onPrevious,
                enabled = currentIndex > 0,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(40.dp)
            ) {
                Text("Previous", style = MaterialTheme.typography.labelLarge)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = onNext,
                enabled = currentIndex < totalQuestions - 1,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(40.dp)
            ) {
                Text("Next", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
fun OptionItem(
    text: String, isSelected: Boolean, onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(
                alpha = 0.5f
            )
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(12.dp),
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
