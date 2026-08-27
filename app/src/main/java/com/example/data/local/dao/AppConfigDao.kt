package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.AppConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppConfigDao {
    @Query("SELECT * FROM app_configurations WHERE configKey = :key LIMIT 1")
    fun getAppConfigFlow(key: String = "SYSTEM_CONFIG"): Flow<AppConfigEntity?>

    @Query("SELECT * FROM app_configurations WHERE configKey = :key LIMIT 1")
    suspend fun getAppConfig(key: String = "SYSTEM_CONFIG"): AppConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateConfig(config: AppConfigEntity)
}
