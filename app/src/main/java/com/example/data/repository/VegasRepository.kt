package com.example.data.repository

import com.example.data.auth.FirebaseAuthService
import com.example.data.local.BetDao
import com.example.data.local.BetEntity
import com.example.data.local.InitialMockData
import com.example.data.local.RiskSettingsDao
import com.example.data.local.RiskSettingsEntity
import com.example.data.local.UserAccountEntity
import com.example.data.local.UserDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class VegasRepository(
    private val betDao: BetDao,
    private val riskSettingsDao: RiskSettingsDao,
    private val userDao: UserDao,
    private val firebaseAuthService: FirebaseAuthService
) {
    val allBets: Flow<List<BetEntity>> = betDao.getAllBets()
    val riskSettings: Flow<RiskSettingsEntity?> = riskSettingsDao.getSettings()
    val activeUser: Flow<UserAccountEntity?> = userDao.getActiveUser()

    suspend fun ensureInitialized() {
        val count = betDao.getCount()
        if (count == 0) {
            betDao.insertAll(InitialMockData.getSampleBets())
        }
        val currentSettings = riskSettingsDao.getSettings().firstOrNull()
        if (currentSettings == null) {
            riskSettingsDao.insertOrUpdate(
                RiskSettingsEntity(
                    id = 1,
                    startingBankroll = 50000.0,
                    weeklyLossLimit = 3500.0,
                    weeklyStakeLimit = 9000.0,
                    currencyCode = "USD"
                )
            )
        }
        val userCount = userDao.getUserCount()
        if (userCount == 0) {
            // Seed initial default VIP account for demo readiness
            userDao.insertUser(
                UserAccountEntity(
                    email = "latouchettdiego@gmail.com",
                    displayName = "Diego Latouchett",
                    passwordHash = "vegas50k",
                    preferredCurrency = "USD",
                    vipTier = "HIGH-ROLLER VIP",
                    isLoggedIn = false
                )
            )
        }
    }

    suspend fun insertBet(bet: BetEntity): Long = betDao.insertBet(bet)

    suspend fun updateBet(bet: BetEntity) = betDao.updateBet(bet)

    suspend fun deleteBetById(id: Long) = betDao.deleteBetById(id)

    suspend fun saveRiskSettings(settings: RiskSettingsEntity) =
        riskSettingsDao.insertOrUpdate(settings)

    suspend fun setCurrency(currencyCode: String) {
        val current = riskSettingsDao.getSettings().firstOrNull() ?: RiskSettingsEntity()
        riskSettingsDao.insertOrUpdate(current.copy(currencyCode = currencyCode))
        userDao.updateCurrencyForActiveUser(currencyCode)
    }

    suspend fun registerUser(email: String, name: String, password: String, currency: String): Result<UserAccountEntity> {
        val cleanEmail = email.trim().lowercase()

        // 1. Firebase Auth Registration
        val fbResult = firebaseAuthService.registerUser(cleanEmail, password, name)
        if (fbResult.isFailure) {
            val err = fbResult.exceptionOrNull()?.localizedMessage ?: "Error de autenticación en Firebase"
            return Result.failure(Exception(err))
        }

        // 2. Persist local user profile
        userDao.logoutAll()
        val existing = userDao.getUserByEmail(cleanEmail)
        val user = if (existing != null) {
            val updated = existing.copy(
                displayName = name.trim().ifBlank { cleanEmail.substringBefore("@") },
                passwordHash = password,
                preferredCurrency = currency,
                isLoggedIn = true
            )
            userDao.updateUser(updated)
            updated
        } else {
            val newUser = UserAccountEntity(
                email = cleanEmail,
                displayName = name.trim().ifBlank { cleanEmail.substringBefore("@") },
                passwordHash = password,
                preferredCurrency = currency,
                vipTier = "VIP GOLD MEMBER",
                isLoggedIn = true
            )
            val id = userDao.insertUser(newUser)
            newUser.copy(id = id)
        }

        setCurrency(currency)
        return Result.success(user)
    }

    suspend fun loginUser(email: String, password: String): Result<UserAccountEntity> {
        val cleanEmail = email.trim().lowercase()

        // 1. Firebase Auth Login
        val fbResult = firebaseAuthService.loginUser(cleanEmail, password)
        if (fbResult.isFailure) {
            // Check if local demo account matches
            val localUser = userDao.getUserByEmail(cleanEmail)
            if (localUser != null && localUser.passwordHash == password) {
                userDao.logoutAll()
                userDao.setLoggedIn(localUser.id)
                setCurrency(localUser.preferredCurrency)
                return Result.success(localUser.copy(isLoggedIn = true))
            }
            val err = fbResult.exceptionOrNull()?.localizedMessage ?: "Credenciales incorrectas"
            return Result.failure(Exception(err))
        }

        // 2. Activate local session
        userDao.logoutAll()
        val user = userDao.getUserByEmail(cleanEmail) ?: run {
            val newUser = UserAccountEntity(
                email = cleanEmail,
                displayName = cleanEmail.substringBefore("@").replaceFirstChar { it.uppercase() },
                passwordHash = password,
                preferredCurrency = "USD",
                vipTier = "HIGH-ROLLER VIP",
                isLoggedIn = true
            )
            val id = userDao.insertUser(newUser)
            newUser.copy(id = id)
        }

        userDao.setLoggedIn(user.id)
        setCurrency(user.preferredCurrency)
        return Result.success(user.copy(isLoggedIn = true))
    }

    suspend fun logoutUser() {
        firebaseAuthService.signOut()
        userDao.logoutAll()
    }

    suspend fun resetDemoData() {
        betDao.deleteAllBets()
        betDao.insertAll(InitialMockData.getSampleBets())
        riskSettingsDao.insertOrUpdate(
            RiskSettingsEntity(
                id = 1,
                startingBankroll = 50000.0,
                weeklyLossLimit = 3500.0,
                weeklyStakeLimit = 9000.0,
                currencyCode = "USD"
            )
        )
    }
}
