package com.myQuizApp.data.remote.dto

import com.google.gson.annotations.SerializedName


data class QuestionsDto(
    @SerializedName("id") val id: Int,
    @SerializedName("question") val question: String,
    @SerializedName("options") val options: List<String>,
    @SerializedName("correctOptionIndex") val correctOptionIndex: Int
)