package com.waterreminder.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ReminderDao {
    // The flow always holds/caches latest version of data. Notifies its observers when the
    // data has changed.
    @Query("SELECT * FROM reminder")
    fun getAllHistory(): LiveData<List<Reminder>>

    @Query("SELECT * FROM reminder")
    fun getAllHistoryOnce(): List<Reminder>



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: Reminder)

    @Query("DELETE FROM reminder")
    suspend fun deleteAll()
}