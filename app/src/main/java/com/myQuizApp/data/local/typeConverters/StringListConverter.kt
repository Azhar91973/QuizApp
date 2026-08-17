package com.myQuizApp.data.local.typeConverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class StringListConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return gson.toJson(value) // Converts List<String> to JSON String
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        if (value == null) return null

        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType) // Converts JSON String back to List<String>
    }
}
