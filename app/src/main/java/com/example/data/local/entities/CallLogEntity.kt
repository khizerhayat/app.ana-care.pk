package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "call_logs")
data class CallLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val callId: String = UUID.randomUUID().toString(),
    val callerId: String,
    val callerName: String,
    val callerRole: String, // "PATIENT", "DOCTOR", "CAREGIVER"
    val callerAvatar: String = "",
    val recipientId: String,
    val recipientName: String,
    val recipientRole: String,
    val recipientAvatar: String = "",
    val callType: String = "VIDEO", // "AUDIO" or "VIDEO"
    val callDirection: String = "DIALED", // "MISSED", "RECEIVED", "DIALED"
    val timestamp: Long = System.currentTimeMillis(),
    val durationSeconds: Int = 0,
    val caseTitle: String? = null
)
