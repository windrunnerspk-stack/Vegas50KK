package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.auth.FirebaseAuthService
import com.example.data.auth.awaitTask
import com.example.data.firebase.FirestoreBet
import com.example.data.firebase.FirestoreUserProfile
import com.example.data.firebase.OperationType
import com.example.data.firebase.handleFirestoreError
import com.example.data.local.BetDao
import com.example.data.local.BetEntity
import com.example.data.local.RiskSettingsDao
import com.example.data.local.RiskSettingsEntity
import com.example.data.local.UserAccountEntity
import com.example.data.local.UserDao
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "VegasRepository"

class VegasRepository(
    private val betDao: BetDao,
    private val riskSettingsDao: RiskSettingsDao,
    private val userDao: UserDao,
    val firebaseAuthService: FirebaseAuthService,
    private val firestore: FirebaseFirestore
) {

    constructor(
        context: Context,
        betDao: BetDao,
        riskSettingsDao: RiskSettingsDao,
        userDao: UserDao,
        firebaseAuthService: FirebaseAuthService
    ) : this(
        betDao = betDao,
        riskSettingsDao = riskSettingsDao,
        userDao = userDao,
        firebaseAuthService = firebaseAuthService,
        firestore = FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    // Reactive StateFlows observed by UI and ViewModel
    private val _allBets = MutableStateFlow<List<BetEntity>>(emptyList())
    val allBets: Flow<List<BetEntity>> = _allBets.asStateFlow()

    private val _riskSettings = MutableStateFlow<RiskSettingsEntity?>(null)
    val riskSettings: Flow<RiskSettingsEntity?> = _riskSettings.asStateFlow()

    private val _activeUser = MutableStateFlow<UserAccountEntity?>(null)
    val activeUser: Flow<UserAccountEntity?> = _activeUser.asStateFlow()

    private var userDocListener: ListenerRegistration? = null
    private var betsCollectionListener: ListenerRegistration? = null
    private var currentActiveUserId: String? = null

    init {
        // Observe Firebase Auth state changes
        repositoryScope.launch {
            firebaseAuthService.authStateFlow().collect { firebaseUser ->
                if (firebaseUser != null) {
                    attachFirestoreListeners(firebaseUser)
                } else {
                    detachFirestoreListeners()
                    // Fall back to local room data for guest / unauthenticated mode
                    val localUser = userDao.getActiveUser().firstOrNull()
                    _activeUser.value = localUser
                    val localSettings = riskSettingsDao.getSettings().firstOrNull()
                    _riskSettings.value = localSettings
                    val localBets = betDao.getAllBets().firstOrNull() ?: emptyList()
                    _allBets.value = localBets
                }
            }
        }
    }

    suspend fun ensureInitialized() {
        val currentSettings = riskSettingsDao.getSettings().firstOrNull()
        if (currentSettings == null) {
            val defaultSettings = RiskSettingsEntity(
                id = 1,
                startingBankroll = 50000.0,
                weeklyLossLimit = 3500.0,
                weeklyStakeLimit = 9000.0,
                currencyCode = "USD"
            )
            riskSettingsDao.insertOrUpdate(defaultSettings)
            if (_riskSettings.value == null) {
                _riskSettings.value = defaultSettings
            }
        } else if (_riskSettings.value == null) {
            _riskSettings.value = currentSettings
        }
    }

    private fun attachFirestoreListeners(firebaseUser: FirebaseUser) {
        val uid = firebaseUser.uid
        if (currentActiveUserId == uid && userDocListener != null) return

        detachFirestoreListeners()
        currentActiveUserId = uid

        val userDocRef = firestore.collection("users").document(uid)
        val betsCollectionRef = userDocRef.collection("bets")

        // 1. Listen to User Profile & Risk Settings in Firestore
        userDocListener = userDocRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                handleFirestoreError(error, OperationType.GET, userDocRef.path)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val profile = snapshot.toObject(FirestoreUserProfile::class.java)
                if (profile != null) {
                    val userEntity = profile.toUserAccountEntity()
                    val settingsEntity = profile.toRiskSettingsEntity()

                    _activeUser.value = userEntity
                    _riskSettings.value = settingsEntity

                    // Cache locally in Room for offline access
                    repositoryScope.launch {
                        userDao.logoutAll()
                        userDao.insertUser(userEntity)
                        riskSettingsDao.insertOrUpdate(settingsEntity)
                    }
                }
            } else if (snapshot != null && !snapshot.exists()) {
                // First-time user in Firestore: create initial profile document
                repositoryScope.launch {
                    initFirestoreUserProfile(firebaseUser)
                }
            }
        }

        // 2. Listen to User Bets in Firestore in real-time
        betsCollectionListener = betsCollectionRef.addSnapshotListener { querySnapshot, error ->
            if (error != null) {
                handleFirestoreError(error, OperationType.LIST, betsCollectionRef.path)
                return@addSnapshotListener
            }

            if (querySnapshot != null) {
                val firestoreBets = querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(FirestoreBet::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                }
                val betEntities = firestoreBets.map { it.toBetEntity() }.sortedByDescending { it.timestamp }
                _allBets.value = betEntities

                // Sync to local Room cache
                repositoryScope.launch {
                    betDao.deleteAllBets()
                    betDao.insertAll(betEntities)
                }
            }
        }
    }

    private fun detachFirestoreListeners() {
        userDocListener?.remove()
        userDocListener = null
        betsCollectionListener?.remove()
        betsCollectionListener = null
        currentActiveUserId = null
    }

    private suspend fun initFirestoreUserProfile(user: FirebaseUser) {
        val uid = user.uid
        val localSettings = riskSettingsDao.getSettings().firstOrNull()
        val curr = localSettings?.currencyCode ?: "USD"
        val startBank = localSettings?.startingBankroll ?: 50000.0
        val lossLimit = localSettings?.weeklyLossLimit ?: 3500.0
        val stakeLimit = localSettings?.weeklyStakeLimit ?: 9000.0

        val newProfile = FirestoreUserProfile(
            userId = uid,
            email = user.email ?: "",
            displayName = user.displayName ?: user.email?.substringBefore("@")?.replaceFirstChar { it.uppercase() } ?: "VIP Vegas",
            preferredCurrency = curr,
            vipTier = "VIP GOLD MEMBER",
            startingBankroll = startBank,
            weeklyLossLimit = lossLimit,
            weeklyStakeLimit = stakeLimit,
            createdAt = Timestamp.now()
        )

        val docRef = firestore.collection("users").document(uid)
        try {
            docRef.set(newProfile.toWriteMap()).awaitTask()
            Log.d(TAG, "Created user profile in Firestore for $uid")

            // Migrate any local bets to Firestore if available
            migrateLocalBetsToFirestore(uid)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
        }
    }

    private suspend fun migrateLocalBetsToFirestore(userId: String) {
        try {
            val localBets = betDao.getAllBets().firstOrNull() ?: emptyList()
            if (localBets.isNotEmpty()) {
                val batch = firestore.batch()
                localBets.forEach { bet ->
                    val betDocId = "bet_${bet.id}"
                    val betDocRef = firestore.collection("users").document(userId).collection("bets").document(betDocId)
                    val firestoreBet = FirestoreBet(
                        id = betDocId,
                        userId = userId,
                        category = bet.category,
                        subcategory = bet.subcategory,
                        eventName = bet.eventName,
                        market = bet.market,
                        odds = bet.odds,
                        stake = bet.stake,
                        status = bet.status,
                        payout = bet.payout,
                        notes = bet.notes,
                        timestamp = bet.timestamp,
                        createdAt = Timestamp.now()
                    )
                    batch.set(betDocRef, firestoreBet.toWriteMap())
                }
                batch.commit().awaitTask()
                Log.d(TAG, "Migrated ${localBets.size} local bets to Firestore for user $userId")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error migrating local bets to Firestore", e)
        }
    }

    suspend fun insertBet(bet: BetEntity): Long {
        val uid = currentActiveUserId ?: firebaseAuthService.currentFirebaseUser?.uid

        val generatedId = if (bet.id != 0L) bet.id else System.currentTimeMillis()
        val betWithId = bet.copy(id = generatedId)

        // Always save to Room for instant local reflection
        betDao.insertBet(betWithId)

        if (uid != null) {
            val docId = "bet_$generatedId"
            val docRef = firestore.collection("users").document(uid).collection("bets").document(docId)
            val firestoreBet = FirestoreBet(
                id = docId,
                userId = uid,
                category = betWithId.category,
                subcategory = betWithId.subcategory,
                eventName = betWithId.eventName,
                market = betWithId.market,
                odds = betWithId.odds,
                stake = betWithId.stake,
                status = betWithId.status,
                payout = betWithId.payout,
                notes = betWithId.notes,
                timestamp = betWithId.timestamp,
                createdAt = Timestamp.now()
            )
            try {
                docRef.set(firestoreBet.toWriteMap()).awaitTask()
            } catch (e: Exception) {
                handleFirestoreError(e, OperationType.CREATE, docRef.path)
            }
        } else {
            // Update in-memory stream for guest mode
            val currentList = _allBets.value.toMutableList()
            currentList.removeAll { it.id == generatedId }
            currentList.add(0, betWithId)
            _allBets.value = currentList
        }

        return generatedId
    }

    suspend fun updateBet(bet: BetEntity) {
        val uid = currentActiveUserId ?: firebaseAuthService.currentFirebaseUser?.uid

        betDao.updateBet(bet)

        if (uid != null) {
            val docId = "bet_${bet.id}"
            val docRef = firestore.collection("users").document(uid).collection("bets").document(docId)
            val firestoreBet = FirestoreBet(
                id = docId,
                userId = uid,
                category = bet.category,
                subcategory = bet.subcategory,
                eventName = bet.eventName,
                market = bet.market,
                odds = bet.odds,
                stake = bet.stake,
                status = bet.status,
                payout = bet.payout,
                notes = bet.notes,
                timestamp = bet.timestamp,
                createdAt = Timestamp(bet.timestamp / 1000, 0)
            )
            try {
                docRef.set(firestoreBet.toWriteMap()).awaitTask()
            } catch (e: Exception) {
                handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            }
        } else {
            val currentList = _allBets.value.map { if (it.id == bet.id) bet else it }
            _allBets.value = currentList
        }
    }

    suspend fun deleteBetById(id: Long) {
        val uid = currentActiveUserId ?: firebaseAuthService.currentFirebaseUser?.uid

        betDao.deleteBetById(id)

        if (uid != null) {
            val docId = "bet_$id"
            val docRef = firestore.collection("users").document(uid).collection("bets").document(docId)
            try {
                docRef.delete().awaitTask()
            } catch (e: Exception) {
                handleFirestoreError(e, OperationType.DELETE, docRef.path)
            }
        } else {
            _allBets.value = _allBets.value.filter { it.id != id }
        }
    }

    suspend fun saveRiskSettings(settings: RiskSettingsEntity) {
        val uid = currentActiveUserId ?: firebaseAuthService.currentFirebaseUser?.uid

        riskSettingsDao.insertOrUpdate(settings)
        _riskSettings.value = settings

        if (uid != null) {
            val docRef = firestore.collection("users").document(uid)
            try {
                val updates = mapOf(
                    "startingBankroll" to settings.startingBankroll,
                    "weeklyLossLimit" to settings.weeklyLossLimit,
                    "weeklyStakeLimit" to settings.weeklyStakeLimit,
                    "preferredCurrency" to settings.currencyCode,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.update(updates).awaitTask()
            } catch (e: Exception) {
                handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            }
        }
    }

    suspend fun setCurrency(currencyCode: String) {
        val current = _riskSettings.value ?: riskSettingsDao.getSettings().firstOrNull() ?: RiskSettingsEntity()
        val updated = current.copy(currencyCode = currencyCode)
        saveRiskSettings(updated)
    }

    suspend fun signInWithGoogle(activityContext: Context): Result<UserAccountEntity> {
        val authResult = firebaseAuthService.signInWithGoogle(activityContext)
        return if (authResult.isSuccess) {
            val fbUser = authResult.getOrThrow()
            attachFirestoreListeners(fbUser)

            val userEntity = UserAccountEntity(
                email = fbUser.email ?: "",
                displayName = fbUser.displayName ?: fbUser.email?.substringBefore("@")?.replaceFirstChar { it.uppercase() } ?: "VIP User",
                passwordHash = "",
                preferredCurrency = _riskSettings.value?.currencyCode ?: "USD",
                vipTier = "VIP GOLD MEMBER",
                isLoggedIn = true
            )
            _activeUser.value = userEntity
            userDao.logoutAll()
            userDao.insertUser(userEntity)
            Result.success(userEntity)
        } else {
            Result.failure(authResult.exceptionOrNull() ?: Exception("Google Sign-In falló"))
        }
    }

    suspend fun logoutUser() {
        firebaseAuthService.signOut()
        detachFirestoreListeners()
        userDao.logoutAll()
        _activeUser.value = null
    }

    suspend fun wipeAllData() {
        val uid = currentActiveUserId ?: firebaseAuthService.currentFirebaseUser?.uid

        // Clear local room database
        betDao.deleteAllBets()
        _allBets.value = emptyList()

        val currentCurr = _riskSettings.value?.currencyCode ?: "USD"
        val cleanSettings = RiskSettingsEntity(
            id = 1,
            startingBankroll = 50000.0,
            weeklyLossLimit = 3500.0,
            weeklyStakeLimit = 9000.0,
            currencyCode = currentCurr
        )
        riskSettingsDao.insertOrUpdate(cleanSettings)
        _riskSettings.value = cleanSettings

        if (uid != null) {
            try {
                // Delete all bets in Firestore for this user
                val betsRef = firestore.collection("users").document(uid).collection("bets")
                val allDocs = betsRef.get().await()
                if (!allDocs.isEmpty) {
                    val batch = firestore.batch()
                    allDocs.documents.forEach { doc ->
                        batch.delete(doc.reference)
                    }
                    batch.commit().awaitTask()
                }

                // Reset bankroll settings in user doc
                val userDocRef = firestore.collection("users").document(uid)
                val updates = mapOf(
                    "startingBankroll" to 50000.0,
                    "weeklyLossLimit" to 3500.0,
                    "weeklyStakeLimit" to 9000.0,
                    "preferredCurrency" to currentCurr,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                userDocRef.update(updates).awaitTask()
                Log.d(TAG, "Wiped all data in Firestore for user $uid")
            } catch (e: Exception) {
                handleFirestoreError(e, OperationType.WRITE, "users/$uid")
            }
        }
    }
}
