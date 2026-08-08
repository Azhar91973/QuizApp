package com.myQuizApp.ui.quiz

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.myQuizApp.R
import com.myQuizApp.data.model.QuizQuestionModel
import com.myQuizApp.ui.theme.SuccessGreen
import com.myQuizApp.utils.Result
import kotlinx.coroutines.flow.collectLatest

@Composable
fun QuizScreen(
    modifier: Modifier = Modifier,
    viewModel: QuizViewModel,
    onFinished: () -> Unit,
) {
    val quizResult by viewModel.quizResponse.collectAsStateWithLifecycle()
    val currentIndex by viewModel.currentIndex.collectAsStateWithLifecycle()
    val userSelections by viewModel.userSelections.collectAsStateWithLifecycle()
    val streak by viewModel.streak.collectAsStateWithLifecycle()

    // Handle ViewModel events
    LaunchedEffect(viewModel.events) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is QuizEvent.FinishQuiz -> onFinished()
            }
        }
    }

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
                        streak = streak,
                        onOptionSelected = { optionIndex ->
                            viewModel.selectOption(optionIndex)
                        },
                        onNext = { viewModel.nextQuestion() },
                        onPrevious = { viewModel.previousQuestion() },
                        onSkip = { viewModel.skipQuestion() })
                } else {
                    Text(
                        stringResource(R.string.no_questions_available),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            is Result.Error -> {
                Text(
                    text = stringResource(R.string.error_message, result.message),
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun StreakBadge(streak: Int) {
    val isOnFire = streak >= 3
    val infiniteTransition = rememberInfiniteTransition(label = "streak")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isOnFire) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing), repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val fireColor by animateColorAsState(
        targetValue = if (isOnFire) Color(0xFFFF9800) else Color.Gray, label = "fireColor"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .background(
                color = if (isOnFire) Color(0xFFFF9800).copy(alpha = 0.1f) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.LocalFireDepartment,
            contentDescription = stringResource(R.string.streak_content_description),
            tint = fireColor,
            modifier = Modifier
                .size(if (isOnFire) 24.dp else 20.dp)
                .graphicsLayer(scaleX = scale, scaleY = scale)
        )
        if (streak > 0) {
            Text(
                text = streak.toString(),
                color = fireColor,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        if (isOnFire) {
            Text(
                text = stringResource(R.string.on_fire),
                color = Color(0xFFFF9800),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
fun QuizContent(
    question: QuizQuestionModel,
    currentIndex: Int,
    totalQuestions: Int,
    selectedOption: Int?,
    streak: Int,
    onOptionSelected: (Int) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSkip: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.question_progress, currentIndex + 1, totalQuestions),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )

            StreakBadge(streak = streak)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = question.question ?: "",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            question.options?.forEachIndexed { index, option ->
                val isAnswered = selectedOption != null
                val correctIndex = question.correctOptionIndex

                val optionStatus = when {
                    !isAnswered -> OptionStatus.Normal
                    index == correctIndex -> OptionStatus.Correct
                    index == selectedOption -> OptionStatus.Incorrect
                    else -> OptionStatus.Normal
                }

                OptionItem(
                    text = option,
                    status = optionStatus,
                ) {
                    if (!isAnswered) onOptionSelected(index)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous Button: Low priority
            TextButton(
                onClick = onPrevious, enabled = currentIndex > 0, modifier = Modifier.height(48.dp)
            ) {
                Text(stringResource(R.string.previous), style = MaterialTheme.typography.labelLarge)
            }

            // Dynamic Right Button: Skip -> Next -> Finish
            val isLast = currentIndex == (totalQuestions - 1)

            if (selectedOption == null) {
                // User hasn't answered yet: Show Skip
                OutlinedButton(
                    onClick = onSkip,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(44.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp)
                ) {
                    Text(
                        if (isLast) stringResource(R.string.finish) else stringResource(R.string.skip),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            } else {
                // User has answered: Show Next (to bypass the 2s timer)
                Button(
                    onClick = onNext,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(44.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp)
                ) {
                    Text(
                        if (isLast) stringResource(R.string.finish) else stringResource(R.string.next),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

enum class OptionStatus {
    Normal, Correct, Incorrect
}

@Composable
fun OptionItem(
    text: String, status: OptionStatus, onClick: () -> Unit
) {
    val backgroundColor = when (status) {
        OptionStatus.Normal -> MaterialTheme.colorScheme.surface
        OptionStatus.Correct -> SuccessGreen.copy(alpha = 0.1f)
        OptionStatus.Incorrect -> MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
    }

    val borderColor = when (status) {
        OptionStatus.Normal -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        OptionStatus.Correct -> SuccessGreen
        OptionStatus.Incorrect -> MaterialTheme.colorScheme.error
    }

    val contentColor = when (status) {
        OptionStatus.Normal -> MaterialTheme.colorScheme.onSurface
        OptionStatus.Correct -> SuccessGreen
        OptionStatus.Incorrect -> MaterialTheme.colorScheme.error
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(12.dp),
            color = contentColor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (status != OptionStatus.Normal) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
