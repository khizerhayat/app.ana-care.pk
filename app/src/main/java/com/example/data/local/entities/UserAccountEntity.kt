package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_accounts")
data class UserAccountEntity(
    @PrimaryKey val userId: String,
    val name: String,
    val email: String,
    val role: String, // "PATIENT", "MEDICAL_PROFESSIONAL", "CAREGIVER"
    val phone: String = "",
    val age: String = "68",
    val diagnosis: String = "Hypertension, Type-2 Diabetes Mellitus",
    val medicalHistory: String = "Coronary artery disease (2021), mild asthma, penicillin allergy",
    val dateOfBirth: String = "",
    val gender: String = "",
    val bloodGroup: String = "",
    val allergies: String = "",
    val emergencyContact: String = "",
    val insuranceProvider: String = "",
    val insurancePolicyNo: String = "",
    val specialty: String = "", // For doctors/nurses
    val licenseNumber: String = "",
    val hospitalClinic: String = "",
    val avatarInitials: String = "",
    val biometricEnabled: Boolean = true,
    val mfaEnabled: Boolean = true,
    val isCurrentActive: Boolean = false,
    val isPrimaryPatient: Boolean = true,
    val caregiverPermissions: String = "FULL_ACCESS", // "FULL_ACCESS", "VIEW_ONLY", "EMERGENCY_ONLY"
    val assignedPatientId: String = "",
    val assignedDoctorId: String = "1001",
    val relationship: String = "" // "Self", "Father", "Mother", "Spouse", "Child", "Primary Care Physician"
)
