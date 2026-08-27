package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "encrypted_messages")
data class EncryptedMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val senderId: String,
    val senderName: String,
    val senderRole: String, // "PATIENT", "DOCTOR", "CAREGIVER"
    val receiverId: String,
    val receiverName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val messageText: String,
    val cipherTextDigest: String = "", // E2EE cipher payload representation
    val isEncrypted: Boolean = true,
    val attachmentName: String? = null,
    val attachmentType: String? = null, // "PDF_REPORT", "IMAGE_SCAN", "PRESCRIPTION", "LAB_DOC"
    val attachmentSize: String? = null,
    val isRead: Boolean = false
)
