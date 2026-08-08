package com.myQuizApp.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myQuizApp.data.QuizRepo
import com.myQuizApp.data.model.QuizQuestionModel
import com.myQuizApp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    init {
        loadQuiz()
    }

    fun loadQuiz() {
        viewModelScope.launch {
            _quizResponse.value = Result.Loading
            _quizResponse.value = quizRepo.getQuiz()
        }
    }

    fun nextQuestion(totalQuestions: Int) {
        if (_currentIndex.value < (totalQuestions - 1)) {
            _currentIndex.value += 1
        } else {
            // Signal quiz completion if needed or handled in UI
        }
    }

    fun restartQuiz() {
        _currentIndex.value = 0
        _userSelections.value = emptyMap()
        _streak.value = 0
    }

    fun previousQuestion() {
        if (_currentIndex.value > 0) {
            _currentIndex.value -= 1
        }
    }

    fun selectOption(questionIndex: Int, optionIndex: Int) {
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
    }
}