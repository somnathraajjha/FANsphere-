package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Language
import com.example.data.LocalizedStrings
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel
import kotlin.random.Random
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
    viewModel: AppViewModel,
    onNavigateToTab: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val language by viewModel.currentLanguage.collectAsState()
    val isDark by viewModel.isDarkTheme.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()
    val liveMatch by viewModel.liveMatch.collectAsState()
    val parkingZones by viewModel.parkingZones.collectAsState()
    val reservedParking by viewModel.reservedZone.collectAsState()
    val foodCart by viewModel.foodCart.collectAsState()
    val sosAlert by viewModel.sosAlertTriggered.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val userFavTeam by viewModel.userFavTeam.collectAsState()

    var showLangSelector by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ElegantDarkBg),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- HEADER SECTION (Gradients, title, actions) ---
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(ElegantDarkHeaderStart, Color.Transparent)
                        )
                    )
                    .padding(start = 20.dp, end = 20.dp, top = 28.dp, bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (userName.isNotEmpty()) "WELCOME, ${userName.uppercase()}" else "FIFA 2026 • AI SMART STADIUM",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = ElegantDarkPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (userFavTeam.isNotEmpty()) "Supporting $userFavTeam" else "FanSphere",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Black,
                            style = androidx.compose.ui.text.TextStyle(
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                letterSpacing = (-0.5).sp
                            ),
                            color = Color.White
                        )
                    }

                    // Header actions
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Language Switcher
                        IconButton(
                            onClick = { showLangSelector = !showLangSelector },
                            modifier = Modifier
                                .size(38.dp)
                                .background(Color.White.copy(alpha = 0.05f), CircleShape)
                                .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = "Change Language",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Theme Selector
                        IconButton(
                            onClick = { viewModel.toggleTheme() },
                            modifier = Modifier
                                .size(38.dp)
                                .background(Color.White.copy(alpha = 0.05f), CircleShape)
                                .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Toggle Theme",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Logout Button
                        IconButton(
                            onClick = { viewModel.logout() },
                            modifier = Modifier
                                .size(38.dp)
                                .background(Color.White.copy(alpha = 0.05f), CircleShape)
                                .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Logout",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Profile Circle with elegant gradient ring
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(ElegantDarkPrimary, ElegantDarkCyan)
                                    )
                                )
                                .padding(1.5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(ElegantDarkBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (userName.isNotEmpty()) userName.take(2).uppercase() else (userEmail?.take(2)?.uppercase() ?: "FI"),
                                    fontWeight = FontWeight.Bold,
                                    color = ElegantDarkPrimary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- LANGUAGE EXPANDABLE PICKER ---
        item {
            AnimatedVisibility(visible = showLangSelector) {
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = ElegantDarkSurface),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, ElegantDarkBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = LocalizedStrings.get("select_language", language),
                                fontWeight = FontWeight.Bold,
                                color = ElegantDarkPrimary,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(Language.values()) { lang ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (lang == language) ElegantDarkPrimary
                                                else Color.White.copy(alpha = 0.05f)
                                            )
                                            .clickable {
                                                viewModel.setLanguage(lang)
                                                showLangSelector = false
                                            }
                                            .padding(horizontal = 14.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = lang.displayName,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (lang == language) Color.Black else Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- EMERGENCY ACTIVE WARNING HEADER ---
        item {
            AnimatedVisibility(visible = sosAlert) {
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = ElegantDarkEmergency),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Icon(Icons.Default.Warning, contentDescription = "SOS Warning", tint = Color.White)
                                Column {
                                    Text("ACTIVE EMERGENCY DISPATCHED", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                    Text("Responders en route to Sec 102", color = Color.White.copy(alpha = 0.9f), fontSize = 11.sp)
                                }
                            }
                            IconButton(onClick = { viewModel.clearSOS() }) {
                                Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // --- QUICK STATS GRID (Crowd Density & Food Queue) ---
        item {
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Card 1: Crowd Density
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = ElegantDarkCardBg),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, ElegantDarkBorderSubtle)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Groups,
                                    contentDescription = "Crowd Density",
                                    tint = ElegantDarkTertiary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(ElegantDarkTertiary.copy(alpha = 0.1f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "LOW",
                                        color = ElegantDarkTertiary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Crowd Density",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.5f),
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Zone 4: 24%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Card 2: Food Queue
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = ElegantDarkCardBg),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, ElegantDarkBorderSubtle)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Restaurant,
                                    contentDescription = "Food Queue",
                                    tint = ElegantDarkSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(ElegantDarkSecondary.copy(alpha = 0.1f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "8 MIN",
                                        color = ElegantDarkSecondary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Food Queue",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.5f),
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Stall #12",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // --- LIVE MATCH SCOREBOARD CARD ---
        item {
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("live_match_card"),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.dp, ElegantDarkBorder)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(ElegantDarkSurfaceStart, ElegantDarkSurfaceEnd)
                                )
                            )
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            // Match header with "LIVE" blinking light
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    // Animated pulse look
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(ElegantDarkTertiary)
                                    )
                                    Text(
                                        text = "LIVE MATCH • FIFA 2026",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ElegantDarkTertiary,
                                        letterSpacing = 1.sp
                                    )
                                }
                                Text(
                                    text = liveMatch.minute,
                                    fontWeight = FontWeight.Bold,
                                    color = ElegantDarkSecondary,
                                    fontSize = 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Teams Score Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Team A
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.05f))
                                            .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(liveMatch.teamA.flagEmoji, fontSize = 28.sp)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(liveMatch.teamA.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                                    Text(liveMatch.teamA.code, fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
                                }

                                // Score
                                Text(
                                    text = "${liveMatch.scoreA} - ${liveMatch.scoreB}",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    letterSpacing = (-1).sp
                                )

                                // Team B
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.05f))
                                            .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(liveMatch.teamB.flagEmoji, fontSize = 28.sp)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(liveMatch.teamB.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                                    Text(liveMatch.teamB.code, fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Possession Bar
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Possession", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
                                    Text("${liveMatch.possessionA}% vs ${100 - liveMatch.possessionA}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                                Spacer(modifier = Modifier.height(5.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.08f))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(liveMatch.possessionA / 100f)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.horizontalGradient(
                                                    listOf(ElegantDarkPrimary, ElegantDarkCyan)
                                                )
                                            )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Expandable Scorecard / Timeline
                            var isTimelineExpanded by remember { mutableStateOf(true) } // expanded by default so it's super visible!
                            
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.03f))
                                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                                    .clickable { isTimelineExpanded = !isTimelineExpanded }
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text("⚽", fontSize = 14.sp)
                                        Text(
                                            text = "Live Match Events & Goals",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                    Icon(
                                        imageVector = if (isTimelineExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = "Expand Timeline",
                                        tint = ElegantDarkPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                AnimatedVisibility(visible = isTimelineExpanded) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 10.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        liveMatch.timeline.forEach { event ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.Top,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .clip(CircleShape)
                                                        .background(
                                                            when (event.type) {
                                                                "GOAL" -> TurfGreen.copy(alpha = 0.2f)
                                                                "YELLOW" -> Color.Yellow.copy(alpha = 0.2f)
                                                                else -> Color.White.copy(alpha = 0.1f)
                                                            }
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = when (event.type) {
                                                            "GOAL" -> "⚽"
                                                            "YELLOW" -> "🟨"
                                                            "SUB" -> "🔄"
                                                            else -> "📢"
                                                        },
                                                        fontSize = 11.sp
                                                    )
                                                }
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                    ) {
                                                        Text(
                                                            text = "${event.minute}'",
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 11.sp,
                                                            color = ElegantDarkSecondary
                                                        )
                                                        Text(
                                                            text = event.teamCode,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 10.sp,
                                                            color = if (event.teamCode == "ARG") ElegantDarkCyan else Color.Yellow
                                                        )
                                                    }
                                                    Text(
                                                        text = event.description,
                                                        fontSize = 11.sp,
                                                        color = Color.White.copy(alpha = 0.8f)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Divided by a gorgeous gradient line
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(Color.Transparent, Color.White.copy(alpha = 0.12f), Color.Transparent)
                                        )
                                    )
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Bottom row: Lusail Stadium and Semi-Final A
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = "Stadium Location",
                                        tint = Color.White.copy(alpha = 0.6f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = liveMatch.stadium,
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.6f)
                                    )
                                }
                                Text(
                                    text = "Semi-Final A",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ElegantDarkSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- QUICK ACTION SMART HUB GRID ---
        item {
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = LocalizedStrings.get("quick_actions", language),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = ElegantDarkSecondary,
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Navigate Seat
                        QuickHubCard(
                            title = "Seat Navigator",
                            icon = Icons.Default.Directions,
                            tint = ElegantDarkPrimary,
                            description = "Sec 102 Row F",
                            modifier = Modifier.weight(1f)
                        ) { onNavigateToTab("stadium_map") }

                        // Order Food
                        val cartCount = foodCart.values.sum()
                        QuickHubCard(
                            title = "Order Snacks",
                            icon = Icons.Default.Fastfood,
                            tint = ElegantDarkSecondary,
                            description = if (cartCount > 0) "$cartCount Items" else "3 min queue",
                            modifier = Modifier.weight(1f)
                        ) { onNavigateToTab("food_beverage") }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Ticket QR Access
                        QuickHubCard(
                            title = "My Ticket",
                            icon = Icons.Default.QrCodeScanner,
                            tint = ElegantDarkTertiary,
                            description = "VIP Gold Pass",
                            modifier = Modifier.weight(1f)
                        ) { onNavigateToTab("tickets") }

                        // Smart Parking Slot
                        QuickHubCard(
                            title = "Smart Parking",
                            icon = Icons.Default.LocalParking,
                            tint = ElegantDarkPrimary,
                            description = if (reservedParking != null) "Spot reserved" else "Zone A active",
                            modifier = Modifier.weight(1f)
                        ) { onNavigateToTab("stadium_map") }
                    }
                }
            }
        }

        // --- INTERACTIVE FOOTBALL PENALTY CHALLENGE ---
        item {
            InteractiveFootballFieldAnimation()
        }

        // --- COGNITIVE AI CONTEXT CORNER (FanSphere AI) ---
        item {
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToTab("ai_assistant") },
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, ElegantDarkBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(ElegantDarkPrimary, ElegantDarkCyan)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI Companion",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "FanSphere AI",
                                fontWeight = FontWeight.Bold,
                                color = ElegantDarkPrimary,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "\"Gate B is currently less crowded. Want directions?\"",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Input",
                            tint = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // --- BOTTOM PADDING SPACE ---
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun QuickHubCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    description: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = ElegantDarkSurface),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, ElegantDarkBorderSubtle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(tint.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = tint,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Action",
                    tint = Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.size(14.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
                Text(
                    text = description,
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun InteractiveFootballFieldAnimation(
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var goalsCount by remember { mutableStateOf(0) }
    var savesCount by remember { mutableStateOf(0) }
    var targetDirection by remember { mutableStateOf("CENTER") } // "LEFT", "CENTER", "RIGHT"
    var isKicking by remember { mutableStateOf(false) }
    var gameMessage by remember { mutableStateOf("⚽ Tap KICK to Shoot!") }

    val ballX = remember { Animatable(0.5f) }
    val ballY = remember { Animatable(0.85f) }
    val ballScale = remember { Animatable(1.0f) }
    val ballRotation = remember { Animatable(0f) }

    // Goalkeeper moving left-right
    val infiniteTransition = rememberInfiniteTransition(label = "goalie")
    val goalieProgress by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "goalieX"
    )

    val confettiList = remember { mutableStateListOf<ConfettiParticle>() }
    var canvasWidth by remember { mutableStateOf(0f) }
    var canvasHeight by remember { mutableStateOf(0f) }

    // Particle update loop
    LaunchedEffect(confettiList.size) {
        if (confettiList.isNotEmpty()) {
            while (confettiList.isNotEmpty()) {
                delay(16)
                val iterator = confettiList.iterator()
                while (iterator.hasNext()) {
                    val p = iterator.next()
                    p.x += p.vx
                    p.y += p.vy
                    p.vy += 0.25f // gravity
                    p.alpha -= 0.02f
                    if (p.alpha <= 0f || p.y > 600f) {
                        iterator.remove()
                    }
                }
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        colors = CardDefaults.cardColors(containerColor = ElegantDarkSurface),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, ElegantDarkBorderSubtle)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        imageVector = Icons.Default.SportsSoccer,
                        contentDescription = "Soccer Ball",
                        tint = ElegantDarkPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "🏟️ STADIUM FAN ZONE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = ElegantDarkPrimary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Goals: $goalsCount",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElegantDarkSecondary
                    )
                    Text(
                        text = "Saved: $savesCount",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Game Status Overlay Message
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = gameMessage,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        gameMessage.contains("GOAL") -> ElegantDarkPrimary
                        gameMessage.contains("SAVED") -> ElegantDarkEmergency
                        else -> Color.White
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive Canvas (Pitch)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF04140A), // dark forest green
                                Color(0xFF0D3219)  // lush grass green
                            )
                        )
                    )
            ) {
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    canvasWidth = size.width
                    canvasHeight = size.height

                    // 1. Draw Football Pitch Markings (Goal area, box)
                    val strokeWidth = 2.dp.toPx()
                    val linePaint = Stroke(width = strokeWidth)

                    // Penalty box outline
                    drawRect(
                        color = Color.White.copy(alpha = 0.2f),
                        topLeft = Offset(canvasWidth * 0.15f, 0f),
                        size = Size(canvasWidth * 0.7f, canvasHeight * 0.5f),
                        style = linePaint
                    )

                    // Goal area outline (smaller box)
                    drawRect(
                        color = Color.White.copy(alpha = 0.2f),
                        topLeft = Offset(canvasWidth * 0.3f, 0f),
                        size = Size(canvasWidth * 0.4f, canvasHeight * 0.22f),
                        style = linePaint
                    )

                    // Goal post drawing at the top
                    val goalLeft = canvasWidth * 0.32f
                    val goalRight = canvasWidth * 0.68f
                    val goalTop = canvasHeight * 0.15f

                    // Draw Goal Net backing
                    drawRect(
                        color = Color.White.copy(alpha = 0.05f),
                        topLeft = Offset(goalLeft, 0f),
                        size = Size(goalRight - goalLeft, goalTop),
                    )
                    // Drawing net cross-lines
                    for (i in 0..12) {
                        val netX = goalLeft + (goalRight - goalLeft) * (i / 12f)
                        drawLine(
                            color = Color.White.copy(alpha = 0.12f),
                            start = Offset(netX, 0f),
                            end = Offset(netX, goalTop),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Goal post solid bars (white)
                    drawLine(
                        color = Color.White,
                        start = Offset(goalLeft, 0f),
                        end = Offset(goalLeft, goalTop),
                        strokeWidth = 3.2.dp.toPx()
                    )
                    drawLine(
                        color = Color.White,
                        start = Offset(goalRight, 0f),
                        end = Offset(goalRight, goalTop),
                        strokeWidth = 3.2.dp.toPx()
                    )
                    drawLine(
                        color = Color.White,
                        start = Offset(goalLeft, goalTop),
                        end = Offset(goalRight, goalTop),
                        strokeWidth = 3.2.dp.toPx()
                    )

                    // 2. Draw Goalkeeper
                    val gX = canvasWidth * goalieProgress
                    val gY = goalTop - 5.dp.toPx()
                    val goalieRadius = 14.dp.toPx()

                    // Goalkeeper jersey body
                    drawCircle(
                        color = ElegantDarkSecondary,
                        radius = goalieRadius,
                        center = Offset(gX, gY)
                    )
                    // Goalkeeper Head
                    drawCircle(
                        color = Color(0xFFFFCC80),
                        radius = goalieRadius * 0.5f,
                        center = Offset(gX, gY - goalieRadius * 0.8f)
                    )
                    // Gloves (Left & Right)
                    drawCircle(
                        color = Color.White,
                        radius = goalieRadius * 0.3f,
                        center = Offset(gX - goalieRadius * 1.2f, gY)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = goalieRadius * 0.3f,
                        center = Offset(gX + goalieRadius * 1.2f, gY)
                    )

                    // 3. Draw Football (Animated position and scale)
                    val bX = canvasWidth * ballX.value
                    val bY = canvasHeight * ballY.value
                    val bRadius = 18.dp.toPx() * ballScale.value

                    // Draw soccer ball backing
                    drawCircle(
                        color = Color.White,
                        radius = bRadius,
                        center = Offset(bX, bY)
                    )

                    // Drawing beautiful classic pentagon-style football pattern
                    val centerOffset = Offset(bX, bY)
                    val pentagonRadius = bRadius * 0.45f
                    val angleOffset = ballRotation.value * (Math.PI / 180f)

                    for (i in 0 until 5) {
                        val angle = (i * 2 * Math.PI / 5) + angleOffset
                        val px = bX + (pentagonRadius * Math.cos(angle)).toFloat()
                        val py = bY + (pentagonRadius * Math.sin(angle)).toFloat()
                        drawLine(
                            color = Color.Black,
                            start = centerOffset,
                            end = Offset(px, py),
                            strokeWidth = 1.5.dp.toPx()
                        )
                        val outerAngle = angle + (Math.PI / 5)
                        val ox = bX + (bRadius * Math.cos(outerAngle)).toFloat()
                        val oy = bY + (bRadius * Math.sin(outerAngle)).toFloat()
                        drawLine(
                            color = Color.Black,
                            start = Offset(px, py),
                            end = Offset(ox, oy),
                            strokeWidth = 1.5.dp.toPx()
                        )
                    }

                    // 4. Draw Confetti Particles
                    confettiList.forEach { p ->
                        drawCircle(
                            color = p.color.copy(alpha = p.alpha),
                            radius = p.size,
                            center = Offset(p.x, p.y)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Game controls (Directions to target & Kick Button)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Direction Selection Tabs
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val directions = listOf("LEFT", "CENTER", "RIGHT")
                    directions.forEach { dir ->
                        val isSelected = targetDirection == dir
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) ElegantDarkPrimary.copy(alpha = 0.2f)
                                    else Color.Transparent
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) ElegantDarkPrimary else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable(enabled = !isKicking) {
                                    targetDirection = dir
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = dir,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) ElegantDarkPrimary else Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                // SHOOT / RESET BUTTON
                Button(
                    onClick = {
                        if (!isKicking) {
                            isKicking = true
                            coroutineScope.launch {
                                gameMessage = "🏃‍♂️ Preparing shot..."
                                delay(300)

                                val targetX = when (targetDirection) {
                                    "LEFT" -> Random.nextFloat() * 0.08f + 0.33f // Left goal section
                                    "RIGHT" -> Random.nextFloat() * 0.08f + 0.59f // Right goal section
                                    else -> Random.nextFloat() * 0.1f + 0.45f // Center goal section
                                }
                                val targetY = 0.15f // Goal line

                                // Play high-fidelity kick sound/animation
                                launch {
                                    ballX.animateTo(
                                        targetValue = targetX,
                                        animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing)
                                    )
                                }
                                launch {
                                    ballY.animateTo(
                                        targetValue = targetY,
                                        animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing)
                                    )
                                }
                                launch {
                                    ballScale.animateTo(
                                        targetValue = 0.38f,
                                        animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing)
                                    )
                                }
                                launch {
                                    ballRotation.animateTo(
                                        targetValue = 900f,
                                        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
                                    )
                                }

                                delay(500)

                                // Check save or goal!
                                val finalGoalieX = goalieProgress
                                val isSaved = Math.abs(targetX - finalGoalieX) < 0.08f

                                if (isSaved) {
                                    savesCount++
                                    gameMessage = "🧤 SAVED! Keeper blocked it!"
                                } else {
                                    goalsCount++
                                    gameMessage = "⚽ GOOOAAAL!!! Unbelievable finish!"

                                    // Spawn exploding confetti particles!
                                    val colors = listOf(ElegantDarkPrimary, ElegantDarkSecondary, ElegantDarkCyan, Color.White)
                                    for (i in 0 until 40) {
                                        confettiList.add(
                                            ConfettiParticle(
                                                x = targetX * canvasWidth,
                                                y = targetY * canvasHeight,
                                                vx = (Random.nextFloat() - 0.5f) * 16f,
                                                vy = (Random.nextFloat() - 0.85f) * 16f,
                                                color = colors.random(),
                                                size = (Random.nextFloat() * 5f + 4f)
                                            )
                                        )
                                    }
                                }

                                delay(2200)
                                gameMessage = "⚽ Tap KICK to Shoot!"
                                ballX.snapTo(0.5f)
                                ballY.snapTo(0.85f)
                                ballScale.snapTo(1.0f)
                                ballRotation.snapTo(0f)
                                isKicking = false
                            }
                        }
                    },
                    enabled = !isKicking,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElegantDarkPrimary,
                        disabledContainerColor = Color.White.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = if (isKicking) "Kicking..." else "KICK!",
                        fontWeight = FontWeight.Bold,
                        color = if (isKicking) Color.White.copy(alpha = 0.4f) else Color.Black,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

data class ConfettiParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val color: Color,
    val size: Float,
    var alpha: Float = 1f
)
