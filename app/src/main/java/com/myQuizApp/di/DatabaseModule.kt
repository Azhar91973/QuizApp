package com.myQuizApp.di

import android.content.Context
import androidx.room.Room
import com.myQuizApp.data.local.QuizDatabase
import com.myQuizApp.data.local.dao.QuestionsDao
import com.myQuizApp.data.local.dao.QuizCategoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {


    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): QuizDatabase {
        return Room.databaseBuilder(context, QuizDatabase::class.java, "quiz_database").build()
    }

    @Provides
    @Singleton
    fun provideQuestionsDao(quizDatabase: QuizDatabase): QuestionsDao {
        return quizDatabase.getQuestionsDao()
    }

    @Provides
    @Singleton
    fun provideQuizCategoryDao(quizDatabase: QuizDatabase): QuizCategoryDao {
        return quizDatabase.getQuizCategoryDao()
    }


}