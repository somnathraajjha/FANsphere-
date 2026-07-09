package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Language
import com.example.data.LocalizedStrings
import com.example.data.StadiumTicket
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel

@Composable
fun TicketScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.currentLanguage.collectAsState()
    val tickets by viewModel.tickets.collectAsState()
    val selectedTicket by viewModel.selectedTicket.collectAsState()

    var upgradeSuccess by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ElegantDarkBg)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- MULTIPLE TICKETS SWITCHER ---
        Text(
            text = "Your Matchday Passports",
            fontWeight = FontWeight.Bold,
            color = ElegantDarkSecondary,
            fontSize = 15.sp,
            modifier = Modifier.fillMaxWidth()
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tickets) { ticket ->
                val isSelected = selectedTicket.id == ticket.id
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) ElegantDarkPrimary
                            else ElegantDarkSurface
                        )
                        .border(
                            1.dp,
                            if (isSelected) Color.Transparent else ElegantDarkBorderSubtle,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            viewModel.selectTicket(ticket)
                            upgradeSuccess = false
                        }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "${ticket.teamAEmoji} vs ${ticket.teamBEmoji}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.Black else Color.White
                        )
                    }
                }
            }
        }

        // --- THE DIGITAL TICKETS CARD (With Perforated Tear off style!) ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .testTag("ticket_card"),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, ElegantDarkBorder)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(ElegantDarkSurfaceStart, ElegantDarkSurfaceEnd)
                        )
                    )
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Top header
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(ElegantDarkSecondary.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = selectedTicket.tier.uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ElegantDarkSecondary
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.WifiOff, contentDescription = "Offline Cache Ready", tint = ElegantDarkTertiary, modifier = Modifier.size(12.dp))
                                Text("OFFLINE TICKET READY", color = ElegantDarkTertiary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Text(
                            text = selectedTicket.matchTitle,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column {
                                Text("Stadium Venue", fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f))
                                Text("MetLife Stadium, NJ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Column {
                                Text("Date & Time", fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f))
                                Text("${selectedTicket.date} • ${selectedTicket.time}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }

                    // Perforated line drawn via Canvas
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                        ) {
                            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            drawLine(
                                color = Color.White.copy(alpha = 0.12f),
                                start = Offset(0f, size.height / 2),
                                end = Offset(size.width, size.height / 2),
                                pathEffect = pathEffect,
                                strokeWidth = 3f
                            )
                        }
                    }

                    // Bottom barcode/QR and seat information
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Seat details grid row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Gate Access", fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f))
                                Text(selectedTicket.gate.substringBefore(" ("), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Seating Section", fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f))
                                Text(selectedTicket.section.replace("VIP ", ""), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Row & Seat", fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f))
                                Text("${selectedTicket.row} • ${selectedTicket.seat}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        // Rotating QR code representation
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                // Draw nice-looking cyber QR grid pattern
                                val dotsCount = 10
                                val spacing = size.width / dotsCount
                                for (i in 0 until dotsCount) {
                                    for (j in 0 until dotsCount) {
                                        val isFilled = (i + j) % 2 == 0 || (i < 3 && j < 3) || (i > 6 && j < 3) || (i < 3 && j > 6)
                                        if (isFilled) {
                                            drawRect(
                                                color = Color.Black,
                                                topLeft = Offset(i * spacing + 2f, j * spacing + 2f),
                                                size = androidx.compose.ui.geometry.Size(spacing - 4f, spacing - 4f)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Text(
                            text = "SECURE ENTRY CHIP • ROTATING QR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElegantDarkPrimary,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }

        // --- SEAT UPGRADE MODULE ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = ElegantDarkSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, ElegantDarkBorderSubtle)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ElegantDarkSecondary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Upgrade, contentDescription = "Upgrade Seat", tint = ElegantDarkSecondary)
                    }
                    Column {
                        Text(
                            text = if (upgradeSuccess) "Diamond Suite Active!" else "Luxury Seat Upgrades",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                        Text(
                            text = if (upgradeSuccess) "Section Diamond Box 10, Row B" else "Instant upgrade to Fieldside Suite",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }
                }

                Button(
                    onClick = {
                        viewModel.upgradeTicket()
                        upgradeSuccess = true
                    },
                    enabled = !upgradeSuccess,
                    colors = ButtonDefaults.buttonColors(containerColor = ElegantDarkSecondary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("upgrade_button")
                ) {
                    Text(
                        text = if (upgradeSuccess) "Upgraded" else "Claim Suite",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}
