package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medication_administration_logs")
data class MedicationAdministrationLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val patientId: String,
    val medicationId: Long = 0L,
    val medicationName: String,
    val dosage: String,
    val administeredTimestamp: Long = System.currentTimeMillis(),
    val administeredDateFormatted: String, // e.g. "Aug 22, 2026, 08:30 AM"
    val status: String = "TAKEN", // "TAKEN", "MISSED", "GIVEN_BY_NURSE", "GIVEN_BY_CAREGIVER", "DELAYED"
    val administeredBy: String = "Self (Patient)", // "Self (Patient)", "Attending Nurse", "Primary Caregiver", "Dr. Sarah"
    val notes: String = ""
)
