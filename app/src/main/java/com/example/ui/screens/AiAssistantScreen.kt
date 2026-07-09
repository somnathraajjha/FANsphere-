package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Language
import com.example.data.LocalizedStrings
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel
import kotlinx.coroutines.launch

@Composable
fun AiAssistantScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.currentLanguage.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val chatInput by viewModel.chatInput.collectAsState()
    val aiLoading by viewModel.aiLoading.collectAsState()
    val offlineMode by viewModel.isOfflineMode.collectAsState()

    val listState = rememberLazyListState()

    // Scroll chat to the bottom automatically when new message arrives
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    val suggestions = listOf(
        "Take me to my seat.",
        "Where is Gate B?",
        "Find vegetarian food.",
        "Translate this announcement.",
        "Where is first aid?",
        "Suggest least crowded food stalls."
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ElegantDarkBg)
    ) {
        // --- ASSISTANT HEADER ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ElegantDarkPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SupportAgent,
                        contentDescription = "Assistant",
                        tint = ElegantDarkPrimary
                    )
                }
                Column {
                    Text(
                        text = "nice",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (offlineMode) Color.Gray else TurfGreen)
                        )
                        Text(
                            text = if (offlineMode) "Offline Mode Enabled" else "Online (Gemini 3.5)",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // Offline toggle button
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Offline Simulation", fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f))
                Switch(
                    checked = offlineMode,
                    onCheckedChange = { viewModel.setOfflineMode(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = ElegantDarkSecondary,
                        checkedTrackColor = ElegantDarkSecondary.copy(alpha = 0.5f),
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color.Gray.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.scale(0.8f)
                )
            }
        }

        // --- CONVERSATION VIEWPORT ---
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(chatMessages) { message ->
                val (text, isUser) = message
                ChatBubble(text = text, isUser = isUser)
            }

            if (aiLoading) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ElegantDarkSurface),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, ElegantDarkBorderSubtle)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                              ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = ElegantDarkPrimary
                                )
                                Text(
                                    text = "AI is thinking...",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- SUGGESTIONS CHIPS ---
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(suggestions) { sugg ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(ElegantDarkSurface)
                        .border(1.dp, ElegantDarkBorderSubtle, RoundedCornerShape(12.dp))
                        .clickable { viewModel.sendChatMessage(sugg) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = sugg,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = ElegantDarkPrimary
                    )
                }
            }
        }

        // --- INPUT SECTION (WITH TEXT, VOICE, CAMERA, AND OCR SIMULATION) ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = ElegantDarkSurface),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, ElegantDarkBorderSubtle)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Camera / OCR simulation button
                IconButton(
                    onClick = {
                        viewModel.updateChatInput("translate \"Por favor, mantenga las escaleras despejadas.\"")
                    },
                    modifier = Modifier.background(Color.White.copy(alpha = 0.05f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "OCR Translate Scanner",
                        tint = ElegantDarkPrimary
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Microphone simulator button
                IconButton(
                    onClick = {
                        viewModel.updateChatInput("Where is my seat?")
                    },
                    modifier = Modifier.background(Color.White.copy(alpha = 0.05f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Simulate Voice",
                        tint = ElegantDarkSecondary
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Input field
                OutlinedTextField(
                    value = chatInput,
                    onValueChange = { viewModel.updateChatInput(it) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_chat_input"),
                    placeholder = { Text("Ask anything...", fontSize = 14.sp, color = Color.White.copy(alpha = 0.4f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    maxLines = 2,
                    textStyle = TextStyle(fontSize = 14.sp, color = Color.White)
                )

                // Send button
                IconButton(
                    onClick = {
                        if (chatInput.isNotEmpty()) {
                            viewModel.sendChatMessage(chatInput)
                        }
                    },
                    enabled = chatInput.isNotEmpty(),
                    modifier = Modifier.background(
                        if (chatInput.isNotEmpty()) ElegantDarkPrimary else Color.White.copy(alpha = 0.05f),
                        CircleShape
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if (chatInput.isNotEmpty()) Color.Black else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(text: String, isUser: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 0.dp,
                        bottomEnd = if (isUser) 0.dp else 16.dp
                    )
                )
                .background(
                    if (isUser) ElegantDarkPrimary
                    else ElegantDarkSurface
                )
                .border(
                    1.dp,
                    if (isUser) Color.Transparent else ElegantDarkBorderSubtle,
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 0.dp,
                        bottomEnd = if (isUser) 0.dp else 16.dp
                    )
                )
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = text,
                    fontSize = 13.sp,
                    color = if (isUser) Color.Black else Color.White,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
