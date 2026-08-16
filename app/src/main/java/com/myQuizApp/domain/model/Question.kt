package com.myQuizApp.domain.model

data class Question(
    val id: Int,
    val categoryId: String,
    val question: String,
    val answeredIdx: Int,
    val correctOptionIndex: Int,
    val options: List<String>
)