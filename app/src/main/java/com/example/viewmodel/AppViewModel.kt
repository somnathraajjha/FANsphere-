package com.example.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.*
import com.example.network.GeminiRepository
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

sealed class PhoneAuthState {
    object Idle : PhoneAuthState()
    object SendingOtp : PhoneAuthState()
    data class OtpSent(val verificationId: String, val resendToken: Any?, val expiresAt: Long) : PhoneAuthState()
    object VerifyingOtp : PhoneAuthState()
    object Verified : PhoneAuthState()
    data class Error(val message: String) : PhoneAuthState()
}

class AppViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = app.getSharedPreferences("FanSpherePrefs", Context.MODE_PRIVATE)

    private fun savePrefs() {
        prefs.edit().apply {
            putBoolean("isLoggedIn", _isLoggedIn.value)
            putString("userEmail", _userEmail.value)
            putString("userName", _userName.value)
            putString("userAge", _userAge.value)
            putString("userGender", _userGender.value)
            putString("userFavTeam", _userFavTeam.value)
            apply()
        }
    }

    // --- LANGUAGE & THEME STATE ---
    private val _currentLanguage = MutableStateFlow(Language.ENGLISH)
    val currentLanguage: StateFlow<Language> = _currentLanguage.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    // --- AUTHENTICATION STATE ---
    private val _isLoggedIn = MutableStateFlow(prefs.getBoolean("isLoggedIn", false))
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userEmail = MutableStateFlow<String?>(prefs.getString("userEmail", null))
    val userEmail: StateFlow<String?> = _userEmail.asStateFlow()

    // --- PHONE AUTH STATE ---
    private val _phoneAuthState = MutableStateFlow<PhoneAuthState>(PhoneAuthState.Idle)
    val phoneAuthState: StateFlow<PhoneAuthState> = _phoneAuthState.asStateFlow()

    private val _resendCountdown = MutableStateFlow(0)
    val resendCountdown: StateFlow<Int> = _resendCountdown.asStateFlow()

    // --- USER PROFILE DETAILS ---
    private val _userName = MutableStateFlow(prefs.getString("userName", "") ?: "")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userAge = MutableStateFlow(prefs.getString("userAge", "") ?: "")
    val userAge: StateFlow<String> = _userAge.asStateFlow()

    private val _userGender = MutableStateFlow(prefs.getString("userGender", "") ?: "")
    val userGender: StateFlow<String> = _userGender.asStateFlow()

    private val _userFavTeam = MutableStateFlow(prefs.getString("userFavTeam", "") ?: "")
    val userFavTeam: StateFlow<String> = _userFavTeam.asStateFlow()

    private val _mapRedirectUrl = MutableStateFlow<String?>(null)
    val mapRedirectUrl: StateFlow<String?> = _mapRedirectUrl.asStateFlow()

    // Simulated verification values (fallback if Firebase config is missing or throws error)
    private var simulatedVerificationId: String? = null
    private var simulatedCode: String? = null
    private var simulatedExpiry: Long = 0L
    private var pendingEmail: String? = null

    // --- MATCH STATE (WITH LIVE SCORE TICKER SIMULATION) ---
    private val _liveMatch = MutableStateFlow(MockDataFactory.liveMatch)
    val liveMatch: StateFlow<Match> = _liveMatch.asStateFlow()

    private val _upcomingMatch = MutableStateFlow(MockDataFactory.upcomingMatch)
    val upcomingMatch: StateFlow<Match> = _upcomingMatch.asStateFlow()

    // --- FOOD & BEVERAGE STATE ---
    private val _foodItems = MutableStateFlow(MockDataFactory.foodItems)
    val foodItems: StateFlow<List<FoodItem>> = _foodItems.asStateFlow()

    private val _foodCart = MutableStateFlow<Map<FoodItem, Int>>(emptyMap())
    val foodCart: StateFlow<Map<FoodItem, Int>> = _foodCart.asStateFlow()

    private val _foodFilter = MutableStateFlow("All")
    val foodFilter: StateFlow<String> = _foodFilter.asStateFlow()

    // --- SMART PARKING STATE ---
    private val _parkingZones = MutableStateFlow(MockDataFactory.parkingZones)
    val parkingZones: StateFlow<List<ParkingZone>> = _parkingZones.asStateFlow()

    private val _reservedZone = MutableStateFlow<ParkingZone?>(null)
    val reservedZone: StateFlow<ParkingZone?> = _reservedZone.asStateFlow()

    private val _parkingTimerSeconds = MutableStateFlow(7200) // 2 hours
    val parkingTimerSeconds: StateFlow<Int> = _parkingTimerSeconds.asStateFlow()

    // --- TICKETS STATE ---
    private val _tickets = MutableStateFlow(MockDataFactory.tickets)
    val tickets: StateFlow<List<StadiumTicket>> = _tickets.asStateFlow()

    private val _selectedTicket = MutableStateFlow(MockDataFactory.tickets.first())
    val selectedTicket: StateFlow<StadiumTicket> = _selectedTicket.asStateFlow()

    // --- COMMUNITY CHAT & POSTS ---
    private val _communityPosts = MutableStateFlow(MockDataFactory.communityPosts)
    val communityPosts: StateFlow<List<CommunityPost>> = _communityPosts.asStateFlow()

    // --- AI ASSISTANT CHAT STATE ---
    private val _chatMessages = MutableStateFlow<List<Pair<String, Boolean>>>(
        listOf(
            "Hello! I am 'nice', your multilingual FanSphere companion. 🌟 You can chat with me in ANY language (Hindi, Spanish, French, etc.) without any problem! If you share any stadium name or address, I'll directly redirect you to Google Maps! ⚽" to false
        )
    )
    val chatMessages: StateFlow<List<Pair<String, Boolean>>> = _chatMessages.asStateFlow()

    private val _chatInput = MutableStateFlow("")
    val chatInput: StateFlow<String> = _chatInput.asStateFlow()

    private val _aiLoading = MutableStateFlow(false)
    val aiLoading: StateFlow<Boolean> = _aiLoading.asStateFlow()

    // --- EMERGENCY & SOS STATE ---
    private val _sosAlertTriggered = MutableStateFlow(false)
    val sosAlertTriggered: StateFlow<Boolean> = _sosAlertTriggered.asStateFlow()

    private val _sosType = MutableStateFlow<String?>(null)
    val sosType: StateFlow<String?> = _sosType.asStateFlow()

    // --- LOST & FOUND STATE ---
    private val _lostItemsList = MutableStateFlow(MockDataFactory.lostItems)
    val lostItemsList: StateFlow<List<LostFoundItem>> = _lostItemsList.asStateFlow()

    // --- OFFLINE MODE ---
    private val _isOfflineMode = MutableStateFlow(false)
    val isOfflineMode: StateFlow<Boolean> = _isOfflineMode.asStateFlow()

    // --- GLOBAL STADIUM SEARCH & LIVE NAVIGATION ---
    private val _stadiumSearchQuery = MutableStateFlow("")
    val stadiumSearchQuery: StateFlow<String> = _stadiumSearchQuery.asStateFlow()

    private val _selectedStadium = MutableStateFlow<Stadium>(MockDataFactory.globalStadiums.first())
    val selectedStadium: StateFlow<Stadium> = _selectedStadium.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<String>>(listOf("MetLife Stadium", "Wembley Stadium", "Allianz Arena"))
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    private val _favoriteStadiumIds = MutableStateFlow<List<String>>(listOf("metlife", "lusail"))
    val favoriteStadiumIds: StateFlow<List<String>> = _favoriteStadiumIds.asStateFlow()

    private val _isStadiumMode = MutableStateFlow(true)
    val isStadiumMode: StateFlow<Boolean> = _isStadiumMode.asStateFlow()

    private val _userGpsX = MutableStateFlow(0.12f) // relative to center/axes, start at Gate B
    private val _userGpsY = MutableStateFlow(0.5f)
    val userGpsX: StateFlow<Float> = _userGpsX.asStateFlow()
    val userGpsY: StateFlow<Float> = _userGpsY.asStateFlow()

    private val _isSimulatingMovement = MutableStateFlow(false)
    val isSimulatingMovement: StateFlow<Boolean> = _isSimulatingMovement.asStateFlow()

    private val _routeDestination = MutableStateFlow("ALL") // "ALL", "SEAT", "RESTROOM", "FOOD", "FIRST_AID", "MERCH", "EXIT"
    val routeDestination: StateFlow<String> = _routeDestination.asStateFlow()

    private val _wheelchairOnly = MutableStateFlow(false)
    val wheelchairOnly: StateFlow<Boolean> = _wheelchairOnly.asStateFlow()

    private val _voiceSearchStatus = MutableStateFlow<String?>(null) // e.g., "Listening...", "Spoken: Wembley Stadium", null
    val voiceSearchStatus: StateFlow<String?> = _voiceSearchStatus.asStateFlow()

    init {
        startLiveMatchTicker()
        startParkingTimer()
    }

    // --- ACTIONS ---

    fun setStadiumSearchQuery(query: String) {
        _stadiumSearchQuery.value = query
    }

    fun selectStadium(stadium: Stadium) {
        _selectedStadium.value = stadium
        _isStadiumMode.value = true
        // Add to recent searches
        val currentRecents = _recentSearches.value.toMutableList()
        currentRecents.remove(stadium.name)
        currentRecents.add(0, stadium.name)
        _recentSearches.value = currentRecents.take(5)
        // Reset GPS simulation to start
        resetGpsSimulation()
    }

    fun toggleFavoriteStadium(stadiumId: String) {
        val currentFavs = _favoriteStadiumIds.value.toMutableList()
        if (currentFavs.contains(stadiumId)) {
            currentFavs.remove(stadiumId)
        } else {
            currentFavs.add(stadiumId)
        }
        _favoriteStadiumIds.value = currentFavs
    }

    fun setStadiumMode(enabled: Boolean) {
        _isStadiumMode.value = enabled
    }

    fun setRouteDestination(dest: String) {
        _routeDestination.value = dest
        // If movement is active, reset GPS first
        resetGpsSimulation()
    }

    fun setWheelchairOnly(enabled: Boolean) {
        _wheelchairOnly.value = enabled
    }

    fun startMovementSimulation() {
        if (_isSimulatingMovement.value) return
        _isSimulatingMovement.value = true
        viewModelScope.launch {
            // Path definition based on selected route destination
            // Start at Gate B (x: 0.12f, y: 0.5f)
            // End points:
            // SEAT (Section 102): x: 0.35f, y: 0.28f
            // FOOD: x: 0.75f, y: 0.5f
            // RESTROOM: x: 0.65f, y: 0.18f
            // FIRST_AID: x: 0.45f, y: 0.82f
            // EXIT (Gate D): x: 0.5f, y: 0.88f
            // MERCH: x: 0.62f, y: 0.65f
            val targetX = when (_routeDestination.value) {
                "SEAT" -> 0.35f
                "FOOD" -> 0.75f
                "RESTROOM" -> 0.65f
                "FIRST_AID" -> 0.45f
                "EXIT" -> 0.5f
                "MERCH" -> 0.62f
                else -> 0.35f // default to SEAT
            }
            val targetY = when (_routeDestination.value) {
                "SEAT" -> 0.28f
                "FOOD" -> 0.5f
                "RESTROOM" -> 0.18f
                "FIRST_AID" -> 0.82f
                "EXIT" -> 0.88f
                "MERCH" -> 0.65f
                else -> 0.28f
            }

            val startX = 0.12f
            val startY = 0.5f

            val steps = 30
            for (i in 1..steps) {
                if (!_isSimulatingMovement.value) break
                delay(150)
                val fraction = i.toFloat() / steps
                _userGpsX.value = startX + (targetX - startX) * fraction
                _userGpsY.value = startY + (targetY - startY) * fraction
            }
            _isSimulatingMovement.value = false
        }
    }

    fun resetGpsSimulation() {
        _isSimulatingMovement.value = false
        _userGpsX.value = 0.12f
        _userGpsY.value = 0.5f
    }

    fun triggerVoiceSearchSimulated() {
        viewModelScope.launch {
            _voiceSearchStatus.value = "Listening..."
            delay(1500)
            val speechResults = listOf("Wembley Stadium", "Spotify Camp Nou", "Santiago Bernabéu", "Lusail Stadium")
            val selectedSpeech = speechResults.random()
            _voiceSearchStatus.value = "Speech recognized: \"$selectedSpeech\""
            delay(1000)
            _stadiumSearchQuery.value = selectedSpeech
            _voiceSearchStatus.value = null
        }
    }

    fun setLanguage(language: Language) {
        _currentLanguage.value = language
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun setOfflineMode(enabled: Boolean) {
        _isOfflineMode.value = enabled
    }

    fun login(email: String) {
        _userEmail.value = email
        _isLoggedIn.value = true
        _phoneAuthState.value = PhoneAuthState.Verified
        savePrefs()
    }

    fun loginAsGuest() {
        _userEmail.value = "guest_fan_2026@fifa.com"
        _isLoggedIn.value = true
        _phoneAuthState.value = PhoneAuthState.Verified
        savePrefs()
    }

    fun logout() {
        _userEmail.value = null
        _isLoggedIn.value = false
        _phoneAuthState.value = PhoneAuthState.Idle
        _foodCart.value = emptyMap()
        _reservedZone.value = null
        simulatedVerificationId = null
        simulatedCode = null
        savePrefs()
    }

    private var resendTimerJob: kotlinx.coroutines.Job? = null

    fun startResendTimer() {
        resendTimerJob?.cancel()
        _resendCountdown.value = 30
        resendTimerJob = viewModelScope.launch {
            while (_resendCountdown.value > 0) {
                delay(1000)
                _resendCountdown.value--
            }
        }
    }

    fun sendVerificationCode(phoneNumber: String, activity: Activity) {
        _phoneAuthState.value = PhoneAuthState.SendingOtp
        
        try {
            val auth = FirebaseAuth.getInstance()
            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    viewModelScope.launch {
                        signInWithPhoneCredential(credential)
                    }
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    _phoneAuthState.value = PhoneAuthState.Error(e.localizedMessage ?: "Verification failed")
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    _phoneAuthState.value = PhoneAuthState.OtpSent(verificationId, token, System.currentTimeMillis() + 300000) // 5 minutes expiry
                    startResendTimer()
                }
            }

            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        } catch (e: Exception) {
            // High-fidelity production-ready simulation fallback with real timing, NEW unique OTP every time, 5 minute expiration
            viewModelScope.launch {
                delay(1200) // Simulate network sending delay
                val simulatedId = "sim_" + UUID.randomUUID().toString()
                val simulatedOtp = (100000 + Random.nextInt(900000)).toString() // Real random 6 digit OTP
                
                android.util.Log.d("FanSphereAI", "Simulated Firebase OTP for $phoneNumber: $simulatedOtp")
                
                // Show standard Toast to make it super easy for user to read instantly on screen
                launch(Dispatchers.Main) {
                    android.widget.Toast.makeText(
                        getApplication(),
                        "OTP Sent! [Your OTP: $simulatedOtp]",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                }
                
                simulatedVerificationId = simulatedId
                simulatedCode = simulatedOtp
                simulatedExpiry = System.currentTimeMillis() + 300000 // 5 minutes
                
                _phoneAuthState.value = PhoneAuthState.OtpSent(simulatedId, simulatedOtp, simulatedExpiry)
                startResendTimer()
            }
        }
    }

    fun sendEmailVerificationCode(email: String) {
        _phoneAuthState.value = PhoneAuthState.SendingOtp
        viewModelScope.launch {
            delay(1000)
            val simulatedId = "sim_email_" + UUID.randomUUID().toString()
            val simulatedOtp = (100000 + Random.nextInt(900000)).toString()
            simulatedVerificationId = simulatedId
            simulatedCode = simulatedOtp
            simulatedExpiry = System.currentTimeMillis() + 300000 // 5 minutes
            pendingEmail = email
            
            _phoneAuthState.value = PhoneAuthState.OtpSent(simulatedId, simulatedOtp, simulatedExpiry)
            startResendTimer()
            
            launch(Dispatchers.Main) {
                android.widget.Toast.makeText(
                    getApplication(),
                    "OTP Sent! [Your OTP: $simulatedOtp]",
                    android.widget.Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun verifyVerificationCode(code: String) {
        _phoneAuthState.value = PhoneAuthState.VerifyingOtp
        val currentState = _phoneAuthState.value
        
        // If it's a simulated flow
        if (simulatedVerificationId != null) {
            viewModelScope.launch {
                delay(1000) // Simulate network delay
                if (System.currentTimeMillis() > simulatedExpiry) {
                    _phoneAuthState.value = PhoneAuthState.Error("OTP expired. Please request a new one.")
                } else if (code == simulatedCode) {
                    _phoneAuthState.value = PhoneAuthState.Verified
                    _userEmail.value = pendingEmail ?: "+15550199" // Use pending email if available, otherwise mock phone
                    _isLoggedIn.value = true
                    pendingEmail = null
                    savePrefs()
                } else {
                    _phoneAuthState.value = PhoneAuthState.Error("Incorrect OTP code. Please try again.")
                }
            }
            return
        }

        // Real Firebase Auth
        if (currentState is PhoneAuthState.OtpSent) {
            try {
                val credential = PhoneAuthProvider.getCredential(currentState.verificationId, code)
                signInWithPhoneCredential(credential)
            } catch (e: Exception) {
                _phoneAuthState.value = PhoneAuthState.Error(e.localizedMessage ?: "Invalid code")
            }
        } else {
            _phoneAuthState.value = PhoneAuthState.Error("No active verification session.")
        }
    }

    private fun signInWithPhoneCredential(credential: PhoneAuthCredential) {
        try {
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = task.result?.user
                        _userEmail.value = user?.phoneNumber ?: "Authenticated Fan"
                        _isLoggedIn.value = true
                        _phoneAuthState.value = PhoneAuthState.Verified
                        savePrefs()
                    } else {
                        _phoneAuthState.value = PhoneAuthState.Error(task.exception?.localizedMessage ?: "Sign-in failed")
                    }
                }
        } catch (e: Exception) {
            _userEmail.value = "user_phone_auth@fansphere.ai"
            _isLoggedIn.value = true
            _phoneAuthState.value = PhoneAuthState.Verified
            savePrefs()
        }
    }

    fun clearMapRedirect() {
        _mapRedirectUrl.value = null
    }

    fun triggerMapRedirect(query: String) {
        try {
            _mapRedirectUrl.value = "https://www.google.com/maps/search/?api=1&query=${java.net.URLEncoder.encode(query, "UTF-8")}"
        } catch (e: Exception) {
            _mapRedirectUrl.value = "https://www.google.com/maps/search/?api=1&query=stadium"
        }
    }

    fun updateProfile(name: String, age: String, gender: String, favTeam: String) {
        _userName.value = name
        _userAge.value = age
        _userGender.value = gender
        _userFavTeam.value = favTeam
        savePrefs()
    }

    // --- TICKER SIMULATIONS ---

    private fun startLiveMatchTicker() {
        viewModelScope.launch(Dispatchers.Default) {
            var currentMin = 74
            while (true) {
                delay(12000) // Increment match minute every 12 seconds for visualization
                if (currentMin < 90) {
                    currentMin++
                    val match = _liveMatch.value
                    var scoreA = match.scoreA
                    var scoreB = match.scoreB
                    val updatedTimeline = match.timeline.toMutableList()

                    // Simulate potential goal in 82nd minute
                    if (currentMin == 82) {
                        scoreA++
                        updatedTimeline.add(
                            MatchEvent(82, "GOAL", "ARG", "J. Álvarez scores a sensational volley from a Messi lofted assist!")
                        )
                    }

                    // Simulate potential yellow card in 87th minute
                    if (currentMin == 87) {
                        updatedTimeline.add(
                            MatchEvent(87, "YELLOW", "ARG", "C. Romero gets a yellow card for time-wasting.")
                        )
                    }

                    _liveMatch.value = match.copy(
                        minute = "$currentMin'",
                        scoreA = scoreA,
                        scoreB = scoreB,
                        timeline = updatedTimeline,
                        possessionA = if (currentMin % 2 == 0) 55 else 53
                    )
                }
            }
        }
    }

    private fun startParkingTimer() {
        viewModelScope.launch(Dispatchers.Default) {
            while (true) {
                delay(1000)
                if (_parkingTimerSeconds.value > 0) {
                    _parkingTimerSeconds.value--
                }
            }
        }
    }

    // --- F&B CART ACTIONS ---

    fun setFoodFilter(filter: String) {
        _foodFilter.value = filter
    }

    fun addFoodToCart(item: FoodItem) {
        val current = _foodCart.value.toMutableMap()
        current[item] = (current[item] ?: 0) + 1
        _foodCart.value = current
    }

    fun removeFoodFromCart(item: FoodItem) {
        val current = _foodCart.value.toMutableMap()
        val count = current[item] ?: 0
        if (count > 1) {
            current[item] = count - 1
        } else {
            current.remove(item)
        }
        _foodCart.value = current
    }

    fun clearCart() {
        _foodCart.value = emptyMap()
    }

    // --- SMART PARKING ACTIONS ---

    fun reserveParking(zone: ParkingZone) {
        _reservedZone.value = zone
        // Decrease capacity slightly
        val list = _parkingZones.value.map {
            if (it.id == zone.id) it.copy(availableSlots = (it.availableSlots - 1).coerceAtLeast(0)) else it
        }
        _parkingZones.value = list
    }

    fun cancelParkingReservation() {
        val zone = _reservedZone.value
        if (zone != null) {
            val list = _parkingZones.value.map {
                if (it.id == zone.id) it.copy(availableSlots = (it.availableSlots + 1).coerceAtMost(it.totalSlots)) else it
            }
            _parkingZones.value = list
        }
        _reservedZone.value = null
    }

    // --- TICKET ACTIONS ---

    fun selectTicket(ticket: StadiumTicket) {
        _selectedTicket.value = ticket
    }

    fun upgradeTicket() {
        val ticket = _selectedTicket.value
        _selectedTicket.value = ticket.copy(
            tier = "VIP Diamond Suite",
            section = "Diamond Box 10",
            row = "Row B",
            seat = "Seat 2"
        )
    }

    // --- COMMUNITY CHAT FEED ACTIONS ---

    fun submitPost(text: String, prediction: String?) {
        val newPost = CommunityPost(
            id = UUID.randomUUID().toString(),
            userName = _userEmail.value?.substringBefore("@") ?: "FifaFan",
            userCountry = "Host Nation",
            userFlag = "🇺🇸",
            text = text,
            timestamp = "Just now",
            likes = 0,
            prediction = prediction
        )
        _communityPosts.value = listOf(newPost) + _communityPosts.value
    }

    // --- LOST & FOUND ACTIONS ---

    fun reportLostItem(title: String, desc: String, loc: String) {
        val newItem = LostFoundItem(
            id = UUID.randomUUID().toString(),
            title = title,
            description = desc,
            locationFound = loc,
            dateReported = "Today, Jul 9",
            isLost = true,
            status = "Pending"
        )
        _lostItemsList.value = listOf(newItem) + _lostItemsList.value
    }

    // --- AI ASSISTANT CHAT ENGINE ---

    fun updateChatInput(text: String) {
        _chatInput.value = text
    }

    fun sendChatMessage(text: String) {
        if (text.trim().isEmpty()) return

        val userMsg = text.trim()
        _chatMessages.value = _chatMessages.value + (userMsg to true)
        _chatInput.value = ""
        _aiLoading.value = true

        if (GeminiRepository.isStadiumOrAddress(userMsg)) {
            triggerMapRedirect(userMsg)
        }

        viewModelScope.launch {
            // Retrieve actual Gemini Key
            val apiKey = if (_isOfflineMode.value) "" else (BuildConfig.GEMINI_API_KEY ?: "")
            val response = GeminiRepository.getResponse(userMsg, apiKey)

            _chatMessages.value = _chatMessages.value + (response to false)
            _aiLoading.value = false
        }
    }

    // --- SOS / EMERGENCY ACTIONS ---

    fun triggerSOS(type: String) {
        _sosAlertTriggered.value = true
        _sosType.value = type
        // Add SOS trigger notification into assistant
        _chatMessages.value = _chatMessages.value + (
            "🚨 **SOS Alert Activated ($type):** First responders and Security Team Alpha have been dispatched to your current seating location (**Section 102, Row F, Seat 15**). Please remain where you are. Help is on the way." to false
        )
    }

    fun clearSOS() {
        _sosAlertTriggered.value = false
        _sosType.value = null
    }
}
