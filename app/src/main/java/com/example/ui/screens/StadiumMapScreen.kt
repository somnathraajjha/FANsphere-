package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StadiumMapScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.currentLanguage.collectAsState()
    val isDark by viewModel.isDarkTheme.collectAsState()

    // Stadium Search State
    val searchQuery by viewModel.stadiumSearchQuery.collectAsState()
    val selectedStadium by viewModel.selectedStadium.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()
    val favoriteStadiumIds by viewModel.favoriteStadiumIds.collectAsState()
    val isStadiumMode by viewModel.isStadiumMode.collectAsState()

    // GPS & Live Navigation State
    val gpsX by viewModel.userGpsX.collectAsState()
    val gpsY by viewModel.userGpsY.collectAsState()
    val isSimulatingMovement by viewModel.isSimulatingMovement.collectAsState()
    val routeDestination by viewModel.routeDestination.collectAsState()
    val wheelchairOnly by viewModel.wheelchairOnly.collectAsState()
    val voiceSearchStatus by viewModel.voiceSearchStatus.collectAsState()

    // Parking & State
    val parkingZones by viewModel.parkingZones.collectAsState()
    val reservedZone by viewModel.reservedZone.collectAsState()
    val parkingTimer by viewModel.parkingTimerSeconds.collectAsState()

    var activeScreenTab by remember { mutableStateOf("NAV") } // "NAV", "INFO", "PARKING"
    var isSearching by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ElegantDarkBg)
    ) {
        // --- TOP PREMIUM SEARCH BAR ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(ElegantDarkHeaderStart, Color.Transparent)
                    )
                )
                .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Title and search toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(
                            imageVector = Icons.Default.SportsFootball,
                            contentDescription = "Stadium Mode",
                            tint = ElegantDarkPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = if (isSearching) "Search Stadiums" else selectedStadium.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth(0.6f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Voice Search Button with simulated voice results
                        IconButton(
                            onClick = { viewModel.triggerVoiceSearchSimulated() },
                            modifier = Modifier
                                .size(38.dp)
                                .background(Color.White.copy(alpha = 0.05f), CircleShape)
                                .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice Search",
                                tint = if (voiceSearchStatus != null) ElegantDarkSecondary else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Search Toggle Button
                        IconButton(
                            onClick = { isSearching = !isSearching },
                            modifier = Modifier
                                .size(38.dp)
                                .background(
                                    if (isSearching) ElegantDarkPrimary.copy(alpha = 0.15f)
                                    else Color.White.copy(alpha = 0.05f),
                                    CircleShape
                                )
                                .border(
                                    1.dp,
                                    if (isSearching) ElegantDarkPrimary else Color.White.copy(alpha = 0.1f),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = if (isSearching) Icons.Default.Close else Icons.Default.Search,
                                contentDescription = "Search Stadiums",
                                tint = if (isSearching) ElegantDarkPrimary else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Voice Search Dialog Overlay status
                voiceSearchStatus?.let { status ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ElegantDarkSecondary.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, ElegantDarkSecondary.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(ElegantDarkSecondary)
                            )
                            Text(
                                text = status,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Main search input field (Visible if isSearching is true)
                AnimatedVisibility(visible = isSearching) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setStadiumSearchQuery(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("stadium_search_input"),
                        placeholder = { Text("Search by name, city, country, team...", color = Color.White.copy(alpha = 0.4f)) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = ElegantDarkPrimary)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setStadiumSearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear search", tint = Color.White)
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElegantDarkPrimary,
                            unfocusedBorderColor = ElegantDarkBorderSubtle,
                            focusedContainerColor = ElegantDarkSurface,
                            unfocusedContainerColor = ElegantDarkSurface
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // --- SEARCH RESULTS & OVERLAYS ---
        if (isSearching) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // If query is empty, show favorites & recent searches
                if (searchQuery.trim().isEmpty()) {
                    // Recent Searches Section
                    if (recentSearches.isNotEmpty()) {
                        item {
                            Text(
                                text = "Recent Searches",
                                fontWeight = FontWeight.Bold,
                                color = ElegantDarkSecondary,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                        item {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(recentSearches) { recentName ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(ElegantDarkSurface)
                                            .border(1.dp, ElegantDarkBorderSubtle, RoundedCornerShape(8.dp))
                                            .clickable {
                                                val found = MockDataFactory.globalStadiums.firstOrNull { it.name == recentName }
                                                found?.let {
                                                    viewModel.selectStadium(it)
                                                    isSearching = false
                                                }
                                            }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(Icons.Default.History, contentDescription = "Recent", tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(12.dp))
                                            Text(recentName, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Favorites Section
                    item {
                        Text(
                            text = "Favorite Arenas",
                            fontWeight = FontWeight.Bold,
                            color = ElegantDarkSecondary,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                        )
                    }

                    val favoriteStadiums = MockDataFactory.globalStadiums.filter { favoriteStadiumIds.contains(it.id) }
                    if (favoriteStadiums.isEmpty()) {
                        item {
                            Text("No favorite stadiums marked yet.", color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
                        }
                    } else {
                        items(favoriteStadiums) { stadium ->
                            StadiumRowItem(
                                stadium = stadium,
                                isFavorite = true,
                                onSelect = {
                                    viewModel.selectStadium(stadium)
                                    isSearching = false
                                },
                                onToggleFav = { viewModel.toggleFavoriteStadium(stadium.id) }
                            )
                        }
                    }

                    // AI Assisted Search Corner hint
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ElegantDarkPrimary.copy(alpha = 0.05f)),
                            border = BorderStroke(1.dp, ElegantDarkPrimary.copy(alpha = 0.2f)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(ElegantDarkPrimary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = "AI Assisted", tint = ElegantDarkPrimary, modifier = Modifier.size(16.dp))
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("AI-Assisted Search Active", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                                    Text("Type natural queries such as 'stadium in Madrid with capacity over 80k' and see auto-suggestions instantly filter.", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                                }
                            }
                        }
                    }

                } else {
                    // Filter stadiums dynamically based on search query
                    val query = searchQuery.lowercase(Locale.getDefault())
                    val matches = MockDataFactory.globalStadiums.filter {
                        it.name.lowercase(Locale.getDefault()).contains(query) ||
                                it.city.lowercase(Locale.getDefault()).contains(query) ||
                                it.country.lowercase(Locale.getDefault()).contains(query) ||
                                it.tournament.lowercase(Locale.getDefault()).contains(query) ||
                                it.homeTeam.lowercase(Locale.getDefault()).contains(query) ||
                                it.league.lowercase(Locale.getDefault()).contains(query)
                    }

                    if (matches.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.SearchOff, contentDescription = "No results", tint = Color.White.copy(alpha = 0.2f), modifier = Modifier.size(48.dp))
                                    Text("No stadiums found", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                                    Text("Try checking spelling or type another country", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                                }
                            }
                        }
                    } else {
                        item {
                            Text(
                                text = "Found ${matches.size} Matching Arenas",
                                fontWeight = FontWeight.Bold,
                                color = ElegantDarkPrimary,
                                fontSize = 12.sp
                            )
                        }

                        items(matches) { stadium ->
                            val isFav = favoriteStadiumIds.contains(stadium.id)
                            StadiumRowItem(
                                stadium = stadium,
                                isFavorite = isFav,
                                onSelect = {
                                    viewModel.selectStadium(stadium)
                                    isSearching = false
                                },
                                onToggleFav = { viewModel.toggleFavoriteStadium(stadium.id) }
                            )
                        }
                    }
                }
            }
        } else {
            // --- STADIUM MODE INTERACTIVE AREA ---

            // Small header for selected stadium
            Card(
                colors = CardDefaults.cardColors(containerColor = ElegantDarkSurface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, ElegantDarkBorderSubtle),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Stadium mini visual representation (FIFA flag color background)
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(ElegantDarkPrimary, ElegantDarkCyan)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Stadium, contentDescription = "Stadium", tint = Color.Black, modifier = Modifier.size(24.dp))
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = selectedStadium.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = "Loc", tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(12.dp))
                            Text(
                                text = "${selectedStadium.city}, ${selectedStadium.country}",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                        Text(
                            text = "Capacity: ${String.format("%,d", selectedStadium.capacity)} • ${selectedStadium.homeTeam}",
                            fontSize = 10.sp,
                            color = ElegantDarkPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Favorite Toggle Icon
                    val isFav = favoriteStadiumIds.contains(selectedStadium.id)
                    IconButton(
                        onClick = { viewModel.toggleFavoriteStadium(selectedStadium.id) }
                    ) {
                        Icon(
                            imageVector = if (isFav) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Fav",
                            tint = if (isFav) ElegantDarkSecondary else Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // --- TABS: Live Nav vs Info vs Parking ---
            TabRow(
                selectedTabIndex = when (activeScreenTab) {
                    "NAV" -> 0
                    "INFO" -> 1
                    else -> 2
                },
                containerColor = ElegantDarkBg,
                contentColor = ElegantDarkPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(
                            tabPositions[when (activeScreenTab) {
                                "NAV" -> 0
                                "INFO" -> 1
                                else -> 2
                            }]
                        ),
                        color = ElegantDarkPrimary
                    )
                },
                divider = {}
            ) {
                Tab(
                    selected = activeScreenTab == "NAV",
                    onClick = { activeScreenTab = "NAV" },
                    text = { Text("Live GPS Nav", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (activeScreenTab == "NAV") ElegantDarkPrimary else Color.White.copy(alpha = 0.6f)) }
                )
                Tab(
                    selected = activeScreenTab == "INFO",
                    onClick = { activeScreenTab = "INFO" },
                    text = { Text("Stadium Facilities", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (activeScreenTab == "INFO") ElegantDarkPrimary else Color.White.copy(alpha = 0.6f)) }
                )
                Tab(
                    selected = activeScreenTab == "PARKING",
                    onClick = { activeScreenTab = "PARKING" },
                    text = { Text("Smart Parking", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (activeScreenTab == "PARKING") ElegantDarkPrimary else Color.White.copy(alpha = 0.6f)) }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // --- TAB CONTENT ---
            when (activeScreenTab) {
                "NAV" -> {
                    // --- LIVE GPS NAVIGATION TAB ---
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Real-Time Stadium Intelligence Broadcast Bar
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.04f)),
                            border = BorderStroke(1.dp, ElegantDarkBorderSubtle),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(TurfGreen)
                                )
                                Text(
                                    text = "LIVE INTELLIGENCE:",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TurfGreen,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Gate B Queue: 3m • Food Stall #12 Queue: 8m • Clear Skies 23°C",
                                    fontSize = 10.sp,
                                    color = Color.White.copy(alpha = 0.8f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // AI Smart Recommendations Tip Banner
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ElegantDarkPrimary.copy(alpha = 0.08f)),
                            border = BorderStroke(1.dp, ElegantDarkPrimary.copy(alpha = 0.15f)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "AI Proactive Tip",
                                    tint = ElegantDarkPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Column {
                                    Text(
                                        text = "FanSphere AI Proactive Recommendation",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = ElegantDarkPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    val aiTip = when (routeDestination) {
                                        "SEAT" -> "We detected you entering through Gate B. Section 102 is 120 meters ahead. Take the North Lift for wheelchair access or Elevator 4 to avoid crowds."
                                        "FOOD" -> "The food court on East Side (Sector 115) currently has the lowest queue wait. Pre-order loaded fries to save another 5 mins!"
                                        "RESTROOM" -> "The restrooms at Sector 102 are currently heavily crowded. We advise using the restrooms in Section 115 (just a 1 min walk, empty)."
                                        "FIRST_AID" -> "First Aid post #3 is located near Section 104 exit ramp. A rapid responder can also be dispatched by hitting SOS."
                                        "MERCH" -> "The official FIFA Store near West Gate B has a 12 min lineup. Try the retail cart in Section 204 which has no waiting queue!"
                                        "EXIT" -> "In case of emergency evacuation, exit directly through Gate B (West) or Gate D (South). Maintain pace and follow illuminated paths."
                                        else -> "Welcome to ${selectedStadium.name}! Gate B (West Entrance) has the lowest queue wait (3 mins). Your ticket seat is VIP Section 102, Row F. Tap 'My Seat' below to route your way."
                                    }
                                    Text(
                                        text = aiTip,
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.9f),
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }

                        // Seating / Amenity Routing Selector Row
                        Text(
                            text = "Set Destination",
                            fontWeight = FontWeight.Bold,
                            color = ElegantDarkSecondary,
                            fontSize = 13.sp
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val routeOptions = listOf(
                                Triple("ALL", Icons.Default.Map, "Show All"),
                                Triple("SEAT", Icons.Default.Chair, "My Seat"),
                                Triple("FOOD", Icons.Default.Fastfood, "Snacks"),
                                Triple("RESTROOM", Icons.Default.Wc, "Restroom"),
                                Triple("MERCH", Icons.Default.Storefront, "Merch"),
                                Triple("FIRST_AID", Icons.Default.LocalHospital, "First Aid"),
                                Triple("EXIT", Icons.Default.ExitToApp, "Safe Exit")
                            )

                            items(routeOptions) { (key, icon, label) ->
                                val isSelected = routeDestination == key
                                Row(
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
                                        .clickable { viewModel.setRouteDestination(key) }
                                        .padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label,
                                        tint = if (isSelected) Color.Black else Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.Black else Color.White
                                    )
                                }
                            }
                        }

                        // INTERACTIVE MAP CARD
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp),
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
                                // Draw Dynamic Stadium map
                                val primaryColor = ElegantDarkPrimary
                                val secondaryColor = ElegantDarkSecondary
                                val tertiaryColor = ElegantDarkTertiary
                                val emergencyColor = EmergencyCoral
                                val cyanColor = ElegantDarkCyan

                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val centerX = size.width / 2
                                    val centerY = size.height / 2
                                    val radius = size.width.coerceAtMost(size.height) * 0.35f

                                    // 1. Draw Football Pitch
                                    drawRect(
                                        color = Color(0xFF0F5E36),
                                        size = Size(radius * 0.8f, radius * 1.2f),
                                        topLeft = Offset(centerX - radius * 0.4f, centerY - radius * 0.6f)
                                    )
                                    // Pitch white lines outline
                                    drawRect(
                                        color = Color.White.copy(alpha = 0.25f),
                                        size = Size(radius * 0.8f, radius * 1.2f),
                                        topLeft = Offset(centerX - radius * 0.4f, centerY - radius * 0.6f),
                                        style = Stroke(width = 2f)
                                    )
                                    // Pitch center circle
                                    drawCircle(
                                        color = Color.White.copy(alpha = 0.2f),
                                        radius = radius * 0.2f,
                                        center = Offset(centerX, centerY),
                                        style = Stroke(width = 2f)
                                    )

                                    // 2. Draw Concentric Seating Rings with different colors representing crowd/sections
                                    // Inner ring (VIP Level 1)
                                    drawCircle(
                                        color = Color.Gray.copy(alpha = 0.2f),
                                        radius = radius,
                                        center = Offset(centerX, centerY),
                                        style = Stroke(width = 8f)
                                    )
                                    // Outer ring (Level 2 & 3)
                                    drawCircle(
                                        color = Color.White.copy(alpha = 0.1f),
                                        radius = radius * 1.3f,
                                        center = Offset(centerX, centerY),
                                        style = Stroke(width = 12f)
                                    )

                                    // 3. Draw Gate B Entrance Point (Starting landmark)
                                    val gateBOffset = Offset(centerX - radius * 1.22f, centerY)
                                    drawCircle(
                                        color = primaryColor,
                                        radius = 16f,
                                        center = gateBOffset
                                    )

                                    // 4. Draw Specific Amenity Locations
                                    // VIP Sec 102 Seat
                                    val seatOffset = Offset(centerX - radius * 0.65f, centerY - radius * 0.65f)
                                    drawCircle(
                                        color = secondaryColor,
                                        radius = 20f,
                                        center = seatOffset
                                    )

                                    // Food Court (Sector 115 East)
                                    val foodOffset = Offset(centerX + radius * 0.85f, centerY)
                                    drawCircle(
                                        color = secondaryColor,
                                        radius = 14f,
                                        center = foodOffset
                                    )

                                    // Restrooms (Sector 102 North-West)
                                    val restroomOffset = Offset(centerX - radius * 0.1f, centerY - radius * 1.1f)
                                    drawCircle(
                                        color = Color(0xFF3B82F6),
                                        radius = 14f,
                                        center = restroomOffset
                                    )

                                    // Merch Store (Sector 204 South-East)
                                    val merchOffset = Offset(centerX + radius * 0.6f, centerY + radius * 0.7f)
                                    drawCircle(
                                        color = tertiaryColor,
                                        radius = 14f,
                                        center = merchOffset
                                    )

                                    // First Aid (South-West)
                                    val medicalOffset = Offset(centerX - radius * 0.4f, centerY + radius * 0.95f)
                                    drawCircle(
                                        color = emergencyColor,
                                        radius = 14f,
                                        center = medicalOffset
                                    )

                                    // Exit Gate D (South Gate)
                                    val exitOffset = Offset(centerX, centerY + radius * 1.3f)
                                    drawCircle(
                                        color = emergencyColor,
                                        radius = 16f,
                                        center = exitOffset
                                    )

                                    // 5. Draw Indoor Routing trail from Entrance (Gate B) to selected targets
                                    val routeColor = if (wheelchairOnly) cyanColor else primaryColor
                                    val trailStroke = 7f

                                    if (routeDestination == "ALL" || routeDestination == "SEAT") {
                                        // Gate B to Seat
                                        drawLine(
                                            color = routeColor,
                                            start = gateBOffset,
                                            end = Offset(centerX - radius * 1.0f, centerY - radius * 0.35f),
                                            strokeWidth = trailStroke
                                        )
                                        drawLine(
                                            color = routeColor,
                                            start = Offset(centerX - radius * 1.0f, centerY - radius * 0.35f),
                                            end = seatOffset,
                                            strokeWidth = trailStroke
                                        )
                                    }

                                    if (routeDestination == "FOOD") {
                                        // Gate B to Food East
                                        drawLine(
                                            color = routeColor,
                                            start = gateBOffset,
                                            end = Offset(centerX, centerY - radius * 0.75f),
                                            strokeWidth = trailStroke
                                        )
                                        drawLine(
                                            color = routeColor,
                                            start = Offset(centerX, centerY - radius * 0.75f),
                                            end = foodOffset,
                                            strokeWidth = trailStroke
                                        )
                                    }

                                    if (routeDestination == "RESTROOM") {
                                        // Gate B to Restrooms
                                        drawLine(
                                            color = routeColor,
                                            start = gateBOffset,
                                            end = Offset(centerX - radius * 0.7f, centerY - radius * 0.7f),
                                            strokeWidth = trailStroke
                                        )
                                        drawLine(
                                            color = routeColor,
                                            start = Offset(centerX - radius * 0.7f, centerY - radius * 0.7f),
                                            end = restroomOffset,
                                            strokeWidth = trailStroke
                                        )
                                    }

                                    if (routeDestination == "MERCH") {
                                        // Gate B to Merch Store
                                        drawLine(
                                            color = routeColor,
                                            start = gateBOffset,
                                            end = Offset(centerX - radius * 0.5f, centerY + radius * 0.5f),
                                            strokeWidth = trailStroke
                                        )
                                        drawLine(
                                            color = routeColor,
                                            start = Offset(centerX - radius * 0.5f, centerY + radius * 0.5f),
                                            end = merchOffset,
                                            strokeWidth = trailStroke
                                        )
                                    }

                                    if (routeDestination == "FIRST_AID") {
                                        // Gate B to First Aid
                                        drawLine(
                                            color = routeColor,
                                            start = gateBOffset,
                                            end = Offset(centerX - radius * 0.9f, centerY + radius * 0.4f),
                                            strokeWidth = trailStroke
                                        )
                                        drawLine(
                                            color = routeColor,
                                            start = Offset(centerX - radius * 0.9f, centerY + radius * 0.4f),
                                            end = medicalOffset,
                                            strokeWidth = trailStroke
                                        )
                                    }

                                    if (routeDestination == "EXIT") {
                                        // Gate B to Exit Gate D
                                        drawLine(
                                            color = routeColor,
                                            start = gateBOffset,
                                            end = Offset(centerX - radius * 0.7f, centerY + radius * 0.7f),
                                            strokeWidth = trailStroke
                                        )
                                        drawLine(
                                            color = routeColor,
                                            start = Offset(centerX - radius * 0.7f, centerY + radius * 0.7f),
                                            end = exitOffset,
                                            strokeWidth = trailStroke
                                        )
                                    }

                                    // 6. Draw Simulated Live User Pulsating GPS Dot
                                    // Map normalized coordinates (0f to 1f) relative to canvas size
                                    val normalizedUserX = gateBOffset.x + (size.width - gateBOffset.x) * gpsX
                                    val normalizedUserY = centerY * (gpsY * 2f)

                                    drawCircle(
                                        color = Color(0xFF3B82F6).copy(alpha = 0.35f),
                                        radius = 24f,
                                        center = Offset(normalizedUserX, normalizedUserY)
                                    )
                                    drawCircle(
                                        color = Color.White,
                                        radius = 10f,
                                        center = Offset(normalizedUserX, normalizedUserY)
                                    )
                                    drawCircle(
                                        color = Color(0xFF3B82F6),
                                        radius = 7f,
                                        center = Offset(normalizedUserX, normalizedUserY)
                                    )
                                }

                                // Overlay Text Tags over Canvas
                                Box(modifier = Modifier.fillMaxSize()) {
                                    // Gate B Tag
                                    Text(
                                        "GATE B\n(Entrance)",
                                        color = primaryColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp,
                                        modifier = Modifier
                                            .align(Alignment.CenterStart)
                                            .padding(start = 14.dp),
                                        textAlign = TextAlign.Center
                                    )

                                    // Ticket Seat Tag
                                    Text(
                                        "MY SEAT\nSec 102",
                                        color = secondaryColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp,
                                        modifier = Modifier
                                            .align(Alignment.TopCenter)
                                            .padding(top = 45.dp, end = 120.dp),
                                        textAlign = TextAlign.Center
                                    )

                                    // Pitch Tag
                                    Text(
                                        "PITCH",
                                        color = Color.White.copy(alpha = 0.25f),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.align(Alignment.Center)
                                    )

                                    // Selected Destination tag bubble
                                    if (routeDestination != "ALL") {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(12.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(ElegantDarkPrimary.copy(alpha = 0.9f))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "Targeting: $routeDestination",
                                                color = Color.Black,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // GPS Simulation and Routing Actions Panel
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ElegantDarkSurface),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, ElegantDarkBorderSubtle)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Live GPS Tracking",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color.White
                                        )
                                        val gpsLabel = if (isSimulatingMovement) "User traveling along path..." else "Standby at Gate B Entrance"
                                        Text(
                                            text = gpsLabel,
                                            fontSize = 11.sp,
                                            color = if (isSimulatingMovement) ElegantDarkSecondary else Color.White.copy(alpha = 0.5f)
                                        )
                                    }

                                    Button(
                                        onClick = { viewModel.startMovementSimulation() },
                                        enabled = !isSimulatingMovement,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = ElegantDarkPrimary,
                                            disabledContainerColor = ElegantDarkSurface.copy(alpha = 0.3f)
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Navigation,
                                            contentDescription = "Simulate",
                                            tint = Color.Black,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isSimulatingMovement) "Navigating..." else "Simulate Move",
                                            fontSize = 11.sp,
                                            color = Color.Black,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Divider(color = Color.White.copy(alpha = 0.08f))

                                // Wheelchair only route toggle
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Accessible,
                                            contentDescription = "Wheelchair",
                                            tint = ElegantDarkCyan,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Column {
                                            Text(
                                                text = "Accessible Routes Only",
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 12.sp,
                                                color = Color.White
                                            )
                                            Text(
                                                text = "Prioritize elevators and wheelchair-friendly ramps",
                                                fontSize = 10.sp,
                                                color = Color.White.copy(alpha = 0.5f)
                                            )
                                        }
                                    }

                                    Switch(
                                        checked = wheelchairOnly,
                                        onCheckedChange = { viewModel.setWheelchairOnly(it) },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = ElegantDarkCyan,
                                            checkedTrackColor = ElegantDarkCyan.copy(alpha = 0.3f)
                                        )
                                    )
                                }

                                if (wheelchairOnly) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(ElegantDarkCyan.copy(alpha = 0.1f))
                                            .padding(8.dp)
                                    ) {
                                        Text(
                                            text = "♿ Accessible routing active. Standard stairs avoided. Touchless elevator sensors enabled at North Core elevator shafts.",
                                            color = ElegantDarkCyan,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        // Public Transport detail button
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ElegantDarkSurface),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, ElegantDarkBorderSubtle)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.Train, contentDescription = "Transport", tint = ElegantDarkSecondary, modifier = Modifier.size(16.dp))
                                    Text("Nearby Public Transport Access", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                                }
                                selectedStadium.publicTransport.forEach { transport ->
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(ElegantDarkSecondary))
                                        Text(transport, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                "INFO" -> {
                    // --- STADIUM FACILITIES TAB ---
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Overview Card with Image placeholder
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = ElegantDarkSurface),
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, ElegantDarkBorderSubtle)
                            ) {
                                Column {
                                    // Dynamic stadium decorative graphic representing pitch field lines
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(100.dp)
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(ElegantDarkHeaderStart, ElegantDarkPrimary.copy(alpha = 0.2f))
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Stadium, contentDescription = "Stadium Illustration", tint = ElegantDarkPrimary.copy(alpha = 0.4f), modifier = Modifier.size(60.dp))
                                    }

                                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(selectedStadium.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                                        Text("Tournament: ${selectedStadium.tournament}", color = ElegantDarkSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text("League: ${selectedStadium.league}", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                                        Text("Opening Hours: ${selectedStadium.openingHours}", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }

                        // Specific listings
                        item {
                            FacilityListSection(
                                title = "Gates & Entry Entrances",
                                icon = Icons.Default.DoorSliding,
                                itemsList = selectedStadium.gates,
                                highlightColor = ElegantDarkPrimary
                            )
                        }

                        item {
                            FacilityListSection(
                                title = "Food Courts & Snack Bars",
                                icon = Icons.Default.Fastfood,
                                itemsList = selectedStadium.foodCourts,
                                highlightColor = ElegantDarkSecondary
                            )
                        }

                        item {
                            FacilityListSection(
                                title = "Restrooms & Rest Facilities",
                                icon = Icons.Default.Wc,
                                itemsList = selectedStadium.restrooms,
                                highlightColor = Color(0xFF3B82F6)
                            )
                        }

                        item {
                            FacilityListSection(
                                title = "Parking Lots & EV Hubs",
                                icon = Icons.Default.LocalParking,
                                itemsList = selectedStadium.parkingLots,
                                highlightColor = ElegantDarkPrimary
                            )
                        }

                        item {
                            FacilityListSection(
                                title = "VIP Lounges",
                                icon = Icons.Default.Stars,
                                itemsList = selectedStadium.vipLounges,
                                highlightColor = ElegantDarkSecondary
                            )
                        }

                        item {
                            FacilityListSection(
                                title = "Prayer & Meditation Rooms",
                                icon = Icons.Default.Place,
                                itemsList = selectedStadium.prayerRooms,
                                highlightColor = ElegantDarkTertiary
                            )
                        }

                        item {
                            FacilityListSection(
                                title = "Official Merchandise Stores",
                                icon = Icons.Default.Storefront,
                                itemsList = selectedStadium.merchandiseStores,
                                highlightColor = ElegantDarkPrimary
                            )
                        }

                        item {
                            FacilityListSection(
                                title = "Accessibility Infrastructure",
                                icon = Icons.Default.Accessible,
                                itemsList = selectedStadium.accessibilityFacilities,
                                highlightColor = ElegantDarkCyan
                            )
                        }
                    }
                }

                else -> {
                    // --- SMART PARKING TAB ---
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Active reservation card (if any)
                        item {
                            AnimatedVisibility(
                                visible = reservedZone != null,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                reservedZone?.let { zone ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = ElegantDarkPrimary.copy(alpha = 0.12f)),
                                        shape = RoundedCornerShape(20.dp),
                                        border = BorderStroke(1.dp, ElegantDarkPrimary.copy(alpha = 0.5f))
                                    ) {
                                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "RESERVATION ACTIVE",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = ElegantDarkPrimary,
                                                    letterSpacing = 1.sp
                                                )

                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(TurfGreen.copy(alpha = 0.15f))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text("Paid via Wallet", color = TurfGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(zone.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                                                    Text("Assigned Spot: Spot #108 • EV Active", fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
                                                }

                                                // Countdown clock
                                                val hours = parkingTimer / 3600
                                                val minutes = (parkingTimer % 3600) / 60
                                                val seconds = parkingTimer % 60
                                                Text(
                                                    text = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds),
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = ElegantDarkSecondary
                                                )
                                            }

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Button(
                                                    onClick = { /* Simulated vehicle locator */ },
                                                    modifier = Modifier.weight(1f),
                                                    colors = ButtonDefaults.buttonColors(containerColor = ElegantDarkPrimary),
                                                    shape = RoundedCornerShape(10.dp)
                                                ) {
                                                    Icon(Icons.Default.LocationOn, contentDescription = "Locate", modifier = Modifier.size(14.dp), tint = Color.Black)
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Car Locator", fontSize = 12.sp, color = Color.Black)
                                                }

                                                OutlinedButton(
                                                    onClick = { viewModel.cancelParkingReservation() },
                                                    modifier = Modifier.weight(1f),
                                                    border = BorderStroke(1.dp, EmergencyCoral.copy(alpha = 0.5f)),
                                                    shape = RoundedCornerShape(10.dp)
                                                ) {
                                                    Text("Cancel Lot", fontSize = 12.sp, color = EmergencyCoral)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Parking Hub Header
                        item {
                            Text(
                                text = "Available Lots at ${selectedStadium.name}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = ElegantDarkSecondary
                            )
                        }

                        // Parking lots items
                        items(parkingZones) { zone ->
                            val isReserved = reservedZone?.id == zone.id
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = ElegantDarkSurface),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, ElegantDarkBorderSubtle)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Text(zone.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                                            if (zone.hasEVCharging) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(CircleShape)
                                                        .background(TurfGreen.copy(alpha = 0.15f))
                                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.EvStation,
                                                        contentDescription = "EV Charging",
                                                        tint = TurfGreen,
                                                        modifier = Modifier.size(10.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Text(
                                            text = "${zone.availableSlots} / ${zone.totalSlots} Slots Available • ${zone.distanceToGate}",
                                            fontSize = 12.sp,
                                            color = Color.White.copy(alpha = 0.6f)
                                        )

                                        Text(
                                            text = "$${zone.costPerHour}/hr • Digital QR Pass",
                                            fontSize = 12.sp,
                                            color = ElegantDarkPrimary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            if (reservedZone == null) {
                                                viewModel.reserveParking(zone)
                                            }
                                        },
                                        enabled = reservedZone == null,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isReserved) TurfGreen else ElegantDarkPrimary,
                                            disabledContainerColor = ElegantDarkSurface.copy(alpha = 0.3f)
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.testTag("reserve_parking_${zone.id}")
                                    ) {
                                        Text(
                                            text = if (isReserved) "Reserved" else "Reserve",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isReserved) Color.White else Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StadiumRowItem(
    stadium: Stadium,
    isFavorite: Boolean,
    onSelect: () -> Unit,
    onToggleFav: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(containerColor = ElegantDarkSurface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, ElegantDarkBorderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Mini visual representation
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(ElegantDarkSurfaceStart, ElegantDarkBorder)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Stadium, contentDescription = "Stadium", tint = ElegantDarkPrimary, modifier = Modifier.size(20.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stadium.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )
                Text(
                    text = "${stadium.city}, ${stadium.country}",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
                Text(
                    text = "${stadium.tournament} • Capacity: ${String.format("%,d", stadium.capacity)}",
                    fontSize = 10.sp,
                    color = ElegantDarkSecondary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            IconButton(onClick = { onToggleFav() }) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) ElegantDarkSecondary else Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun FacilityListSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    itemsList: List<String>,
    highlightColor: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ElegantDarkSurface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, ElegantDarkBorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(highlightColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = title, tint = highlightColor, modifier = Modifier.size(15.dp))
                }
                Text(text = title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                itemsList.forEach { entry ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(highlightColor)
                        )
                        Text(
                            text = entry,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
