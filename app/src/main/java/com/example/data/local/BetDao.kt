package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BetDao {
    @Query("SELECT * FROM bets ORDER BY timestamp DESC")
    fun getAllBets(): Flow<List<BetEntity>>

    @Query("SELECT COUNT(*) FROM bets")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBet(bet: BetEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(bets: List<BetEntity>)

    @Update
    suspend fun updateBet(bet: BetEntity)

    @Delete
    suspend fun deleteBet(bet: BetEntity)

    @Query("DELETE FROM bets WHERE id = :id")
    suspend fun deleteBetById(id: Long)

    @Query("DELETE FROM bets")
    suspend fun deleteAllBets()
}
