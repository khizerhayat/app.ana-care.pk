package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.LabResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LabResultDao {
    @Query("SELECT * FROM lab_results WHERE patientId = :patientId ORDER BY datePerformed DESC")
    fun getLabResultsForPatient(patientId: String): Flow<List<LabResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLabResult(result: LabResultEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(results: List<LabResultEntity>)

    @Query("DELETE FROM lab_results WHERE id = :id")
    suspend fun deleteLabResultById(id: Long)
}
