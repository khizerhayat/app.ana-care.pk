package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medical_gallery")
data class MedicalGalleryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val patientId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val title: String,
    val category: String, // "Prescription / Rx", "Lab & Diagnostic Report", "Wound & Clinical Photo", "Physical Therapy / Mobility", "Diet & Meal Photo", "Insurance & ID Card", "General Record"
    val imageUri: String, // file path, content URI, or placeholder key
    val notes: String = "",
    val loggedByRole: String = "PATIENT", // "DOCTOR", "CAREGIVER", "PATIENT", "ADMIN"
    val loggedByName: String = "Patient",
    val formattedDate: String = ""
)
