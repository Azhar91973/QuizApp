package com.myQuizApp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.myQuizApp.data.local.entity.QuestionsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionsDao {

    @Query("SELECT * FROM questions WHERE categoryId = :categoryId")
    fun getQuestionsByCategoryId(categoryId: String): Flow<List<QuestionsEntity>>

    @Query(
        """
    SELECT COUNT(*)
    FROM questions
    WHERE categoryId = :categoryId
    AND answeredIdx = correctOptionIndex
"""
    )
    suspend fun getCorrectAnswers(categoryId: String): Int

    @Query(
        """
    SELECT COUNT(*)
    FROM questions
    WHERE categoryId = :categoryId
"""
    )
    suspend fun getTotalQuestions(categoryId: String): Int

    @Query(
        """
        UPDATE questions
        SET answeredIdx = :answeredIdx
        WHERE id = :questionId
        AND categoryId = :categoryId
    """
    )
    suspend fun updateAnswer(
        questionId: Int, categoryId: String, answeredIdx: Int
    )

    @Query("SELECT * FROM questions WHERE categoryId = :categoryId")
    suspend fun getQuestionsByCategory(
        categoryId: String
    ): List<QuestionsEntity>

    @Query("SELECT * FROM questions WHERE :id = id")
    suspend fun getQuestionById(id: Int): QuestionsEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionsEntity>)

    @Query("UPDATE questions SET answeredIdx = -1 WHERE categoryId = :categoryId")
    suspend fun resetAnswers(categoryId: String)
}