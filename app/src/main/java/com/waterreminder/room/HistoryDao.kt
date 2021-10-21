package com.waterreminder.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HistoryDao {
    // The flow always holds/caches latest version of data. Notifies its observers when the
    // data has changed.
    @Query("SELECT * FROM history")
    fun getAllHistory(): LiveData<List<History>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: History)

    @Query("DELETE FROM history")
    suspend fun deleteAll()

    @Query("SELECT * FROM history ORDER By id DESC LIMIT 1")
    suspend fun getLastHistory(): History?
}