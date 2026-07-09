package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Language
import com.example.data.LocalizedStrings
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel

@Composable
fun EmergencyScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.currentLanguage.collectAsState()
    val sosActive by viewModel.sosAlertTriggered.collectAsState()
    val sosType by viewModel.sosType.collectAsState()

    var showLostReportForm by remember { mutableStateOf(false) }
    var lostItemTitle by remember { mutableStateOf("") }
    var lostItemDesc by remember { mutableStateOf("") }
    var lostItemLocation by remember { mutableStateOf("") }
    var lostItemReportedSuccess by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ElegantDarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- SCREEN TITLE ---
        item {
            Text(
                text = "Smart Stadium Security Hub",
                fontWeight = FontWeight.Bold,
                color = EmergencyCoral,
                fontSize = 18.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // --- GLOWING BIG SOS PANEL ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(
                    width = 2.dp,
                    color = if (sosActive) EmergencyCoral else ElegantDarkBorder
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (sosActive) {
                                Brush.linearGradient(listOf(EmergencyCoral.copy(alpha = 0.2f), ElegantDarkSurfaceEnd))
                            } else {
                                Brush.linearGradient(listOf(ElegantDarkSurfaceStart, ElegantDarkSurfaceEnd))
                            }
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (sosActive) {
                            // SOS Activated State
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(EmergencyCoral),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Active Alarm",
                                    tint = Color.White,
                                    modifier = Modifier.size(54.dp)
                                )
                            }

                            Text(
                                text = "SOS ALARM IN PROGRESS",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = EmergencyCoral,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "First Responders have been notified of your location. Category: ${sosType?.uppercase() ?: "GENERAL EMERGENCY"}.\n\nYour Location: Section 102, Row F, Seat 15.",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )

                            Button(
                                onClick = { viewModel.clearSOS() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Cancel SOS Call", color = Color.White, fontWeight = FontWeight.SemiBold)
                            }
                        } else {
                            // Standard Active State
                            Box(
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(EmergencyCoral, EmergencyCoral.copy(alpha = 0.3f))
                                        )
                                    )
                                    .clickable { viewModel.triggerSOS("Medical Emergency") },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "SOS",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 32.sp
                                )
                            }

                            Text(
                                text = "Hold SOS button for 2 seconds to alert Security and Medical team.",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.5f),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // --- SPECIFIC HAZARD TRIGGER SELECTOR GRID ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Request Specific Dispatch",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    EmergencyGridButton(
                        label = "Medical Squad",
                        icon = Icons.Default.MedicalServices,
                        modifier = Modifier.weight(1f)
                    ) { viewModel.triggerSOS("Medical Emergency") }

                    EmergencyGridButton(
                        label = "Security Guard",
                        icon = Icons.Default.Security,
                        modifier = Modifier.weight(1f)
                    ) { viewModel.triggerSOS("Security Threat") }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    EmergencyGridButton(
                        label = "Fire Hazard",
                        icon = Icons.Default.LocalFireDepartment,
                        modifier = Modifier.weight(1f)
                    ) { viewModel.triggerSOS("Fire Hazard") }

                    EmergencyGridButton(
                        label = "Lost Child",
                        icon = Icons.Default.ChildCare,
                        modifier = Modifier.weight(1f)
                    ) { viewModel.triggerSOS("Lost Child Alert") }
                }
            }
        }

        // --- EMERGENCY ESCAPE EVACUATION PANEL ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ElegantDarkSurface),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, ElegantDarkBorderSubtle)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Exit Route", tint = TurfGreen)
                            Text("Assigned Evacuation Route", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(TurfGreen.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Safe Route", color = TurfGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Text(
                        text = "In the event of an evacuation, head towards **Gate B (West Entrance)**. Take staircase West-3 down directly to street level. Do not use elevators.",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        lineHeight = 18.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Live Coordinates: 40.8135° N, 74.0745° W",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                        Icon(Icons.Default.Share, contentDescription = "Share Location", tint = ElegantDarkPrimary, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // --- LOST & FOUND INCIDENT REGISTRATION ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                colors = CardDefaults.cardColors(containerColor = ElegantDarkSurface),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, ElegantDarkBorderSubtle)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Search, contentDescription = "Lost Items", tint = ElegantDarkSecondary)
                            Text("Lost & Found Center", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                        }

                        IconButton(onClick = { showLostReportForm = !showLostReportForm }) {
                            Icon(
                                imageVector = if (showLostReportForm) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = "Expand",
                                tint = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }

                    if (showLostReportForm) {
                        if (lostItemReportedSuccess) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(TurfGreen.copy(alpha = 0.15f))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "Lost report submitted successfully! AI Image recognition matching is scanning found lockers.",
                                    fontSize = 12.sp,
                                    color = TurfGreen,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        OutlinedTextField(
                            value = lostItemTitle,
                            onValueChange = { lostItemTitle = it },
                            label = { Text("What did you lose? (e.g. Wallet)", color = Color.White.copy(alpha = 0.6f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElegantDarkPrimary,
                                unfocusedBorderColor = ElegantDarkBorderSubtle,
                                focusedLabelColor = ElegantDarkPrimary,
                                unfocusedLabelColor = Color.White.copy(alpha = 0.4f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("lost_item_title"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = lostItemDesc,
                            onValueChange = { lostItemDesc = it },
                            label = { Text("Description (Color, brand, key contents)", color = Color.White.copy(alpha = 0.6f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElegantDarkPrimary,
                                unfocusedBorderColor = ElegantDarkBorderSubtle,
                                focusedLabelColor = ElegantDarkPrimary,
                                unfocusedLabelColor = Color.White.copy(alpha = 0.4f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("lost_item_desc"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = lostItemLocation,
                            onValueChange = { lostItemLocation = it },
                            label = { Text("Last seen location (e.g. Row F restroom)", color = Color.White.copy(alpha = 0.6f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElegantDarkPrimary,
                                unfocusedBorderColor = ElegantDarkBorderSubtle,
                                focusedLabelColor = ElegantDarkPrimary,
                                unfocusedLabelColor = Color.White.copy(alpha = 0.4f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("lost_item_location"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Button(
                            onClick = {
                                if (lostItemTitle.isNotEmpty()) {
                                    viewModel.reportLostItem(lostItemTitle, lostItemDesc, lostItemLocation)
                                    lostItemReportedSuccess = true
                                    lostItemTitle = ""
                                    lostItemDesc = ""
                                    lostItemLocation = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElegantDarkSecondary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("submit_lost_button")
                        ) {
                            Text("Submit Incident Report", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Text(
                            text = "Lost your keys, phone, or wallet? Register an incident directly to stadium security desk. Our AI scanners will cross-match items instantly.",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmergencyGridButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(72.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = ElegantDarkSurface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, ElegantDarkBorderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(EmergencyCoral.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = EmergencyCoral,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color.White,
                lineHeight = 16.sp
            )
        }
    }
}
