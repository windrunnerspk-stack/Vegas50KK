package com.example.data.local

object InitialMockData {
    private const val ONE_DAY_MS = 86_400_000L
    private const val ONE_HOUR_MS = 3_600_000L

    fun getSampleBets(baseTime: Long = System.currentTimeMillis()): List<BetEntity> {
        return listOf(
            BetEntity(
                category = "SPORTS",
                subcategory = "Fútbol",
                eventName = "Liverpool vs Manchester United",
                market = "Ambos Equipos Marcan y Más 2.5",
                odds = 1.95,
                stake = 400.0,
                status = "PENDING",
                payout = 0.0,
                notes = "Clásico inglés de alta intensidad ofensiva",
                timestamp = baseTime - (ONE_HOUR_MS * 2)
            ),
            BetEntity(
                category = "CASINO",
                subcategory = "Baccarat",
                eventName = "Salón Privé VIP Baccarat",
                market = "Apuesta Banca con Comisión Reducida",
                odds = 1.95,
                stake = 500.0,
                status = "PENDING",
                payout = 0.0,
                notes = "Gestión 1% del bankroll en mesa alta",
                timestamp = baseTime - (ONE_HOUR_MS * 5)
            ),
            BetEntity(
                category = "SPORTS",
                subcategory = "Fútbol",
                eventName = "Real Madrid vs Manchester City",
                market = "Victoria Local y Más de 1.5 goles",
                odds = 2.10,
                stake = 500.0,
                status = "WON",
                payout = 1050.0,
                notes = "Ida Cuartos Champions League, gran valor en cuota",
                timestamp = baseTime - (ONE_DAY_MS * 1)
            ),
            BetEntity(
                category = "CASINO",
                subcategory = "Ruleta",
                eventName = "Ruleta Relámpago VIP Francesa",
                market = "Multiplicador Columna Central 50x / Pleno",
                odds = 5.00,
                stake = 350.0,
                status = "WON",
                payout = 1750.0,
                notes = "Acierto directo en mesa con multiplicador activo",
                timestamp = baseTime - (ONE_DAY_MS * 1) - (ONE_HOUR_MS * 4)
            ),
            BetEntity(
                category = "CASINO",
                subcategory = "Blackjack",
                eventName = "High Roller Blackjack Mesa 5",
                market = "Mano Principal + Apuesta Perfect Pairs",
                odds = 2.50,
                stake = 750.0,
                status = "WON",
                payout = 1875.0,
                notes = "Blackjack natural con as y rey de picas",
                timestamp = baseTime - (ONE_DAY_MS * 2)
            ),
            BetEntity(
                category = "SPORTS",
                subcategory = "Baloncesto",
                eventName = "Los Angeles Lakers vs Boston Celtics",
                market = "Lakers Handicap -4.5 puntos",
                odds = 1.90,
                stake = 350.0,
                status = "WON",
                payout = 665.0,
                notes = "Dominio en la pintura y regreso de titulares",
                timestamp = baseTime - (ONE_DAY_MS * 3)
            ),
            BetEntity(
                category = "SPORTS",
                subcategory = "Fútbol",
                eventName = "Arsenal vs Chelsea FC",
                market = "Empate o Victoria Visitante (Doble Oportunidad)",
                odds = 1.85,
                stake = 400.0,
                status = "LOST",
                payout = 0.0,
                notes = "Gol encajado en el minuto 89 arruinó el pick",
                timestamp = baseTime - (ONE_DAY_MS * 3) - (ONE_HOUR_MS * 6)
            ),
            BetEntity(
                category = "CASINO",
                subcategory = "Slots",
                eventName = "Gates of Olympus 1000",
                market = "Ronda de Giros Gratis Comprada x100",
                odds = 4.20,
                stake = 200.0,
                status = "WON",
                payout = 840.0,
                notes = "Multiplicadores acumulados de x15 y x25",
                timestamp = baseTime - (ONE_DAY_MS * 4)
            ),
            BetEntity(
                category = "CASINO",
                subcategory = "Ruleta",
                eventName = "Mesa Ruleta Europea Gold",
                market = "Pleno 17 Negro y Vecinos del Cero",
                odds = 2.00,
                stake = 300.0,
                status = "WON",
                payout = 600.0,
                notes = "Estrategia de sectores calientes en rueda",
                timestamp = baseTime - (ONE_DAY_MS * 5)
            ),
            BetEntity(
                category = "SPORTS",
                subcategory = "Tenis",
                eventName = "Carlos Alcaraz vs Jannik Sinner",
                market = "Más de 38.5 Juegos Totales",
                odds = 2.25,
                stake = 600.0,
                status = "LOST",
                payout = 0.0,
                notes = "Partido cerrado antes de tiempo en 3 sets corridos",
                timestamp = baseTime - (ONE_DAY_MS * 6)
            ),
            BetEntity(
                category = "CASINO",
                subcategory = "Poker",
                eventName = "Texas Hold'em No-Limit $10/$25",
                market = "Sesión Cash Game Buy-in Profundo",
                odds = 2.80,
                stake = 1000.0,
                status = "WON",
                payout = 2800.0,
                notes = "Gran bote ganado con color de ases al river",
                timestamp = baseTime - (ONE_DAY_MS * 7)
            ),
            BetEntity(
                category = "SPORTS",
                subcategory = "Fútbol",
                eventName = "Bayern Múnich vs Borussia Dortmund",
                market = "Hándicap Asiático Bayern -1.0",
                odds = 1.75,
                stake = 450.0,
                status = "REFUNDED",
                payout = 450.0,
                notes = "Victoria por exactamente 1 gol, devolución total de apuesta",
                timestamp = baseTime - (ONE_DAY_MS * 8)
            ),
            BetEntity(
                category = "CASINO",
                subcategory = "Blackjack",
                eventName = "Diamond VIP Blackjack",
                market = "Doble en 11 contra 6 del Crupier",
                odds = 2.00,
                stake = 800.0,
                status = "LOST",
                payout = 0.0,
                notes = "Crupier sacó 5 cartas y sumó 21 milagroso",
                timestamp = baseTime - (ONE_DAY_MS * 9)
            ),
            BetEntity(
                category = "SPORTS",
                subcategory = "Fútbol",
                eventName = "FC Barcelona vs Paris Saint-Germain",
                market = "Barcelona Gana y Más de 2.5",
                odds = 2.05,
                stake = 550.0,
                status = "WON",
                payout = 1127.50,
                notes = "Superioridad táctica en casa",
                timestamp = baseTime - (ONE_DAY_MS * 10)
            )
        )
    }
}
