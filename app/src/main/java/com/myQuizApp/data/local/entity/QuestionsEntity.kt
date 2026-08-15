package com.myQuizApp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "questions", foreignKeys = [ForeignKey(
        entity = QuizCategoryEntity::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.CASCADE
    )], indices = [Index("categoryId")]
)
data class QuestionsEntity(
    @PrimaryKey val id: String,
    val categoryId: Int,
    val question:String,
    val answeredIdx: Int,
    val longestStreak: Int,
    val correctOptionIndex: Int,
    val options: List<String>
)