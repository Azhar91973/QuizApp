package com.myQuizApp.ui.question

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myQuizApp.data.QuizRepo
import com.myQuizApp.domain.model.Question
import com.myQuizApp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
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
    
    private val _questionsState = MutableStateFlow<Result<List<Question>>>(Result.Loading)
    val questionsState: StateFlow<Result<List<Question>>> = _questionsState.asStateFlow()

    private val _category = MutableStateFlow<com.myQuizApp.domain.model.QuizCategory?>(null)
    val category: StateFlow<com.myQuizApp.domain.model.QuizCategory?> = _category.asStateFlow()

    fun initializeQuiz(categoryId: String, questionUrl: String) {
        viewModelScope.launch {
            quizRepo.getQuestions(categoryId).collect { questions ->
                if (questions.isEmpty()) {
                    _questionsState.value = Result.Loading
                    val result = quizRepo.refreshQuestions(categoryId, questionUrl)
                    if (result is Result.Error) {
                        _questionsState.value = Result.Error(result.message)
                    }
                } else {
                    _questionsState.value = Result.Success(questions)
                }
            }
        }
        
        viewModelScope.launch {
            quizRepo.getQuizCategories().collect { categories ->
                val category = categories.find { it.id == categoryId }
                _category.value = category
                _bestStreak.value = category?.longestStreak ?: 0
            }
        }

        viewModelScope.launch {
            quizRepo.observerStreak(categoryId).collect { _streak.value = it }
        }
    }

    fun nextQuestion() {
        autoAdvanceJob?.cancel()
        val state = _questionsState.value
        if (state is Result.Success) {
            val questionsList = state.data
            if (_currentIndex.value < (questionsList.size - 1)) {
                _currentIndex.value += 1
            } else {
                viewModelScope.launch {
                    if (questionsList.isNotEmpty()) {
                        quizRepo.calculateScore(questionsList[0].categoryId)
                    }
                    _events.emit(QuizEvent.FinishQuiz)
                }
            }
        }
    }

    fun skipQuestion() {
        _streak.value = 0
        nextQuestion()
    }

    fun restartQuiz(categoryId: String) {
        autoAdvanceJob?.cancel()
        _currentIndex.value = 0
        _streak.value = 0
        viewModelScope.launch {
            quizRepo.resetQuiz(categoryId)
        }
    }

    fun previousQuestion() {
        autoAdvanceJob?.cancel()
        if (_currentIndex.value > 0) {
            _currentIndex.value -= 1
        }
    }

    fun selectOption(optionIndex: Int) {
        val state = _questionsState.value
        if (state is Result.Success) {
            val questionsList = state.data
            if (questionsList.isNotEmpty()) {
                val questionIndex = _currentIndex.value
                updateAnswer(questionsList[questionIndex], optionIndex)

                autoAdvanceJob?.cancel()
                autoAdvanceJob = viewModelScope.launch {
                    delay(2000)
                    nextQuestion()
                }
            }
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
