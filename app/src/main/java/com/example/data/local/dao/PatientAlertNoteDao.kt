package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.PatientAlertNoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientAlertNoteDao {
    @Query("SELECT * FROM patient_alert_notes WHERE targetPatientId = :patientId OR targetPatientId = 'ALL' ORDER BY timestamp DESC")
    fun getAlertsForPatient(patientId: String): Flow<List<PatientAlertNoteEntity>>

    @Query("SELECT * FROM patient_alert_notes WHERE (targetPatientId = :patientId OR targetPatientId = 'ALL') AND isAcknowledged = 0 ORDER BY timestamp DESC")
    fun getUnacknowledgedAlertsForPatient(patientId: String): Flow<List<PatientAlertNoteEntity>>

    @Query("SELECT * FROM patient_alert_notes ORDER BY timestamp DESC")
    fun getAllAlerts(): Flow<List<PatientAlertNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: PatientAlertNoteEntity): Long

    @Query("UPDATE patient_alert_notes SET isAcknowledged = 1, acknowledgedAt = :timestamp WHERE id = :alertId")
    suspend fun acknowledgeAlert(alertId: Long, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM patient_alert_notes WHERE id = :alertId")
    suspend fun deleteAlert(alertId: Long)

    @Query("DELETE FROM patient_alert_notes")
    suspend fun deleteAllAlerts()
}
