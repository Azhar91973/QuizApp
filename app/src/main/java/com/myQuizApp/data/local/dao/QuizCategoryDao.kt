package com.myQuizApp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.myQuizApp.data.local.entity.QuizCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizCategoryDao {

    @Query("SELECT * FROM quizCategory")
    fun getCategories(): Flow<List<QuizCategoryEntity>>

    @Query("SELECT * FROM quizCategory WHERE :id = id")
    suspend fun getCategoryById(id: String): QuizCategoryEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(quizCategories: List<QuizCategoryEntity>)
}