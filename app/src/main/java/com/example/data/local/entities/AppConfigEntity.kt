package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_configurations")
data class AppConfigEntity(
    @PrimaryKey
    val configKey: String = "SYSTEM_CONFIG",
    
    // Patient Dashboard Layout Controls (Configured by Admin)
    val showVitalsSummary: Boolean = true,
    val showMedicationSection: Boolean = true,
    val showDailyActivities: Boolean = true,
    val showLabResults: Boolean = true,
    val showEmergencyBanner: Boolean = true,
    val patientLayoutDensity: String = "CARD_EXPANDED", // "CARD_EXPANDED", "COMPACT_GRID", "HIGH_CONTRAST_SENIOR"
    val patientGreetingMessage: String = "Welcome to your ANA Care Health Portal",
    
    // Doctor Dashboard Layout Controls (Configured by Admin)
    val doctorCompactMode: Boolean = false,
    val doctorAutoExpandCriticals: Boolean = true,
    val doctorPrescriptionQuickBar: Boolean = true,
    val doctorHighlightCriticalVitals: Boolean = true,
    
    // App Version & Update Controls (Configured by Admin)
    val appVersionName: String = "2.5.0-PROD",
    val appBuildNumber: Int = 250,
    val updateReleaseNotes: String = "ANA Care Telemetry 2.5: Real-time clinical vital alert pop-ups and live admin layout engine.",
    val isUpdateBannerVisible: Boolean = true,
    val isMaintenanceMode: Boolean = false,
    val maintenanceAnnouncement: String = "",
    val systemThemeAccent: String = "TEAL_EMERALD", // "TEAL_EMERALD", "ROYAL_NAVY", "CRIMSON_VITAL", "INDIGO_MODERN"
    val lastUpdatedTimestamp: Long = System.currentTimeMillis()
)
