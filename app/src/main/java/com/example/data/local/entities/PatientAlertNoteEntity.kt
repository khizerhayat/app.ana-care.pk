package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patient_alert_notes")
data class PatientAlertNoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val targetPatientId: String, // "ALL" or specific user ID e.g. "pat_eleanor_01"
    val targetPatientName: String = "All Patients",
    val senderId: String,
    val senderName: String,
    val senderRole: String, // "DOCTOR" or "ADMIN"
    val title: String,
    val message: String,
    val severity: String, // "URGENT", "WARNING", "MEDICATION_ALERT", "INFO"
    val timestamp: Long = System.currentTimeMillis(),
    val isAcknowledged: Boolean = false,
    val acknowledgedAt: Long? = null,
    val actionLink: String = "NONE" // "VITALS", "MEDICATIONS", "LABS", "APPOINTMENTS", "NONE"
)
