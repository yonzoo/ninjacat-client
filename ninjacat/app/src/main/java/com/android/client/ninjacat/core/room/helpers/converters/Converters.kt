package com.android.client.ninjacat.core.room.helpers.converters

import androidx.room.TypeConverter
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter

object Converters {
    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @JvmStatic
    @TypeConverter
    fun toOffsetDateTime(value: String?): OffsetDateTime? {
        return value?.let {
            return formatter.parse(value, OffsetDateTime::from)
        }
    }

    @JvmStatic
    @TypeConverter
    fun fromOffsetDateTime(date: OffsetDateTime?): String? {
        val offset = OffsetDateTime.now().offset
        return date?.toInstant()?.atOffset(offset)?.format(formatter)
    }
}