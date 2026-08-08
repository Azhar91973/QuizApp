package com.myQuizApp.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myQuizApp.data.QuizRepo
import com.myQuizApp.data.model.QuizQuestionModel
import com.myQuizApp.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(private val quizRepo: QuizRepo) : ViewModel() {

    private val _quizResponse = MutableStateFlow<Result<List<QuizQuestionModel>>>(Result.Loading)
    val quizResponse: StateFlow<Result<List<QuizQuestionModel>>> = _quizResponse.asStateFlow()

    fun loadQuiz() {
        viewModelScope.launch {
            _quizResponse.value = Result.Loading
            _quizResponse.value = quizRepo.getQuiz()
        }
    }
}