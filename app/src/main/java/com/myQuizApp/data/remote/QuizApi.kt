package com.myQuizApp.data.remote

import com.myQuizApp.data.model.QuizQuestionModel
import com.myQuizApp.data.remote.dto.QuizCategoryDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface QuizApi {


    @GET
    suspend fun getQuiz(@Url url: String): Response<List<QuizQuestionModel>>


    @GET("dr-samrat/{id}/raw")
    suspend fun getQuizCategories(@Path("id") id: String): Response<List<QuizCategoryDto>>

}