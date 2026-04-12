package com.njit.medtrack.data

import androidx.room.TypeConverter
import java.util.Date

class Converters {

    @TypeConverter
    fun dateToMs(date: Date?): Long? = date?.time

    @TypeConverter
    fun msToDate(ms: Long?): Date? = ms?.let { Date(it) }

    @TypeConverter
    fun tagsToString(tags: List<String>): String = tags.joinToString("|")

    @TypeConverter
    fun stringToTags(value: String): List<String> =
        if (value.isEmpty()) emptyList() else value.split("|")
}
