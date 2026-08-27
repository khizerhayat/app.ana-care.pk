package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vital_signs")
data class VitalSignEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val patientId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val systolicBp: Int,
    val diastolicBp: Int,
    val heartRate: Int,
    val oxygenSaturation: Int, // SpO2 percentage
    val temperatureF: Float,
    val bloodGlucose: Int, // mg/dL
    val respiratoryRate: Int, // breaths per min
    val weightLbs: Float,
    val notes: String = "",
    val status: String = "NORMAL", // "NORMAL", "ELEVATED", "CRITICAL"
    val measuredBy: String = "Self",
    val encryptionHash: String = ""
)
