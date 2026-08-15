package com.myQuizApp.domain.model

data class QuizCategory(
    val id: String,
    val currentScore: Int,
    val title: String,
    val description: String,
    val questionUrl: String,
    val attempted: Boolean
)