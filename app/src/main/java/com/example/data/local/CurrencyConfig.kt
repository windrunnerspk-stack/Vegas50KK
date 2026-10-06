package com.example.data.local

import java.util.Locale

enum class AppCurrency(
    val code: String,
    val symbol: String,
    val displayName: String,
    val flag: String
) {
    USD("USD", "$", "Dólar Estadounidense", "🇺🇸"),
    EUR("EUR", "€", "Euro", "🇪🇺"),
    GBP("GBP", "£", "Libra Esterlina", "🇬🇧"),
    MXN("MXN", "Mex$", "Peso Mexicano", "🇲🇽"),
    COP("COP", "COL$", "Peso Colombiano", "🇨🇴"),
    ARS("ARS", "ARS$", "Peso Argentino", "🇦🇷"),
    CLP("CLP", "CLP$", "Peso Chileno", "🇨🇱"),
    BRL("BRL", "R$", "Real Brasileño", "🇧🇷"),
    USDT("USDT", "₮", "Tether USDT", "🟢"),
    BTC("BTC", "₿", "Bitcoin", "🪙");

    fun format(amount: Double): String {
        return "$symbol${String.format(Locale.US, "%,.2f", amount)}"
    }

    fun formatNoDecimals(amount: Double): String {
        return "$symbol${String.format(Locale.US, "%,.0f", amount)}"
    }

    companion object {
        fun fromCode(code: String?): AppCurrency {
            return values().firstOrNull { it.code.equals(code, ignoreCase = true) } ?: USD
        }
    }
}
