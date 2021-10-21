package com.waterreminder.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

    @Entity(tableName = "history")
    data class History(
            @ColumnInfo(name = "total") val total: Int,
            @ColumnInfo(name = "totalDrank") val totalDrank: Double,
            @ColumnInfo(name = "percentage") val percentage: String,
            @ColumnInfo(name = "unit") val unit: String,
            @ColumnInfo(name = "date") val date: Long
    ){
        @PrimaryKey(autoGenerate = true) var id: Int = 0
    }

    @Entity(tableName = "reminder")
    data class Reminder(
            @ColumnInfo(name = "triggerDate") val triggerDate: Long,
            @ColumnInfo(name = "title") val title: String,
            @ColumnInfo(name = "isOn") val isOn:Boolean,
            @ColumnInfo(name = "isAdded") val isAdded: Boolean
    ){
        @PrimaryKey(autoGenerate = true) var id: Int = 0
    }

