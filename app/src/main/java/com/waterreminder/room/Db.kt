package com.waterreminder.room

import android.content.Context
import androidx.room.Room

object Db {
    fun database(context: Context): WaterDatabase{
        return Room.databaseBuilder(context,WaterDatabase::class.java,"history").fallbackToDestructiveMigration().build()
    }
}
