package com.myQuizApp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.myQuizApp.data.local.dao.QuestionsDao
import com.myQuizApp.data.local.dao.QuizCategoryDao
import com.myQuizApp.data.local.entity.QuestionsEntity
import com.myQuizApp.data.local.entity.QuizCategoryEntity
import com.myQuizApp.data.local.typeConverters.StringListConverter

@Database(
    entities = [QuizCategoryEntity::class, QuestionsEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(StringListConverter::class)
abstract class QuizDatabase : RoomDatabase() {

    abstract fun getQuestionsDao(): QuestionsDao
    abstract fun getQuizCategoryDao(): QuizCategoryDao
}