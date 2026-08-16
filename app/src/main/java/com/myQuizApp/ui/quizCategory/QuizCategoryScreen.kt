package com.myQuizApp.ui.quizCategory

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.myQuizApp.BuildConfig

@Composable
fun QuizCategoryScreen(
    viewModel: QuizCategoryViewModel = hiltViewModel(),
    openQuizScreen: (categoryId: String, questionUrl: String) -> Unit
) {
    val quizCategories = viewModel.quizCategories.collectAsStateWithLifecycle()

    LazyColumn() {
        items(quizCategories.value) { category ->
            QuizCategoryCard(
                category.title, category.description, category.questionUrl, category.id
            ) { categoryId, questionUrl ->
                openQuizScreen(categoryId, questionUrl.parseUrl())
            }
        }

    }

}

fun String.parseUrl(): String {
    val baseUrl = BuildConfig.BASE_URL
    val url = this.removePrefix(baseUrl)
    Log.d("ParseUrl", "parseUrl: $url")
    return url
}

@Composable
fun QuizCategoryCard(
    title: String,
    description: String,
    questionsUrl: String,
    categoryId: String,
    open: (categoryId: String, questionUrl: String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
            .border(2.dp, shape = RectangleShape, color = Color.Red)
            .clickable(
                onClick = {
                    open(categoryId, questionsUrl)
                })
    ) {
        Text(text = title)
        Spacer(modifier = Modifier.size(3.dp))
        Text(text = description)
    }
}

@Preview
@Composable
private fun QuizCategoryCardPreview() {
    val categoryList = listOf<Pair<String, String>>(
        Pair("Android A", "Fundamentals A"), Pair("Android B", "Fundamentals B"), Pair(
            "Android C", "Fundamentals C"
        )
    )
    LazyColumn() {
        items(categoryList) { category ->
            QuizCategoryCard(
                category.first, category.second, categoryId = "", questionsUrl = ""
            ) { categoryId, questionUrl ->

            }

        }

    }
}