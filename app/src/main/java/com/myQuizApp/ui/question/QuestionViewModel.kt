package com.myQuizApp.ui.question

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myQuizApp.data.QuizRepo
import com.myQuizApp.domain.model.Question
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class QuizEvent {
    object FinishQuiz : QuizEvent()
}

@HiltViewModel
class QuizViewModel @Inject constructor(private val quizRepo: QuizRepo) : ViewModel() {

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _streak = MutableStateFlow(0)
    val streak: StateFlow<Int> = _streak.asStateFlow()

    private val _bestStreak = MutableStateFlow(0)
    val bestStreak: StateFlow<Int> = _bestStreak.asStateFlow()

    private val _events = MutableSharedFlow<QuizEvent>()
    val events: SharedFlow<QuizEvent> = _events.asSharedFlow()
    private var autoAdvanceJob: Job? = null
    private val _questions = MutableStateFlow<List<Question>>(
        emptyList()
    )

    val questions: StateFlow<List<Question>> = _questions.asStateFlow()

    fun loadStreak(categoryId: String) {
        viewModelScope.launch {
            quizRepo.observerStreak(categoryId).collect { streak ->
                _streak.value = streak
            }
        }
    }

    fun loadQuestions(categoryId: String) {

        viewModelScope.launch {
            quizRepo.getQuestions(categoryId).collect { result ->
                _questions.value = result
            }
        }
    }

    fun refreshQuiz(categoryId: String, questionUrl: String) {
        viewModelScope.launch {
            quizRepo.refreshQuestions(categoryId, questionUrl)
        }
    }

    fun nextQuestion() {
        autoAdvanceJob?.cancel()
        val questions = questions.value
        if (_currentIndex.value < (questions.size - 1)) {
            _currentIndex.value += 1
        } else {
            viewModelScope.launch {
                quizRepo.calculateScore(questions[questions.size - 1].categoryId)
                _events.emit(QuizEvent.FinishQuiz)
            }
        }
    }

    fun skipQuestion() {
        _streak.value = 0
        nextQuestion()
    }

    fun restartQuiz() {
        autoAdvanceJob?.cancel()
        _currentIndex.value = 0
        _streak.value = 0
        _bestStreak.value = 0
    }

    fun previousQuestion() {
        autoAdvanceJob?.cancel()
        if (_currentIndex.value > 0) {
            _currentIndex.value -= 1
        }
    }

    fun selectOption(optionIndex: Int) {
        val questionIndex = _currentIndex.value
        updateAnswer(questions.value[questionIndex], optionIndex)

        // Auto-advance after 2 seconds
        autoAdvanceJob?.cancel()
        autoAdvanceJob = viewModelScope.launch {
            delay(2000)
            nextQuestion()
        }
    }

    private fun resetStreak(categoryId: String) {
        viewModelScope.launch {
            quizRepo.resetStreak(categoryId)
        }
    }

    private fun updateAnswer(question: Question, answeredIdx: Int) {
        viewModelScope.launch {
            quizRepo.updateAnswer(question.id, question.categoryId, answeredIdx)
            if (question.correctOptionIndex == answeredIdx) quizRepo.updateStreak(question.categoryId)
            else resetStreak(question.categoryId)
        }

    }
}