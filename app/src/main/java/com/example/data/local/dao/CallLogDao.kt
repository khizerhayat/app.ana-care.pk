package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.CallLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CallLogDao {
    @Query("SELECT * FROM call_logs WHERE callerId = :userId OR recipientId = :userId ORDER BY timestamp DESC")
    fun getAllCallLogsForUser(userId: String): Flow<List<CallLogEntity>>

    @Query("SELECT * FROM call_logs WHERE (callerId = :userId OR recipientId = :userId) AND callDirection = 'MISSED' ORDER BY timestamp DESC")
    fun getMissedCallLogsForUser(userId: String): Flow<List<CallLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallLog(callLog: CallLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(callLogs: List<CallLogEntity>)

    @Query("DELETE FROM call_logs WHERE id = :id")
    suspend fun deleteCallLog(id: Long)

    @Query("DELETE FROM call_logs WHERE callerId = :userId OR recipientId = :userId")
    suspend fun clearAllCallLogsForUser(userId: String)
}
