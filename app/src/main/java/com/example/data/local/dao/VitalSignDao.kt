package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.VitalSignEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VitalSignDao {
    @Query("SELECT * FROM vital_signs WHERE patientId = :patientId ORDER BY timestamp DESC")
    fun getVitalsForPatient(patientId: String): Flow<List<VitalSignEntity>>

    @Query("SELECT * FROM vital_signs ORDER BY timestamp DESC")
    fun getAllVitals(): Flow<List<VitalSignEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVital(vital: VitalSignEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vitals: List<VitalSignEntity>)

    @Query("DELETE FROM vital_signs WHERE id = :id")
    suspend fun deleteVitalById(id: Long)
}
