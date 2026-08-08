package com.myQuizApp.data

import android.content.Context
import com.myQuizApp.R
import com.myQuizApp.data.api.QuizApi
import com.myQuizApp.data.model.QuizQuestionModel
import com.myQuizApp.utils.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class QuizRepo @Inject constructor(
    val quizApi: QuizApi, @ApplicationContext private val context: Context
) {

    suspend fun getQuiz(): Result<List<QuizQuestionModel>> {
        return try {
            val quizResponse = quizApi.getQuiz("53846277a8fcb034e482906ccc0d12b2")
            if (quizResponse.isSuccessful && quizResponse.body() != null) {
                Result.Success(quizResponse.body()!!)
            } else {
                Result.Error(context.getString(R.string.no_quiz_available))
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: context.getString(R.string.something_went_wrong))
        }
    }
}
