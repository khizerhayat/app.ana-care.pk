package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_activities")
data class DailyActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val patientId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val activityType: String, // "Walking / Steps", "Physical Therapy", "Exercise", "Sleep", "Water Intake", "Diet & Nutrition"
    val durationMinutes: Int = 0,
    val metricValue: String, // "5,400 steps", "7.5 hrs sleep", "2,400 ml", "Balanced low-sodium lunch"
    val painScore: Int = 0, // 0 to 10
    val mood: String = "Good", // "Great", "Good", "Neutral", "Tired", "In Pain"
    val notes: String = "",
    val loggedBy: String = "Patient"
)
