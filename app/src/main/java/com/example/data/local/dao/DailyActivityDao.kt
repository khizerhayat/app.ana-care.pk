package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.DailyActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyActivityDao {
    @Query("SELECT * FROM daily_activities WHERE patientId = :patientId ORDER BY timestamp DESC")
    fun getActivitiesForPatient(patientId: String): Flow<List<DailyActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: DailyActivityEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(activities: List<DailyActivityEntity>)

    @Query("DELETE FROM daily_activities WHERE id = :id")
    suspend fun deleteActivityById(id: Long)
}
