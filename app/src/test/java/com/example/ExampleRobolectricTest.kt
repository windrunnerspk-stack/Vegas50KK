package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppCurrency
import com.example.data.local.BetEntity
import com.example.data.local.InitialMockData
import com.example.data.local.UserAccountEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Vegas 50k", appName)
    }

    @Test
    fun `currency formatting works across currencies`() {
        assertEquals("$1,500.00", AppCurrency.USD.format(1500.0))
        assertEquals("€1,500.00", AppCurrency.EUR.format(1500.0))
        assertEquals("COL$1,500.00", AppCurrency.COP.format(1500.0))
        assertEquals("Mex$1,500.00", AppCurrency.MXN.format(1500.0))
        assertEquals("₮1,500.00", AppCurrency.USDT.format(1500.0))
        assertEquals("₿1,500.00", AppCurrency.BTC.format(1500.0))

        assertEquals(AppCurrency.EUR, AppCurrency.fromCode("EUR"))
        assertEquals(AppCurrency.USD, AppCurrency.fromCode("UNKNOWN"))
    }

    @Test
    fun `user account entity stores credentials and preferred currency`() {
        val user = UserAccountEntity(
            email = "latouchettdiego@gmail.com",
            displayName = "Diego Latouchett",
            passwordHash = "vegas50k",
            preferredCurrency = "EUR",
            vipTier = "HIGH-ROLLER VIP",
            isLoggedIn = true
        )
        assertEquals("latouchettdiego@gmail.com", user.email)
        assertEquals("EUR", user.preferredCurrency)
        assertTrue(user.isLoggedIn)
    }

    @Test
    fun `initial mock data returns empty list in production`() {
        val bets = InitialMockData.getSampleBets()
        assertTrue("In production there should be zero mock bets", bets.isEmpty())
    }

    @Test
    fun `net profit calculation is correct`() {
        val wonBet = BetEntity(
            category = "SPORTS",
            subcategory = "Fútbol",
            eventName = "Real Madrid vs City",
            market = "Victoria Local",
            odds = 2.10,
            stake = 500.0,
            status = "WON",
            payout = 1050.0
        )
        assertEquals(550.0, wonBet.netProfit, 0.001)

        val lostBet = BetEntity(
            category = "CASINO",
            subcategory = "Ruleta",
            eventName = "Mesa VIP",
            market = "Pleno 17",
            odds = 36.0,
            stake = 100.0,
            status = "LOST",
            payout = 0.0
        )
        assertEquals(-100.0, lostBet.netProfit, 0.001)
    }
}
