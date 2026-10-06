package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "risk_settings")
data class RiskSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val startingBankroll: Double = 50000.0,
    val weeklyLossLimit: Double = 3500.0,
    val weeklyStakeLimit: Double = 9000.0,
    val currencyCode: String = "USD"
)
