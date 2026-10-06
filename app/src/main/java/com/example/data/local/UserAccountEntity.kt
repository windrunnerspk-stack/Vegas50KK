package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_accounts",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserAccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val email: String,
    val displayName: String,
    val passwordHash: String,
    val preferredCurrency: String = "USD",
    val vipTier: String = "HIGH-ROLLER VIP",
    val createdAt: Long = System.currentTimeMillis(),
    val isLoggedIn: Boolean = true
)
