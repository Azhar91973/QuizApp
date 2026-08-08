package com.myQuizApp.utils

import androidx.annotation.Keep

@Keep
sealed class Result<out T> {

    @Keep
    data class Success<T>(
        val data: T
    ) : Result<T>()

    @Keep
    data class Error(
        val message: String
    ) : Result<Nothing>()

    @Keep
    data object Loading : Result<Nothing>()
}
