package com.myQuizApp.data.api

import com.myQuizApp.data.model.QuizQuestionModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface QuizApi {

    @GET("dr-samrat/{id}/raw")
    suspend fun getQuiz(@Path("id") id: String): Response<List<QuizQuestionModel>>

}