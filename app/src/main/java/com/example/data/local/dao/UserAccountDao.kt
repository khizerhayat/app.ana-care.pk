package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.UserAccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserAccountDao {
    @Query("SELECT * FROM user_accounts ORDER BY isPrimaryPatient DESC, name ASC")
    fun getAllAccounts(): Flow<List<UserAccountEntity>>

    @Query("SELECT * FROM user_accounts WHERE isCurrentActive = 1 LIMIT 1")
    fun getActiveAccount(): Flow<UserAccountEntity?>

    @Query("SELECT * FROM user_accounts WHERE userId = :userId LIMIT 1")
    suspend fun getAccountById(userId: String): UserAccountEntity?

    @Query("SELECT * FROM user_accounts WHERE role = 'CAREGIVER' AND assignedPatientId = :patientId")
    fun getCaregiversForPatient(patientId: String): Flow<List<UserAccountEntity>>

    @Query("SELECT * FROM user_accounts WHERE role = 'PATIENT' AND assignedDoctorId = :doctorId")
    fun getPatientsForDoctor(doctorId: String): Flow<List<UserAccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: UserAccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(accounts: List<UserAccountEntity>)

    @Update
    suspend fun updateAccount(account: UserAccountEntity)

    @Query("UPDATE user_accounts SET isCurrentActive = 0")
    suspend fun clearActiveAccounts()

    @Query("UPDATE user_accounts SET isCurrentActive = 1 WHERE userId = :userId")
    suspend fun setActiveAccount(userId: String)

    @Query("DELETE FROM user_accounts WHERE userId = :userId")
    suspend fun deleteAccount(userId: String)
}
