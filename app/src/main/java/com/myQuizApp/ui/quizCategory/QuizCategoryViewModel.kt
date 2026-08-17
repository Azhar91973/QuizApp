package com.myQuizApp.ui.quizCategory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myQuizApp.data.QuizRepo
import com.myQuizApp.domain.model.QuizCategory
import com.myQuizApp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizCategoryViewModel @Inject constructor(private val quizRepo: QuizRepo) : ViewModel() {

    private val _categoriesState = MutableStateFlow<Result<List<QuizCategory>>>(Result.Loading)
    val categoriesState: StateFlow<Result<List<QuizCategory>>> = _categoriesState.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            quizRepo.getQuizCategories().collect { categories ->
                if (categories.isEmpty()) {
                    _categoriesState.value = Result.Loading
                    val result = quizRepo.refreshQuizCategories()
                    if (result is Result.Error) {
                        _categoriesState.value = Result.Error(result.message)
                    }
                } else {
                    _categoriesState.value = Result.Success(categories)
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _categoriesState.value = Result.Loading
            val result = quizRepo.refreshQuizCategories()
            if (result is Result.Error) {
                _categoriesState.value = Result.Error(result.message)
            }
        }
    }
}
