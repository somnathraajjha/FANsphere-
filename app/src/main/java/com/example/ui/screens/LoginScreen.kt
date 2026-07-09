package com.example.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Language
import com.example.data.LocalizedStrings
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel
import com.example.viewmodel.PhoneAuthState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.currentLanguage.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val phoneAuthState by viewModel.phoneAuthState.collectAsState()
    val resendCountdown by viewModel.resendCountdown.collectAsState()

    val context = LocalContext.current
    val activity = context as? Activity

    var emailInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var loginMethod by remember { mutableStateOf("PHONE") } // "PHONE" or "EMAIL"
    var showOtpField by remember { mutableStateOf(false) }
    var otpInput by remember { mutableStateOf("") }
    var selectedCountryCode by remember { mutableStateOf("+1") }
    var showCountryDropdown by remember { mutableStateOf(false) }

    // User Profile input states
    var profileComplete by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf("") }
    var ageInput by remember { mutableStateOf("") }
    var genderInput by remember { mutableStateOf("Male") }
    var favTeamInput by remember { mutableStateOf("Argentina 🇦🇷") }

    var showGenderDropdown by remember { mutableStateOf(false) }
    var showTeamDropdown by remember { mutableStateOf(false) }

    val countryCodes = listOf(
        Pair("+1", "USA 🇺🇸"),
        Pair("+44", "UK 🇬🇧"),
        Pair("+91", "IND 🇮🇳"),
        Pair("+52", "MEX 🇲🇽"),
        Pair("+55", "BRA 🇧🇷"),
        Pair("+49", "GER 🇩🇪"),
        Pair("+34", "ESP 🇪🇸")
    )

    val genders = listOf("Male", "Female", "Non-binary", "Prefer not to say")
    val footballTeams = listOf(
        "Argentina 🇦🇷", "Brazil 🇧🇷", "France 🇫🇷", "England 🇬🇧", 
        "Germany 🇩🇪", "Spain 🇪🇸", "India 🇮🇳", "USA 🇺🇸", "Mexico 🇲🇽"
    )

    val scrollState = rememberScrollState()

    // Automatically transition to OTP field when code is sent
    LaunchedEffect(phoneAuthState) {
        if (phoneAuthState is PhoneAuthState.OtpSent) {
            showOtpField = true
        } else if (phoneAuthState is PhoneAuthState.Verified) {
            showOtpField = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        ElegantDarkBg,
                        ElegantDarkSurfaceEnd
                    )
                )
            )
            .imePadding()
            .verticalScroll(scrollState),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .widthIn(max = 500.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (isLoggedIn && !profileComplete) {
                // --- STADIUM FAN PROFILE SETUP ---
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    ElegantDarkSecondary,
                                    ElegantDarkPrimary
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "Profile Setup Logo",
                        modifier = Modifier.size(50.dp),
                        tint = Color.Black
                    )
                }

                Text(
                    text = "STADIUM PROFILE",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = ElegantDarkSecondary,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Personalize your FIFA 2026 matchday with real-time customized alerts and local supporter navigation assistance!",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = ElegantDarkSurface
                    ),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, ElegantDarkBorderSubtle)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Supporter Configuration",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )

                        // Full Name Input
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("profile_name_input"),
                            label = { Text("Full Name") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = "User Icon", tint = ElegantDarkPrimary)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElegantDarkPrimary,
                                unfocusedBorderColor = ElegantDarkBorderSubtle,
                                focusedLabelColor = ElegantDarkPrimary,
                                unfocusedLabelColor = Color.White.copy(alpha = 0.4f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = ElegantDarkSurface,
                                unfocusedContainerColor = ElegantDarkSurface
                            ),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        // Age Input
                        OutlinedTextField(
                            value = ageInput,
                            onValueChange = { ageInput = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("profile_age_input"),
                            label = { Text("Age") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            leadingIcon = {
                                Icon(Icons.Default.CalendarToday, contentDescription = "Calendar Icon", tint = ElegantDarkPrimary)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElegantDarkPrimary,
                                unfocusedBorderColor = ElegantDarkBorderSubtle,
                                focusedLabelColor = ElegantDarkPrimary,
                                unfocusedLabelColor = Color.White.copy(alpha = 0.4f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = ElegantDarkSurface,
                                unfocusedContainerColor = ElegantDarkSurface
                            ),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        // Gender Selector Dropdown
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = genderInput,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("profile_gender_selector")
                                    .clickable { showGenderDropdown = true },
                                label = { Text("Gender") },
                                leadingIcon = {
                                    Icon(Icons.Default.Face, contentDescription = "Gender Icon", tint = ElegantDarkPrimary)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { showGenderDropdown = true }) {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ElegantDarkPrimary,
                                    unfocusedBorderColor = ElegantDarkBorderSubtle,
                                    focusedLabelColor = ElegantDarkPrimary,
                                    unfocusedLabelColor = Color.White.copy(alpha = 0.4f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedContainerColor = ElegantDarkSurface,
                                    unfocusedContainerColor = ElegantDarkSurface
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )

                            DropdownMenu(
                                expanded = showGenderDropdown,
                                onDismissRequest = { showGenderDropdown = false },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(ElegantDarkSurface)
                            ) {
                                genders.forEach { gen ->
                                    DropdownMenuItem(
                                        text = { Text(gen, color = Color.White) },
                                        onClick = {
                                            genderInput = gen
                                            showGenderDropdown = false
                                        }
                                    )
                                }
                            }
                        }

                        // Favorite Football Team Dropdown
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = favTeamInput,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("profile_team_selector")
                                    .clickable { showTeamDropdown = true },
                                label = { Text("Favorite Team") },
                                leadingIcon = {
                                    Icon(Icons.Default.SportsFootball, contentDescription = "Team Icon", tint = ElegantDarkPrimary)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { showTeamDropdown = true }) {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ElegantDarkPrimary,
                                    unfocusedBorderColor = ElegantDarkBorderSubtle,
                                    focusedLabelColor = ElegantDarkPrimary,
                                    unfocusedLabelColor = Color.White.copy(alpha = 0.4f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedContainerColor = ElegantDarkSurface,
                                    unfocusedContainerColor = ElegantDarkSurface
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )

                            DropdownMenu(
                                expanded = showTeamDropdown,
                                onDismissRequest = { showTeamDropdown = false },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(ElegantDarkSurface)
                            ) {
                                footballTeams.forEach { team ->
                                    DropdownMenuItem(
                                        text = { Text(team, color = Color.White) },
                                        onClick = {
                                            favTeamInput = team
                                            showTeamDropdown = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                if (nameInput.trim().isEmpty() || ageInput.trim().isEmpty()) {
                                    Toast.makeText(context, "Please fill in all details!", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.updateProfile(
                                        name = nameInput.trim(),
                                        age = ageInput.trim(),
                                        gender = genderInput,
                                        favTeam = favTeamInput
                                    )
                                    profileComplete = true
                                    Toast.makeText(context, "Welcome back to Stadium, $nameInput!", Toast.LENGTH_LONG).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("save_profile_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElegantDarkPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Save & Enter FanSphere 🚀",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            } else {
                // --- BRAND HEADER ---
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    ElegantDarkSecondary,
                                    ElegantDarkPrimary
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsFootball,
                        contentDescription = "FanSphere Logo",
                        modifier = Modifier.size(50.dp),
                        tint = Color.Black
                    )
                }

                Text(
                    text = LocalizedStrings.get("app_name", language).uppercase(),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp,
                    color = ElegantDarkSecondary,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = LocalizedStrings.get("tagline", language),
                    fontSize = 14.sp,
                    color = ElegantDarkPrimary,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Glassmorphic Card Login Block
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = ElegantDarkSurface
                    ),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, ElegantDarkBorderSubtle),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = LocalizedStrings.get("login_prompt", language),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        // Elegant Tab Selector for Phone/Email Login
                        TabRow(
                            selectedTabIndex = if (loginMethod == "PHONE") 0 else 1,
                            containerColor = Color.Transparent,
                            contentColor = ElegantDarkPrimary,
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[if (loginMethod == "PHONE") 0 else 1]),
                                    color = ElegantDarkPrimary
                                )
                            },
                            divider = {}
                        ) {
                            Tab(
                                selected = loginMethod == "PHONE",
                                onClick = { 
                                    loginMethod = "PHONE" 
                                    showOtpField = false
                                    otpInput = ""
                                },
                                text = { 
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Phone,
                                            contentDescription = "Phone Login",
                                            tint = if (loginMethod == "PHONE") ElegantDarkPrimary else Color.White.copy(alpha = 0.6f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            "Phone", 
                                            fontWeight = FontWeight.Bold, 
                                            fontSize = 13.sp, 
                                            color = if (loginMethod == "PHONE") ElegantDarkPrimary else Color.White.copy(alpha = 0.6f)
                                        ) 
                                    }
                                }
                            )
                            Tab(
                                selected = loginMethod == "EMAIL",
                                onClick = { 
                                    loginMethod = "EMAIL" 
                                    showOtpField = false
                                    otpInput = ""
                                },
                                text = { 
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Email,
                                            contentDescription = "Email Login",
                                            tint = if (loginMethod == "EMAIL") ElegantDarkPrimary else Color.White.copy(alpha = 0.6f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            "Email", 
                                            fontWeight = FontWeight.Bold, 
                                            fontSize = 13.sp, 
                                            color = if (loginMethod == "EMAIL") ElegantDarkPrimary else Color.White.copy(alpha = 0.6f)
                                        ) 
                                    }
                                }
                            )
                        }

                        // Conditional Inputs based on Tab Selection
                        AnimatedVisibility(visible = loginMethod == "PHONE") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Country Code Dropdown Selector
                                Box {
                                    OutlinedButton(
                                        onClick = { showCountryDropdown = true },
                                        modifier = Modifier
                                            .height(56.dp)
                                            .testTag("country_code_button"),
                                        border = BorderStroke(1.dp, ElegantDarkBorderSubtle),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Text(selectedCountryCode, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", modifier = Modifier.size(16.dp))
                                        }
                                    }

                                    DropdownMenu(
                                        expanded = showCountryDropdown,
                                        onDismissRequest = { showCountryDropdown = false },
                                        modifier = Modifier.background(ElegantDarkSurface)
                                    ) {
                                        countryCodes.forEach { (code, name) ->
                                            DropdownMenuItem(
                                                text = { Text("$name ($code)", color = Color.White, fontSize = 13.sp) },
                                                onClick = {
                                                    selectedCountryCode = code
                                                    showCountryDropdown = false
                                                }
                                            )
                                        }
                                    }
                                }

                                // Phone Number Field
                                OutlinedTextField(
                                    value = phoneInput,
                                    onValueChange = { phoneInput = it },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("phone_input"),
                                    label = { Text("Phone Number") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = ElegantDarkPrimary,
                                        unfocusedBorderColor = ElegantDarkBorderSubtle,
                                        focusedLabelColor = ElegantDarkPrimary,
                                        unfocusedLabelColor = Color.White.copy(alpha = 0.4f),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedContainerColor = ElegantDarkSurface,
                                        unfocusedContainerColor = ElegantDarkSurface
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )
                            }
                        }

                        AnimatedVisibility(visible = loginMethod == "EMAIL") {
                            OutlinedTextField(
                                value = emailInput,
                                onValueChange = { emailInput = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("email_input"),
                                label = { Text("Email Address") },
                                leadingIcon = {
                                    Icon(Icons.Default.Email, contentDescription = "Email Icon", tint = ElegantDarkPrimary)
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ElegantDarkPrimary,
                                    unfocusedBorderColor = ElegantDarkBorderSubtle,
                                    focusedLabelColor = ElegantDarkPrimary,
                                    unfocusedLabelColor = Color.White.copy(alpha = 0.4f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedContainerColor = ElegantDarkSurface,
                                    unfocusedContainerColor = ElegantDarkSurface
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        AnimatedVisibility(visible = showOtpField) {
                            OutlinedTextField(
                                value = otpInput,
                                onValueChange = { otpInput = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("otp_input"),
                                label = { Text("6-Digit OTP Code") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ElegantDarkPrimary,
                                    unfocusedBorderColor = ElegantDarkBorderSubtle,
                                    focusedLabelColor = ElegantDarkPrimary,
                                    unfocusedLabelColor = Color.White.copy(alpha = 0.4f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedContainerColor = ElegantDarkSurface,
                                    unfocusedContainerColor = ElegantDarkSurface
                                ),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                        }

                        // State Feedback Row (Loading / Success / Error Alerts)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            when (phoneAuthState) {
                                is PhoneAuthState.SendingOtp -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = ElegantDarkPrimary,
                                        strokeWidth = 2.dp
                                    )
                                    Text("Sending secure OTP via Firebase Auth...", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                                }
                                is PhoneAuthState.OtpSent -> {
                                    Text(
                                        text = "OTP sent successfully! Expires in 5 minutes.",
                                        color = TurfGreen,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Center
                                    )
                                    
                                    Spacer(modifier = Modifier.height(4.dp))
                                    
                                    // 30-Second Resend Countdown Timer
                                    if (resendCountdown > 0) {
                                        Text(
                                            text = "Resend code in ${resendCountdown}s",
                                            color = Color.White.copy(alpha = 0.5f),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    } else {
                                        Text(
                                            text = "Resend OTP Code",
                                            color = ElegantDarkPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                                .clickable {
                                                    if (phoneInput.isNotEmpty() && activity != null) {
                                                        viewModel.sendVerificationCode("$selectedCountryCode$phoneInput", activity)
                                                    }
                                                }
                                                .testTag("resend_otp_button")
                                        )
                                    }
                                }
                                is PhoneAuthState.VerifyingOtp -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = ElegantDarkPrimary,
                                        strokeWidth = 2.dp
                                    )
                                    Text("Verifying verification code...", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                                }
                                is PhoneAuthState.Error -> {
                                    Text(
                                        text = "⚠️ ${(phoneAuthState as PhoneAuthState.Error).message}",
                                        color = Color.Red,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                }
                                else -> {}
                            }
                        }

                        Button(
                            onClick = {
                                if (loginMethod == "PHONE") {
                                    if (!showOtpField) {
                                        if (phoneInput.trim().isEmpty() || phoneInput.length < 7) {
                                            Toast.makeText(context, "Please enter a valid phone number!", Toast.LENGTH_SHORT).show()
                                        } else if (activity != null) {
                                            viewModel.sendVerificationCode("$selectedCountryCode${phoneInput.trim()}", activity)
                                        }
                                    } else {
                                        if (otpInput.trim().length != 6) {
                                            Toast.makeText(context, "Please enter 6-digit OTP code!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            viewModel.verifyVerificationCode(otpInput.trim())
                                        }
                                    }
                                } else {
                                    // Email Authentication with simulated OTP verification
                                    if (!showOtpField) {
                                        if (emailInput.trim().isEmpty() || !emailInput.contains("@")) {
                                            Toast.makeText(context, "Please enter a valid email address!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            viewModel.sendEmailVerificationCode(emailInput.trim())
                                        }
                                    } else {
                                        if (otpInput.trim().isEmpty()) {
                                            Toast.makeText(context, "Please enter verification code!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            viewModel.verifyVerificationCode(otpInput.trim())
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("login_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElegantDarkPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (showOtpField) "Verify & Enter" else "Send OTP Code",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.Black
                            )
                        }

                        HorizontalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 4.dp))

                        // Social Sign Ins row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.login("google_fan@gmail.com") },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp),
                                border = BorderStroke(1.dp, ElegantDarkBorderSubtle),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Google", fontSize = 13.sp, color = Color.White)
                            }

                            OutlinedButton(
                                onClick = { viewModel.login("apple_fan@apple.com") },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp),
                                border = BorderStroke(1.dp, ElegantDarkBorderSubtle),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Apple ID", fontSize = 13.sp, color = Color.White)
                            }
                        }

                        // Guest Enter option
                        Button(
                            onClick = { viewModel.loginAsGuest() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("guest_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElegantDarkSecondary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = LocalizedStrings.get("guest_login", language),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Color.Black
                            )
                        }
                    }
                }

                // Biometric touch target
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { viewModel.loginAsGuest() }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Face ID / Touch ID",
                        tint = ElegantDarkPrimary
                    )
                    Text(
                        text = "Quick Face / Touch ID",
                        color = ElegantDarkPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
