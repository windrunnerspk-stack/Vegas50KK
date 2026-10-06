package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [BetEntity::class, RiskSettingsEntity::class, UserAccountEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun betDao(): BetDao
    abstract fun riskSettingsDao(): RiskSettingsDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vegas50k_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Preload with realistic initial data
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.let { database ->
                                    database.betDao().insertAll(InitialMockData.getSampleBets())
                                    database.riskSettingsDao().insertOrUpdate(
                                        RiskSettingsEntity(
                                            id = 1,
                                            startingBankroll = 50000.0,
                                            weeklyLossLimit = 3500.0,
                                            weeklyStakeLimit = 9000.0,
                                            currencyCode = "USD"
                                        )
                                    )
                                    database.userDao().insertUser(
                                        UserAccountEntity(
                                            email = "latouchettdiego@gmail.com",
                                            displayName = "Diego Latouchett",
                                            passwordHash = "vegas50k",
                                            preferredCurrency = "USD",
                                            vipTier = "HIGH-ROLLER VIP",
                                            isLoggedIn = true
                                        )
                                    )
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
