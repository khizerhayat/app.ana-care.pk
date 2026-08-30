package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.AppConfigDao
import com.example.data.local.dao.AppointmentDao
import com.example.data.local.dao.AuditLogDao
import com.example.data.local.dao.DailyActivityDao
import com.example.data.local.dao.EncryptedMessageDao
import com.example.data.local.dao.LabResultDao
import com.example.data.local.dao.MedicalGalleryDao
import com.example.data.local.dao.MedicationAdministrationLogDao
import com.example.data.local.dao.MedicationDao
import com.example.data.local.dao.PatientAlertNoteDao
import com.example.data.local.dao.UserAccountDao
import com.example.data.local.dao.VitalSignDao
import com.example.data.local.entities.AppConfigEntity
import com.example.data.local.entities.AppointmentEntity
import com.example.data.local.entities.AuditLogEntity
import com.example.data.local.entities.DailyActivityEntity
import com.example.data.local.entities.EncryptedMessageEntity
import com.example.data.local.entities.LabResultEntity
import com.example.data.local.entities.MedicalGalleryEntity
import com.example.data.local.entities.MedicationAdministrationLogEntity
import com.example.data.local.entities.MedicationEntity
import com.example.data.local.entities.PatientAlertNoteEntity
import com.example.data.local.entities.UserAccountEntity
import com.example.data.local.entities.VitalSignEntity

@Database(
    entities = [
        UserAccountEntity::class,
        VitalSignEntity::class,
        MedicationEntity::class,
        MedicationAdministrationLogEntity::class,
        DailyActivityEntity::class,
        LabResultEntity::class,
        AppointmentEntity::class,
        EncryptedMessageEntity::class,
        PatientAlertNoteEntity::class,
        AppConfigEntity::class,
        MedicalGalleryEntity::class,
        AuditLogEntity::class
    ],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userAccountDao(): UserAccountDao
    abstract fun vitalSignDao(): VitalSignDao
    abstract fun medicationDao(): MedicationDao
    abstract fun medicationAdministrationLogDao(): MedicationAdministrationLogDao
    abstract fun dailyActivityDao(): DailyActivityDao
    abstract fun labResultDao(): LabResultDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun encryptedMessageDao(): EncryptedMessageDao
    abstract fun patientAlertNoteDao(): PatientAlertNoteDao
    abstract fun appConfigDao(): AppConfigDao
    abstract fun medicalGalleryDao(): MedicalGalleryDao
    abstract fun auditLogDao(): AuditLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ana_care_health_portal.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
