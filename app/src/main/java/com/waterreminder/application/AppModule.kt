package com.waterreminder.application

import android.content.Context
import androidx.room.Room
import com.waterreminder.room.HistoryDao
import com.waterreminder.room.ReminderDao
import com.waterreminder.room.WaterDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext context: Context): WaterDatabase {
        return Room.databaseBuilder(
            context,
            WaterDatabase::class.java,
            "water_reminder.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun providesHistoryDao(database: WaterDatabase): HistoryDao{
        return database.historyDao()
    }

    @Provides
    @Singleton
    fun providesReminderDao(database: WaterDatabase): ReminderDao{
        return database.reminderDao()
    }
}