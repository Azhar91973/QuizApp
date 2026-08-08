package com.myQuizApp.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myQuizApp.data.QuizRepo
import com.myQuizApp.data.model.QuizQuestionModel
import com.myQuizApp.utils.Result
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

    private val _quizResponse = MutableStateFlow<Result<List<QuizQuestionModel>>>(Result.Loading)
    val quizResponse: StateFlow<Result<List<QuizQuestionModel>>> = _quizResponse.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _userSelections = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val userSelections: StateFlow<Map<Int, Int>> = _userSelections.asStateFlow()

    private val _streak = MutableStateFlow(0)
    val streak: StateFlow<Int> = _streak.asStateFlow()

    private val _bestStreak = MutableStateFlow(0)
    val bestStreak: StateFlow<Int> = _bestStreak.asStateFlow()

    private val _events = MutableSharedFlow<QuizEvent>()
    val events: SharedFlow<QuizEvent> = _events.asSharedFlow()

    private var autoAdvanceJob: Job? = null

    init {
        loadQuiz()
    }

    fun loadQuiz() {
        viewModelScope.launch {
            _quizResponse.value = Result.Loading
            _quizResponse.value = quizRepo.getQuiz()
        }
    }

    fun nextQuestion() {
        autoAdvanceJob?.cancel()
        val questions = (quizResponse.value as? Result.Success)?.data ?: return
        if (_currentIndex.value < (questions.size - 1)) {
            _currentIndex.value += 1
        } else {
            viewModelScope.launch { _events.emit(QuizEvent.FinishQuiz) }
        }
    }

    fun skipQuestion() {
        _streak.value = 0
        nextQuestion()
    }

    fun restartQuiz() {
        autoAdvanceJob?.cancel()
        _currentIndex.value = 0
        _userSelections.value = emptyMap()
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
        if (_userSelections.value.containsKey(questionIndex)) return

        val response = _quizResponse.value
        if (response is Result.Success) {
            val correctIndex = response.data[questionIndex].correctOptionIndex
            if (optionIndex == correctIndex) {
                _streak.value += 1
                if (_streak.value > _bestStreak.value) {
                    _bestStreak.value = _streak.value
                }
            } else {
                _streak.value = 0
            }
        }

        val currentSelections = _userSelections.value.toMutableMap()
        currentSelections[questionIndex] = optionIndex
        _userSelections.value = currentSelections

        // Auto-advance after 2 seconds
        autoAdvanceJob?.cancel()
        autoAdvanceJob = viewModelScope.launch {
            delay(2000)
            nextQuestion()
        }
    }
}