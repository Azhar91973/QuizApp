package com.myQuizApp.data

import android.content.Context
import android.util.Log
import com.myQuizApp.R
import com.myQuizApp.data.local.dao.QuestionsDao
import com.myQuizApp.data.local.dao.QuizCategoryDao
import com.myQuizApp.data.local.entity.QuestionsEntity
import com.myQuizApp.data.local.entity.QuizCategoryEntity
import com.myQuizApp.data.remote.QuizApi
import com.myQuizApp.data.remote.dto.QuestionsDto
import com.myQuizApp.data.remote.dto.QuizCategoryDto
import com.myQuizApp.domain.model.Question
import com.myQuizApp.domain.model.QuizCategory
import com.myQuizApp.utils.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.roundToInt

class QuizRepo @Inject constructor(
    private val quizApi: QuizApi,
    private val quizCategoryDao: QuizCategoryDao,
    private val questionsDao: QuestionsDao,
    @ApplicationContext private val context: Context
) {
    fun getQuestions(categoryId: String): Flow<List<Question>> {
        return questionsDao.getQuestionsByCategoryId(categoryId).map { entries ->
            entries.map { entity ->
                entity.toDomain()
            }
        }
    }

    fun observerStreak(categoryId: String): Flow<Int> {
        return quizCategoryDao.observeStreak(categoryId)
    }

    suspend fun updateAnswer(
        questionId: Int, categoryId: String, answeredIdx: Int
    ) {
        questionsDao.updateAnswer(
            questionId = questionId, categoryId = categoryId, answeredIdx = answeredIdx
        )
    }

    suspend fun getCurrentScore(categoryId: String): Int {
        return quizCategoryDao.getScore(categoryId)
    }

    suspend fun getStreak(categoryId: String): Int {
        return quizCategoryDao.getStreak(categoryId)
    }

    suspend fun resetStreak(categoryId: String) {
        quizCategoryDao.resetStreak(categoryId)
    }

    suspend fun resetQuiz(categoryId: String) {
        questionsDao.resetAnswers(categoryId)
        quizCategoryDao.updateScore(categoryId, 0)
        quizCategoryDao.resetStreak(categoryId)
    }

    suspend fun updateStreak(
        categoryId: String
    ) {
        val currentStreak = getStreak(categoryId)
        val newStreak = currentStreak + 1
        val longestStreak = quizCategoryDao.getLongestStreak(categoryId)
        quizCategoryDao.updateStreak(
            categoryId = categoryId, streak = newStreak, max(newStreak, longestStreak)
        )
    }

    suspend fun calculateScore(
        categoryId: String
    ) {
        val correctAnswers = questionsDao.getCorrectAnswers(categoryId)
        val totalQuestion = questionsDao.getTotalQuestions(categoryId)

        if (totalQuestion == 0) return

        val score = ((correctAnswers.toDouble() / totalQuestion) * 10).roundToInt()

        val currentBest = quizCategoryDao.getScore(categoryId)
        if (score > currentBest) {
            updateScore(categoryId, score)
        } else {
            // Even if not a high score, mark as attempted
            quizCategoryDao.insertCategories(
                listOf(
                    quizCategoryDao.getCategoryById(categoryId).copy(attempted = true)
                )
            )
        }
    }

    suspend fun updateScore(
        categoryId: String, score: Int
    ) {
        quizCategoryDao.updateScore(categoryId, score)
    }

    suspend fun refreshQuestions(
        categoryId: String, questionUrl: String
    ): Result<Unit> {
        Log.d("DebugQuestions", "refreshQuestions: $categoryId")
        return try {
            val quizResponse = quizApi.getQuestions(questionUrl)
            if (quizResponse.isSuccessful) {
                val questions = quizResponse.body() ?: return Result.Error("Empty response")
                val existingQuestions = questionsDao.getQuestionsByCategory(categoryId)
                Log.d("QuizRepo", "refreshQuestions: $categoryId $existingQuestions")
                val entities = questions.map { dto ->
                    dto.toEntity(
                        categoryId = categoryId,
                        existing = if (existingQuestions.isNotEmpty()) existingQuestions.getOrNull(
                            dto.id - 1
                        ) else null
                    )
                }
                questionsDao.insertQuestions(entities)
                quizCategoryDao.updateTotalQuestions(categoryId, questions.size)
                Result.Success(Unit)
            } else {
                Result.Error("Failed to fetch questions pls retry")
            }
        } catch (e: Exception) {
            Log.d("QuizRepo", "refreshQuestions: ${e.message}")
            Result.Error(context.getString(R.string.something_went_wrong))
        }
    }

    fun getQuizCategories(): Flow<List<QuizCategory>> {
        return quizCategoryDao.getCategories().map { entries ->
            entries.map { entity ->
                entity.toDomain()
            }
        }
    }

    suspend fun refreshQuizCategories(): Result<Unit> {
        return try {
            val quizCategoriesResponse =
                quizApi.getQuizCategories("ee986f16da9d8303c1acfd364ece22c5")
            if (quizCategoriesResponse.isSuccessful && quizCategoriesResponse.body() != null) {
                val existingQuizCategory =
                    quizCategoryDao.getExistingCategories().associateBy { it.id }
                Log.d("QuizRepo", "refreshQuizCategories: $existingQuizCategory")
                quizCategoryDao.insertCategories(
                    quizCategoriesResponse.body()!!.map { quizCategory ->
                        quizCategory.toEntity(existingQuizCategory[quizCategory.id])
                    })
                Result.Success(Unit)
            } else {
                Result.Error("Failed to fetch categories pls retry ")
            }
        } catch (e: Exception) {
            Log.d("QuizRepo", "refreshQuizCategories: ${e.message}")
            Result.Error(context.getString(R.string.something_went_wrong))
        }
    }

    fun QuestionsDto.toEntity(
        categoryId: String, existing: QuestionsEntity?
    ): QuestionsEntity {
        return QuestionsEntity(
            id = id,
            categoryId = categoryId,
            question = question,
            answeredIdx = existing?.answeredIdx ?: -1,
            correctOptionIndex = correctOptionIndex,
            options = options
        )
    }

    fun QuestionsEntity.toDomain(
    ): Question {
        return Question(
            id = id,
            categoryId = categoryId,
            question = question,
            answeredIdx = answeredIdx,
            correctOptionIndex = correctOptionIndex,
            options = options
        )
    }

    fun QuizCategoryDto.toEntity(
        existing: QuizCategoryEntity?
    ): QuizCategoryEntity {
        return QuizCategoryEntity(
            id = id,
            title = title,
            description = description,
            questionUrl = questionsUrl,
            currentScore = existing?.currentScore ?: 0,
            totalQuestions = existing?.totalQuestions ?: 0,
            streak = existing?.streak ?: 0,
            longestStreak = existing?.longestStreak ?: 0,
            attempted = existing?.attempted ?: false
        )
    }

    fun QuizCategoryEntity.toDomain(
    ): QuizCategory {
        return QuizCategory(
            id = id,
            title = title,
            description = description,
            questionUrl = questionUrl,
            currentScore = currentScore,
            totalQuestions = totalQuestions,
            attempted = attempted,
            longestStreak = longestStreak
        )
    }
}
