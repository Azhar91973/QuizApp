package com.myQuizApp.data

import com.myQuizApp.data.api.QuizApi
import com.myQuizApp.data.model.QuizQuestionModel
import com.myQuizApp.utils.Result
import javax.inject.Inject

class QuizRepo @Inject constructor(val quizApi: QuizApi) {

    suspend fun getQuiz(): Result<List<QuizQuestionModel>> {
        return try {
            val quizResponse = quizApi.getQuiz("53846277a8fcb034e482906ccc0d12b2")
            if (quizResponse.isSuccessful && quizResponse.body() != null) {
                Result.Success(quizResponse.body()!!)
            } else {
                Result.Error("No Quiz Available pls try again")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Something went wrong pls try again")
        }
    }
}