package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val formattedTimestamp: String = "",
    val userId: String,
    val userName: String,
    val userRole: String, // PATIENT, CAREGIVER, MEDICAL_PROFESSIONAL, ADMIN, SYSTEM
    val actionType: String, // USER_LOGIN, VITAL_ADDED, MEDICATION_ACTION, MEDICATION_ADDED, ACTIVITY_LOGGED, GALLERY_UPLOAD, GALLERY_DELETE, MESSAGE_SENT, ALERT_SENT, ALERT_ACKNOWLEDGED, CONFIG_UPDATED, USER_CREATED, USER_DELETED, REPORT_EXPORTED
    val category: String, // AUTH & SECURITY, CLINICAL VITALS, MEDICATIONS, DAILY ACTIVITIES, MEDICAL GALLERY, TELEHEALTH MESSAGING, CLINICAL ALERTS, SYSTEM CONFIG
    val description: String,
    val details: String = "",
    val severity: String = "INFO", // INFO, SUCCESS, WARNING, CRITICAL
    val ipAddress: String = "127.0.0.1 (Local Session)"
)
