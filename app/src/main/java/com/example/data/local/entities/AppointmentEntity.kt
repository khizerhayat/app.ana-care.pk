package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val patientId: String,
    val doctorName: String,
    val specialty: String,
    val appointmentType: String, // "HOME_VISIT", "VIDEO_CONSULTATION", "CLINIC_VISIT"
    val scheduledEpochMillis: Long,
    val timeSlotString: String = "10:00 AM",
    val durationMinutes: Int = 30,
    val status: String = "CONFIRMED", // "CONFIRMED", "COMPLETED", "CANCELLED", "PENDING"
    val reason: String = "",
    val locationOrLink: String = "",
    val doctorNotes: String = ""
)
