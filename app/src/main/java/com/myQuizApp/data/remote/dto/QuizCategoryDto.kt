package com.myQuizApp.data.remote.dto

import com.google.gson.annotations.SerializedName


data class QuizCategoryDto(
    @SerializedName("id") val id: String,
    @SerializedName("description") val description: String,
    @SerializedName("questions_url") val questionsUrl: String,
    @SerializedName("title") val title: String
)