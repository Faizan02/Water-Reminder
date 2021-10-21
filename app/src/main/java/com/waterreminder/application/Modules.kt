package com.waterreminder.application

import androidx.room.Room
import com.waterreminder.room.Simple
import com.waterreminder.room.WaterDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(androidContext(),WaterDatabase::class.java,"history")
            .fallbackToDestructiveMigration()
            .build()
    }
    single { get(WaterDatabase::class.java).reminderDao() }
    single { Simple(get()) }
}