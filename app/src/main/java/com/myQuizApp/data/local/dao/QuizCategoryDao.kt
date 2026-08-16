package com.myQuizApp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.myQuizApp.data.local.entity.QuizCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizCategoryDao {

    @Query("SELECT * FROM quizCategory")
    fun getCategories(): Flow<List<QuizCategoryEntity>>

    @Query("SELECT * FROM quizCategory")
    suspend fun getExistingCategories(): List<QuizCategoryEntity>

    @Query("""
    UPDATE quizCategory
    SET streak = :streak,
        longestStreak = :longestStreak
    WHERE id = :categoryId
""")
    suspend fun updateStreak(
        categoryId: String,
        streak: Int,
        longestStreak: Int
    )

    @Query(
        """
        UPDATE quizCategory
        SET totalQuestions = :totalQuestions
        WHERE id = :categoryId
    """
    )
    suspend fun updateTotalQuestions(
        categoryId: String, totalQuestions: Int
    )

    @Query(
        """
        UPDATE quizCategory
        SET currentScore= :score,
            attempted = 1
        WHERE id = :categoryId
    """
    )
    suspend fun updateScore(
        categoryId: String, score: Int
    )

    @Query(
        """
        UPDATE quizCategory
        SET streak= 0
        WHERE id = :categoryId
    """
    )
    suspend fun resetStreak(
        categoryId: String
    )

    @Query("SELECT * FROM quizCategory WHERE :id = id")
    suspend fun getCategoryById(id: String): QuizCategoryEntity

    @Query(
        """
    SELECT currentScore
    FROM quizCategory
    WHERE id = :categoryId
"""
    )
    suspend fun getScore(categoryId: String): Int

    @Query(
        """
    SELECT streak
    FROM quizCategory
    WHERE id = :categoryId
"""
    )
    suspend fun getStreak(categoryId: String): Int

    @Query(
        """
    SELECT longestStreak
    FROM quizCategory
    WHERE id = :categoryId
"""
    )
    suspend fun getLongestStreak(categoryId: String): Int

    @Query("""
    SELECT streak
    FROM quizCategory
    WHERE id = :categoryId
""")
    fun observeStreak(categoryId: String): Flow<Int>

    @Upsert
    suspend fun insertCategories(quizCategories: List<QuizCategoryEntity>)
}