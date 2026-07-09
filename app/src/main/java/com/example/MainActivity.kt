package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appViewModel: AppViewModel = viewModel()
            val isDarkTheme by appViewModel.isDarkTheme.collectAsState()
            val mapRedirectUrl by appViewModel.mapRedirectUrl.collectAsState()
            val context = LocalContext.current

            LaunchedEffect(mapRedirectUrl) {
                mapRedirectUrl?.let { url ->
                    try {
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url)).apply {
                            setPackage("com.google.android.apps.maps")
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        try {
                            val webIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
                            context.startActivity(webIntent)
                        } catch (ex: Exception) {
                            // Ignored or toasted
                        }
                    } finally {
                        appViewModel.clearMapRedirect()
                    }
                }
            }

            MyApplicationTheme(darkTheme = isDarkTheme) {
                val isLoggedIn by appViewModel.isLoggedIn.collectAsState()

                if (!isLoggedIn) {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        LoginScreen(
                            viewModel = appViewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                } else {
                    MainAppLayout(viewModel = appViewModel)
                }
            }
        }
    }
}

@Composable
fun MainAppLayout(
    viewModel: AppViewModel
) {
    var activeTab by remember { mutableStateOf("dashboard") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ElegantDarkBg,
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            // High fidelity bottom navigation deck
            CustomBottomBar(
                activeTab = activeTab,
                onTabSelected = { activeTab = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                "dashboard" -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToTab = { activeTab = it }
                )
                "ai_assistant" -> AiAssistantScreen(
                    viewModel = viewModel
                )
                "stadium_map" -> StadiumMapScreen(
                    viewModel = viewModel
                )
                "food_beverage" -> FoodBeverageScreen(
                    viewModel = viewModel
                )
                "tickets" -> TicketScreen(
                    viewModel = viewModel
                )
                "emergency" -> EmergencyScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun CustomBottomBar(
    activeTab: String,
    onTabSelected: (String) -> Unit
) {
    val items = listOf(
        NavigationItem("dashboard", Icons.Default.Dashboard, "Hub"),
        NavigationItem("ai_assistant", Icons.Default.SmartToy, "AI Bot"),
        NavigationItem("stadium_map", Icons.Default.Map, "Map"),
        NavigationItem("food_beverage", Icons.Default.Fastfood, "Snacks"),
        NavigationItem("tickets", Icons.Default.ConfirmationNumber, "Pass"),
        NavigationItem("emergency", Icons.Default.Warning, "SOS")
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = ElegantDarkSurface),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, ElegantDarkBorderSubtle),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = activeTab == item.route
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onTabSelected(item.route) }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("tab_${item.route}"),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) ElegantDarkPrimary.copy(alpha = 0.15f)
                                else Color.Transparent
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (isSelected) ElegantDarkPrimary else Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = item.label,
                        fontSize = 9.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) ElegantDarkPrimary else Color.White.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

data class NavigationItem(
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String
)
