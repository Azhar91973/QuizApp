package com.myQuizApp.ui.quizCategory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myQuizApp.data.QuizRepo
import com.myQuizApp.domain.model.QuizCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizCategoryViewModel @Inject constructor(private val quizRepo: QuizRepo) : ViewModel() {
    val quizCategories: StateFlow<List<QuizCategory>> = quizRepo.getQuizCategories().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()
    )

    init {
        viewModelScope.launch {
            // Only refresh if we don't have categories in DB
            quizCategories.collect { 
                if (it.isEmpty()) {
                    refreshQuizCategory()
                }
            }
        }
    }

    private fun refreshQuizCategory() {
        viewModelScope.launch {
            quizRepo.refreshQuizCategories()
        }
    }
}
