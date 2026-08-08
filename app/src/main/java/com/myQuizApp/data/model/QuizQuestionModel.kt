package com.myQuizApp.data.model

import com.google.gson.annotations.SerializedName


data class QuizQuestionModel(
    @SerializedName("id") val id: Int? = -1,

    @SerializedName("question") val question: String? = "",

    @SerializedName("options") val options: List<String>? = emptyList(),

    @SerializedName("correctOptionIndex") val correctOptionIndex: Int? = -1
)
