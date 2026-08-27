package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.MedicationAdministrationLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationAdministrationLogDao {
    @Query("SELECT * FROM medication_administration_logs WHERE patientId = :patientId ORDER BY administeredTimestamp DESC")
    fun getLogsForPatient(patientId: String): Flow<List<MedicationAdministrationLogEntity>>

    @Query("SELECT * FROM medication_administration_logs WHERE patientId = :patientId AND medicationId = :medicationId ORDER BY administeredTimestamp DESC")
    fun getLogsForMedication(patientId: String, medicationId: Long): Flow<List<MedicationAdministrationLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: MedicationAdministrationLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllLogs(logs: List<MedicationAdministrationLogEntity>)

    @Query("DELETE FROM medication_administration_logs WHERE id = :id")
    suspend fun deleteLogById(id: Long)

    @Query("DELETE FROM medication_administration_logs WHERE patientId = :patientId")
    suspend fun deleteAllLogsForPatient(patientId: String)
}
