package com.myQuizApp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quizCategory")
data class QuizCategoryEntity(
    @PrimaryKey val id: String,
    val currentScore: Int,
    val totalQuestions: Int,
    val title: String,
    val description: String,
    val questionUrl: String,
    val streak: Int,
    val longestStreak: Int,
    val attempted: Boolean
)