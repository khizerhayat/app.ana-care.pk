package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.MedicationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {
    @Query("SELECT * FROM medications WHERE patientId = :patientId ORDER BY status ASC, name ASC")
    fun getMedicationsForPatient(patientId: String): Flow<List<MedicationEntity>>

    @Query("SELECT * FROM medications WHERE patientId = :patientId AND status = 'RUNNING' ORDER BY scheduledTime ASC, name ASC")
    fun getRunningMedicationsForPatient(patientId: String): Flow<List<MedicationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(medication: MedicationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(medications: List<MedicationEntity>)

    @Update
    suspend fun updateMedication(medication: MedicationEntity)

    @Query("UPDATE medications SET status = :status WHERE id = :id")
    suspend fun updateMedicationStatus(id: Long, status: String)

    @Query("UPDATE medications SET isTakenToday = :taken, lastAction = :action, lastActionTimestamp = :timestamp, lastActionDateFormatted = :formattedDate WHERE id = :id")
    suspend fun recordMedicationAction(id: Long, taken: Boolean, action: String, timestamp: Long, formattedDate: String)

    @Query("UPDATE medications SET isTakenToday = :taken WHERE id = :id")
    suspend fun setMedicationTaken(id: Long, taken: Boolean)

    @Query("DELETE FROM medications WHERE id = :id")
    suspend fun deleteMedicationById(id: Long)
}

