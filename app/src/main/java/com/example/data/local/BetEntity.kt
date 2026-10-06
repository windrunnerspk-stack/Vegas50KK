package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bets")
data class BetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String, // "SPORTS" or "CASINO"
    val subcategory: String, // "Fútbol", "Baloncesto", "Tenis", "Ruleta", "Blackjack", "Slots", "Poker", etc.
    val eventName: String, // "Real Madrid vs Manchester City", "Mesa Ruleta VIP #1"
    val market: String, // "Más de 2.5 goles", "Pleno al 17", "Gana Lakers -4.5"
    val odds: Double, // Cuota (e.g. 1.95)
    val stake: Double, // Monto apostado (e.g. 250.0)
    val status: String, // "PENDING", "WON", "LOST", "REFUNDED"
    val payout: Double, // Ganancia cobrada (0.0 if lost/pending, stake*odds if won, stake if refunded)
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    // Calculado: Beneficio neto de la apuesta individual
    val netProfit: Double
        get() = when (status) {
            "WON" -> payout - stake
            "LOST" -> -stake
            "REFUNDED" -> 0.0
            else -> 0.0 // PENDING: aún no liquidado
        }
}
