package com.myQuizApp.data

import android.content.Context
import android.util.Log
import com.myQuizApp.R
import com.myQuizApp.data.local.dao.QuestionsDao
import com.myQuizApp.data.local.dao.QuizCategoryDao
import com.myQuizApp.data.local.entity.QuizCategoryEntity
import com.myQuizApp.data.model.QuizQuestionModel
import com.myQuizApp.data.remote.QuizApi
import com.myQuizApp.data.remote.dto.QuizCategoryDto
import com.myQuizApp.domain.model.QuizCategory
import com.myQuizApp.utils.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class QuizRepo @Inject constructor(
    private val quizApi: QuizApi,
    private val quizCategoryDao: QuizCategoryDao,
    private val questionsDao: QuestionsDao,
    @ApplicationContext private val context: Context
) {

    suspend fun getQuiz(questionUrl: String): Result<List<QuizQuestionModel>> {
        return try {
            val quizResponse = quizApi.getQuiz(questionUrl)
            if (quizResponse.isSuccessful && quizResponse.body() != null) {
                Result.Success(quizResponse.body()!!)
            } else {
                Result.Error(context.getString(R.string.no_quiz_available))
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: context.getString(R.string.something_went_wrong))
        }
    }

    fun getQuizCategories(): Flow<List<QuizCategory>> {
        return quizCategoryDao.getCategories().map { entries ->
            entries.map { entity ->
                entity.toDomain()
            }
        }
    }

    suspend fun refreshQuizCategories() {
        try {
            val quizCategoriesResponse =
                quizApi.getQuizCategories("ee986f16da9d8303c1acfd364ece22c5")
            if (quizCategoriesResponse.isSuccessful && quizCategoriesResponse.body() != null) {
                quizCategoryDao.insertCategories(
                    quizCategoriesResponse.body()!!.map { quizCategory ->
                        val existing = quizCategoryDao.getCategoryById(quizCategory.id)
                        quizCategory.toEntity(existing)
                    })
            }
        } catch (e: Exception) {
            Log.d("QuizRepo", "refreshQuizCategories: ${e.message}")
        }
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
            attempted = attempted
        )
    }
}