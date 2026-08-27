package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.EncryptedMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EncryptedMessageDao {
    @Query("SELECT * FROM encrypted_messages WHERE (senderId = :userId AND receiverId = :peerId) OR (senderId = :peerId AND receiverId = :userId) ORDER BY timestamp ASC")
    fun getMessagesBetween(userId: String, peerId: String): Flow<List<EncryptedMessageEntity>>

    @Query("SELECT * FROM encrypted_messages WHERE senderId = :userId OR receiverId = :userId ORDER BY timestamp DESC")
    fun getAllMessagesForUser(userId: String): Flow<List<EncryptedMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: EncryptedMessageEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(messages: List<EncryptedMessageEntity>)

    @Query("UPDATE encrypted_messages SET isRead = 1 WHERE receiverId = :userId AND senderId = :peerId")
    suspend fun markAsRead(userId: String, peerId: String)
}
