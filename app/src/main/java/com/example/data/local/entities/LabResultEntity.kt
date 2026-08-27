package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lab_results")
data class LabResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val patientId: String,
    val testName: String, // "Comprehensive Metabolic Panel", "Complete Blood Count (CBC)", "Lipid Panel", "HbA1c Glycated Hemoglobin"
    val category: String = "Blood Chemistry",
    val datePerformed: Long = System.currentTimeMillis(),
    val orderedBy: String = "Dr. Sarah Jenkins, MD",
    val facility: String = "ANA Care Diagnostic Laboratory",
    val status: String = "NORMAL", // "NORMAL", "ELEVATED", "PENDING_REVIEW"
    val summary: String = "",
    val keyParameters: String = "", // Formatted text / lines of lab values
    val doctorNotes: String = "",
    val isEncrypted: Boolean = true,
    val documentRef: String = ""
)
