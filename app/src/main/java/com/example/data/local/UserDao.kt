package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_accounts WHERE isLoggedIn = 1 LIMIT 1")
    fun getActiveUser(): Flow<UserAccountEntity?>

    @Query("SELECT * FROM user_accounts WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserAccountEntity?

    @Query("SELECT COUNT(*) FROM user_accounts")
    suspend fun getUserCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserAccountEntity): Long

    @Update
    suspend fun updateUser(user: UserAccountEntity)

    @Query("UPDATE user_accounts SET isLoggedIn = 0")
    suspend fun logoutAll()

    @Query("UPDATE user_accounts SET isLoggedIn = 1 WHERE id = :userId")
    suspend fun setLoggedIn(userId: Long)

    @Query("UPDATE user_accounts SET preferredCurrency = :currencyCode WHERE isLoggedIn = 1")
    suspend fun updateCurrencyForActiveUser(currencyCode: String)
}
