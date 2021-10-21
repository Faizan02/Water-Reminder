package com.waterreminder.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.*

/**
 * This is the backend. The database. This used to be done by the OpenHelper.
 * The fact that this has very few comments emphasizes its coolness.
 */
@TypeConverters(Converters::class)
@Database(entities = [History::class,Reminder::class], version = 5)
abstract class WaterDatabase: RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun reminderDao(): ReminderDao
}
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}

