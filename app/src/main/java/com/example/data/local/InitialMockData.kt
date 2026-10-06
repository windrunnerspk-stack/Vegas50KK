package com.example.data.local

object InitialMockData {
    // Production mode: No test bets or mock data
    fun getSampleBets(): List<BetEntity> {
        return emptyList()
    }
}
