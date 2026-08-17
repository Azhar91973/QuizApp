package com.myQuizApp.ui.question

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.myQuizApp.R
import com.myQuizApp.domain.model.Question
import com.myQuizApp.ui.theme.StreakOrange
import com.myQuizApp.ui.theme.SuccessGreen
import com.myQuizApp.utils.Result
import kotlinx.coroutines.flow.collectLatest

@Composable
fun QuizScreen(
    modifier: Modifier = Modifier,
    categoryId: String,
    questionUrl: String,
    viewModel: QuizViewModel,
    onFinished: () -> Unit,
) {
    LaunchedEffect(categoryId, questionUrl) {
        viewModel.initializeQuiz(categoryId, questionUrl)
    }

    val state by viewModel.questionsState.collectAsStateWithLifecycle()
    val streak by viewModel.streak.collectAsStateWithLifecycle()
    val currentIndex by viewModel.currentIndex.collectAsStateWithLifecycle()

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
        when (val result = state) {
            is Result.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is Result.Success -> {
                val questions = result.data
                if (questions.isNotEmpty()) {
                    val currentQuestion = questions[currentIndex]
                    QuizContent(
                        question = currentQuestion,
                        currentIndex = currentIndex,
                        totalQuestions = questions.size,
                        selectedOption = currentQuestion.answeredIdx,
                        streak = streak,
                        onOptionSelected = { optionIndex ->
                            viewModel.selectOption(optionIndex)
                        },
                        onNext = { viewModel.nextQuestion() },
                        onPrevious = { viewModel.previousQuestion() },
                        onSkip = { viewModel.skipQuestion() }
                    )
                } else {
                    Text(
                        stringResource(R.string.no_questions_available),
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            is Result.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text(
                            text = stringResource(R.string.error_message, result.message),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.initializeQuiz(categoryId, questionUrl) }) {
                            Text("Retry")
                        }
                    }
                }
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
        targetValue = if (isOnFire) StreakOrange else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
        label = "fireColor"
    )

    Surface(
        color = if (isOnFire) StreakOrange.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = null,
                tint = fireColor,
                modifier = Modifier
                    .size(16.dp)
                    .graphicsLayer(scaleX = scale, scaleY = scale)
            )
            Spacer(modifier = Modifier.width(4.dp))
            val streakText = if (isOnFire) "ON FIRE" else "STREAK"
            Text(
                text = "$streak $streakText",
                color = fireColor,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun QuizContent(
    question: Question,
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
            .padding(top = 12.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.List,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Question ${currentIndex + 1}/$totalQuestions",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            StreakBadge(streak = streak)
        }

        Spacer(modifier = Modifier.height(12.dp))

        LinearProgressIndicator(
            progress = { (currentIndex + 1).toFloat() / totalQuestions },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(6.dp)
                .clip(CircleShape),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = question.question ?: "",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            question.options?.forEachIndexed { index, option ->
                val isAnswered = selectedOption != -1
                val correctIndex = question.correctOptionIndex

                val optionStatus = when {
                    !isAnswered -> OptionStatus.Normal
                    index == correctIndex -> OptionStatus.Correct
                    index == selectedOption -> OptionStatus.Incorrect
                    else -> OptionStatus.Normal
                }

                OptionItem(
                    text = option,
                    index = index,
                    status = optionStatus,
                ) {
                    if (!isAnswered) onOptionSelected(index)
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // Reverted Footer: Matches design reference (Outlined Previous, Solid Next)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.1f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous Button: Grey outlined matching design
                OutlinedButton(
                    onClick = onPrevious,
                    enabled = currentIndex > 0,
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier.height(44.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBackIos, null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        stringResource(R.string.previous),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                val isLast = currentIndex == (totalQuestions - 1)
                val isAnswered = selectedOption != -1

                // Next / Finish Button: Solid Blue matching design
                Button(
                    onClick = if (isAnswered || currentIndex < (totalQuestions - 1)) onNext else onSkip,
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier.height(44.dp).widthIn(min = 120.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 24.dp)
                ) {
                    Text(
                        text = if (isLast) stringResource(R.string.finish) else if (isAnswered) stringResource(R.string.next) else stringResource(R.string.skip),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, null, modifier = Modifier.size(12.dp))
                }
            }
        }
    }
}

@Composable
fun OptionItem(
    text: String,
    index: Int,
    status: OptionStatus,
    onClick: () -> Unit
) {
    val letter = ('A' + index).toString()
    
    val backgroundColor = when (status) {
        OptionStatus.Normal -> MaterialTheme.colorScheme.surface
        OptionStatus.Correct -> SuccessGreen.copy(alpha = 0.08f)
        OptionStatus.Incorrect -> MaterialTheme.colorScheme.error.copy(alpha = 0.08f)
    }

    val borderColor = when (status) {
        OptionStatus.Normal -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        OptionStatus.Correct -> SuccessGreen.copy(alpha = 0.6f)
        OptionStatus.Incorrect -> MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
    }

    val iconBgColor = when (status) {
        OptionStatus.Normal -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        OptionStatus.Correct -> SuccessGreen
        OptionStatus.Incorrect -> MaterialTheme.colorScheme.error
    }

    val iconTintColor = when (status) {
        OptionStatus.Normal -> MaterialTheme.colorScheme.primary
        else -> Color.White
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = status == OptionStatus.Normal) { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor),
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                when (status) {
                    OptionStatus.Correct -> Icon(Icons.Default.Check, null, tint = iconTintColor, modifier = Modifier.size(16.dp))
                    OptionStatus.Incorrect -> Icon(Icons.Default.Close, null, tint = iconTintColor, modifier = Modifier.size(16.dp))
                    else -> Text(
                        text = letter,
                        color = iconTintColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = text,
                modifier = Modifier.weight(1f),
                color = if (status != OptionStatus.Normal) iconBgColor else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (status != OptionStatus.Normal) FontWeight.SemiBold else FontWeight.Medium
            )
        }
    }
}

enum class OptionStatus {
    Normal, Correct, Incorrect
}
