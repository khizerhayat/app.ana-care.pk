package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.MedicalGalleryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicalGalleryDao {
    @Query("SELECT * FROM medical_gallery WHERE patientId = :patientId ORDER BY timestamp DESC")
    fun getGalleryForPatient(patientId: String): Flow<List<MedicalGalleryEntity>>

    @Query("SELECT * FROM medical_gallery ORDER BY timestamp DESC")
    fun getAllGallery(): Flow<List<MedicalGalleryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(item: MedicalGalleryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<MedicalGalleryEntity>)

    @Query("DELETE FROM medical_gallery WHERE id = :id")
    suspend fun deleteImageById(id: Long)

    @Update
    suspend fun updateImage(item: MedicalGalleryEntity)
}
