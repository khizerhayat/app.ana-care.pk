package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medications")
data class MedicationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val patientId: String,
    val name: String,
    val dosage: String,
    val frequency: String, // "Once daily", "Twice daily", "Three times daily", "As needed"
    val route: String = "Oral", // "Oral", "Subcutaneous", "Topical", "Inhalation"
    val scheduledTime: String = "08:00 AM",
    val instructions: String = "",
    val prescribedBy: String = "",
    val startDate: Long = System.currentTimeMillis(),
    val startDateFormatted: String = "",
    val endDate: Long = 0L,
    val endDateFormatted: String = "",
    val isTakenToday: Boolean = false,
    val reminderEnabled: Boolean = true,
    val category: String = "General",
    val refillsRemaining: Int = 2,
    val status: String = "RUNNING", // "RUNNING" or "STOPPED"
    val lastAction: String = "", // "TAKEN", "SKIPPED", or ""
    val lastActionTimestamp: Long = 0L,
    val lastActionDateFormatted: String = ""
)

