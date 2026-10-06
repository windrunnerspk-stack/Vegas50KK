package com.example

import com.example.data.firebase.FirestoreBet
import com.example.data.firebase.FirestoreUserProfile
import com.example.data.local.BetEntity
import com.google.firebase.Timestamp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class FirestoreModelsTest {

    @Test
    fun `firestore bet converts to and from domain bet entity`() {
        val firestoreBet = FirestoreBet(
            id = "bet_12345",
            userId = "user_abc",
            category = "SPORTS",
            subcategory = "Fútbol",
            eventName = "Real Madrid vs Barcelona",
            market = "Victoria Local",
            odds = 2.10,
            stake = 500.0,
            status = "WON",
            payout = 1050.0,
            notes = "Estrategia clásica",
            timestamp = 1700000000000L
        )

        val entity = firestoreBet.toBetEntity(12345L)
        assertEquals(12345L, entity.id)
        assertEquals("SPORTS", entity.category)
        assertEquals("Fútbol", entity.subcategory)
        assertEquals(550.0, entity.netProfit, 0.001)

        val writeMap = firestoreBet.toWriteMap()
        assertEquals("bet_12345", writeMap["id"])
        assertEquals("user_abc", writeMap["userId"])
        assertEquals(2.10, writeMap["odds"])
        assertEquals(500.0, writeMap["stake"])
        assertNotNull(writeMap["createdAt"])
    }

    @Test
    fun `firestore user profile converts to entities cleanly`() {
        val profile = FirestoreUserProfile(
            userId = "user_xyz",
            email = "vegas@vip.com",
            displayName = "Vegas High Roller",
            preferredCurrency = "EUR",
            vipTier = "VIP GOLD MEMBER",
            startingBankroll = 60000.0,
            weeklyLossLimit = 4000.0,
            weeklyStakeLimit = 12000.0,
            createdAt = Timestamp.now()
        )

        val userEntity = profile.toUserAccountEntity()
        assertEquals("vegas@vip.com", userEntity.email)
        assertEquals("Vegas High Roller", userEntity.displayName)
        assertEquals("EUR", userEntity.preferredCurrency)

        val settingsEntity = profile.toRiskSettingsEntity()
        assertEquals(60000.0, settingsEntity.startingBankroll, 0.001)
        assertEquals(4000.0, settingsEntity.weeklyLossLimit, 0.001)
        assertEquals(12000.0, settingsEntity.weeklyStakeLimit, 0.001)
        assertEquals("EUR", settingsEntity.currencyCode)

        val writeMap = profile.toWriteMap()
        assertEquals("user_xyz", writeMap["userId"])
        assertEquals("vegas@vip.com", writeMap["email"])
        assertNotNull(writeMap["createdAt"])
    }
}
