package com.myQuizApp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "questions", primaryKeys = ["id", "categoryId"], foreignKeys = [ForeignKey(
        entity = QuizCategoryEntity::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.CASCADE
    )], indices = [Index("categoryId")]
)
data class QuestionsEntity(
    val id: Int,
    val categoryId: String,
    val question: String,
    val answeredIdx: Int,
    val correctOptionIndex: Int,
    val options: List<String>
)