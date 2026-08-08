package com.myQuizApp.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myQuizApp.data.QuizRepo
import com.myQuizApp.data.model.QuizQuestionModel
import com.myQuizApp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
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
        if (_currentIndex.value < totalQuestions - 1) {
            _currentIndex.value += 1
        }
    }

    fun previousQuestion() {
        if (_currentIndex.value > 0) {
            _currentIndex.value -= 1
        }
    }

    fun selectOption(questionIndex: Int, optionIndex: Int) {
        val currentSelections = _userSelections.value.toMutableMap()
        currentSelections[questionIndex] = optionIndex
        _userSelections.value = currentSelections
    }
}