package com.myQuizApp.utils

import android.util.Log
import com.myQuizApp.BuildConfig

fun String.parseUrl(): String {
    val baseUrl = BuildConfig.BASE_URL
    val url = this.removePrefix(baseUrl)
    Log.d("ParseUrl", "parsedUrl: $url")
    return url
}
