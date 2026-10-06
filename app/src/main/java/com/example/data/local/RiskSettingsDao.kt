package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RiskSettingsDao {
    @Query("SELECT * FROM risk_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<RiskSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: RiskSettingsEntity)
}
