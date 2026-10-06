package com.example.data.firebase

import com.example.data.local.BetEntity
import com.example.data.local.RiskSettingsEntity
import com.example.data.local.UserAccountEntity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

data class FirestoreUserProfile(
    val userId: String = "",
    val email: String = "",
    val displayName: String = "",
    val preferredCurrency: String = "USD",
    val vipTier: String = "VIP GOLD MEMBER",
    val startingBankroll: Double = 50000.0,
    val weeklyLossLimit: Double = 3500.0,
    val weeklyStakeLimit: Double = 9000.0,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    fun toUserAccountEntity(): UserAccountEntity {
        return UserAccountEntity(
            id = 1,
            email = email,
            displayName = displayName.ifBlank { email.substringBefore("@").replaceFirstChar { it.uppercase() } },
            passwordHash = "",
            preferredCurrency = preferredCurrency,
            vipTier = vipTier,
            isLoggedIn = true,
            createdAt = createdAt?.toDate()?.time ?: System.currentTimeMillis()
        )
    }

    fun toRiskSettingsEntity(): RiskSettingsEntity {
        return RiskSettingsEntity(
            id = 1,
            startingBankroll = startingBankroll,
            weeklyLossLimit = weeklyLossLimit,
            weeklyStakeLimit = weeklyStakeLimit,
            currencyCode = preferredCurrency
        )
    }

    fun toWriteMap(): Map<String, Any> {
        return mapOf(
            "userId" to userId,
            "email" to email,
            "displayName" to displayName,
            "preferredCurrency" to preferredCurrency,
            "vipTier" to vipTier,
            "startingBankroll" to startingBankroll,
            "weeklyLossLimit" to weeklyLossLimit,
            "weeklyStakeLimit" to weeklyStakeLimit,
            "createdAt" to (createdAt ?: FieldValue.serverTimestamp()),
            "updatedAt" to FieldValue.serverTimestamp()
        ).filterValues { it != null }
    }
}

data class FirestoreBet(
    val id: String = "",
    val userId: String = "",
    val category: String = "SPORTS",
    val subcategory: String = "Fútbol",
    val eventName: String = "",
    val market: String = "",
    val odds: Double = 1.0,
    val stake: Double = 0.0,
    val status: String = "PENDING",
    val payout: Double = 0.0,
    val notes: String = "",
    val timestamp: Long = 0L,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    fun toBetEntity(numericId: Long = 0L): BetEntity {
        return BetEntity(
            id = if (numericId != 0L) numericId else (id.hashCode().toLong() and 0x7FFFFFFFFFFFFFFFL),
            category = category,
            subcategory = subcategory,
            eventName = eventName,
            market = market,
            odds = odds,
            stake = stake,
            status = status,
            payout = payout,
            notes = notes,
            timestamp = timestamp
        )
    }

    fun toWriteMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "userId" to userId,
            "category" to category,
            "subcategory" to subcategory,
            "eventName" to eventName,
            "market" to market,
            "odds" to odds,
            "stake" to stake,
            "status" to status,
            "payout" to payout,
            "notes" to notes,
            "timestamp" to timestamp,
            "createdAt" to (createdAt ?: FieldValue.serverTimestamp()),
            "updatedAt" to FieldValue.serverTimestamp()
        ).filterValues { it != null }
    }
}
