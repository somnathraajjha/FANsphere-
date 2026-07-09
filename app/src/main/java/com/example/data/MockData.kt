package com.example.data

import java.util.UUID

// --- MATCH DATA MODELS ---

data class Team(
    val name: String,
    val flagEmoji: String,
    val code: String,
    val primaryColor: String
)

data class Player(
    val name: String,
    val number: Int,
    val position: String,
    val rating: Double
)

data class MatchEvent(
    val minute: Int,
    val type: String, // "GOAL", "YELLOW", "RED", "SUB"
    val teamCode: String,
    val description: String
)

data class Match(
    val id: String,
    val teamA: Team,
    val teamB: Team,
    val scoreA: Int,
    val scoreB: Int,
    val status: String, // "LIVE", "UPCOMING", "FINISHED"
    val minute: String,
    val date: String,
    val time: String,
    val stadium: String,
    val city: String,
    val possessionA: Int,
    val shotsA: Int,
    val shotsB: Int,
    val foulsA: Int,
    val foulsB: Int,
    val cornersA: Int,
    val cornersB: Int,
    val lineupA: List<Player>,
    val lineupB: List<Player>,
    val timeline: List<MatchEvent>
)

// --- FOOD & BEVERAGE MODELS ---

data class FoodItem(
    val id: String,
    val name: String,
    val category: String, // "Burger", "Pizza", "Beverage", "Snack", "Dessert"
    val price: Double,
    val imageDescription: String,
    val waitingTimeMinutes: Int,
    val isVeg: Boolean = false,
    val isHalal: Boolean = false,
    val isGlutenFree: Boolean = false,
    val isVegan: Boolean = false,
    val rating: Double = 4.5,
    val stallName: String = "Stadium Express"
)

// --- PARKING MODELS ---

data class ParkingZone(
    val id: String,
    val name: String,
    val totalSlots: Int,
    val availableSlots: Int,
    val costPerHour: Double,
    val distanceToGate: String,
    val hasEVCharging: Boolean,
    val qrCode: String = "MOCK_PARKING_QR"
)

// --- TICKET MODELS ---

data class StadiumTicket(
    val id: String,
    val matchTitle: String,
    val teamAEmoji: String,
    val teamBEmoji: String,
    val section: String,
    val row: String,
    val seat: String,
    val gate: String,
    val level: String,
    val qrCodePayload: String,
    val date: String,
    val time: String,
    val tier: String = "VIP Gold"
)

// --- COMMUNITY FEED MODELS ---

data class CommunityPost(
    val id: String,
    val userName: String,
    val userCountry: String,
    val userFlag: String,
    val text: String,
    val timestamp: String,
    val likes: Int,
    val prediction: String? = null
)

// --- LOST & FOUND MODELS ---

data class LostFoundItem(
    val id: String,
    val title: String,
    val description: String,
    val locationFound: String,
    val dateReported: String,
    val isLost: Boolean, // true = Lost, false = Found
    val status: String // "Pending", "Matched", "Claimed"
)

object MockDataFactory {
    val teamArgentina = Team("Argentina", "🇦🇷", "ARG", "#74ACDF")
    val teamBrazil = Team("Brazil", "🇧🇷", "BRA", "#FEDD00")
    val teamUSA = Team("USA", "🇺🇸", "USA", "#0A3161")
    val teamMexico = Team("Mexico", "🇲🇽", "MEX", "#006847")

    val lineupARG = listOf(
        Player("E. Martínez", 23, "GK", 8.5),
        Player("N. Molina", 26, "DF", 7.6),
        Player("C. Romero", 13, "DF", 8.2),
        Player("N. Otamendi", 19, "DF", 7.8),
        Player("N. Tagliafico", 3, "DF", 7.5),
        Player("R. De Paul", 7, "MF", 8.0),
        Player("E. Fernández", 24, "MF", 8.3),
        Player("A. Mac Allister", 20, "MF", 8.1),
        Player("L. Messi", 10, "FW", 9.8),
        Player("L. Martínez", 22, "FW", 8.7),
        Player("J. Álvarez", 9, "FW", 8.4)
    )

    val lineupBRA = listOf(
        Player("Alisson", 1, "GK", 8.2),
        Player("Danilo", 2, "DF", 7.2),
        Player("Marquinhos", 4, "DF", 8.1),
        Player("Gabriel", 14, "DF", 7.9),
        Player("W. Wendell", 6, "DF", 7.4),
        Player("Bruno G.", 5, "MF", 8.1),
        Player("Lucas Paquetá", 8, "MF", 7.8),
        Player("Rodrygo", 10, "FW", 8.5),
        Player("Raphinha", 11, "FW", 8.3),
        Player("Vinícius Jr.", 7, "FW", 9.4),
        Player("Endrick", 9, "FW", 8.2)
    )

    val liveMatch = Match(
        id = "match_1",
        teamA = teamArgentina,
        teamB = teamBrazil,
        scoreA = 2,
        scoreB = 1,
        status = "LIVE",
        minute = "74'",
        date = "Today",
        time = "15:00",
        stadium = "MetLife Stadium",
        city = "East Rutherford, NJ",
        possessionA = 54,
        shotsA = 12,
        shotsB = 9,
        foulsA = 11,
        foulsB = 14,
        cornersA = 6,
        cornersB = 4,
        lineupA = lineupARG,
        lineupB = lineupBRA,
        timeline = listOf(
            MatchEvent(12, "GOAL", "ARG", "L. Messi scores a breathtaking free kick from 25 yards!"),
            MatchEvent(34, "YELLOW", "BRA", "Lucas Paquetá gets booked for a sliding tackle on De Paul."),
            MatchEvent(42, "GOAL", "BRA", "Vinícius Jr. scores from a tight angle after a solo dribble."),
            MatchEvent(55, "GOAL", "ARG", "L. Martínez headers into the top corner from an Enzo corner!"),
            MatchEvent(68, "SUB", "ARG", "J. Álvarez is replaced by N. González.")
        )
    )

    val upcomingMatch = Match(
        id = "match_2",
        teamA = teamUSA,
        teamB = teamMexico,
        scoreA = 0,
        scoreB = 0,
        status = "UPCOMING",
        minute = "0'",
        date = "Tomorrow",
        time = "20:00",
        stadium = "Azteca Stadium",
        city = "Mexico City",
        possessionA = 50,
        shotsA = 0,
        shotsB = 0,
        foulsA = 0,
        foulsB = 0,
        cornersA = 0,
        cornersB = 0,
        lineupA = emptyList(),
        lineupB = emptyList(),
        timeline = emptyList()
    )

    val foodItems = listOf(
        FoodItem("food_1", "Gourmet Angus Burger", "Burger", 14.99, "Juicy beef burger with melted cheddar", 8, isHalal = true, rating = 4.8, stallName = "Star Stadium Grill"),
        FoodItem("food_2", "Spicy Jalapeno Pizza Slice", "Pizza", 7.99, "Large slice with spicy pepperoni and jalapeno", 5, isHalal = true, rating = 4.6, stallName = "FIFA Pizza Corner"),
        FoodItem("food_3", "Avocado Quinoa Bowl", "Snack", 12.50, "Fresh organic bowl with quinoa, avocado, tomatoes", 6, isVeg = true, isVegan = true, isGlutenFree = true, rating = 4.7, stallName = "Green Field Salads"),
        FoodItem("food_4", "FIFA Loaded Cheese Fries", "Snack", 8.99, "Crispy golden fries loaded with cheddar cheese sauce", 4, isVeg = true, isHalal = true, rating = 4.5, stallName = "Star Stadium Grill"),
        FoodItem("food_5", "Vegan Shawarma Wrap", "Burger", 11.00, "Spiced plant-based shawarma wrap with garlic dip", 10, isVeg = true, isVegan = true, isHalal = true, rating = 4.7, stallName = "Middle Eastern Delights"),
        FoodItem("food_6", "Golden Churros with Chocolate", "Dessert", 6.50, "Cinnamon churros with hot rich chocolate sauce", 3, isVeg = true, rating = 4.9, stallName = "Sweet Football Treats"),
        FoodItem("food_7", "Zero Sugar Power Soda", "Beverage", 4.50, "Ice cold zero sugar energy soda", 2, isVeg = true, isVegan = true, isGlutenFree = true, rating = 4.3, stallName = "FIFA Refreshments")
    )

    val parkingZones = listOf(
        ParkingZone("park_a", "Premium Zone A (West Entrance)", 300, 24, 15.00, "3 mins walk", true),
        ParkingZone("park_b", "Standard Zone B (North Gate)", 500, 185, 10.00, "7 mins walk", true),
        ParkingZone("park_c", "Economy Zone C (South Lot)", 800, 442, 6.00, "12 mins walk", false),
        ParkingZone("park_d", "VIP Reserved Lot", 100, 3, 30.00, "1 min walk", true)
    )

    val tickets = listOf(
        StadiumTicket(
            id = "t_1",
            matchTitle = "Argentina vs Brazil",
            teamAEmoji = "🇦🇷",
            teamBEmoji = "🇧🇷",
            section = "VIP Sec 102",
            row = "Row F",
            seat = "Seat 15",
            gate = "Gate B (West Gate)",
            level = "Level 2 (Club Premium)",
            qrCodePayload = "FIFA2026-TICKET-VIP-ARG-BRA-102-F-15",
            date = "Today, Jul 9",
            time = "15:00"
        ),
        StadiumTicket(
            id = "t_2",
            matchTitle = "USA vs Mexico",
            teamAEmoji = "🇺🇸",
            teamBEmoji = "🇲🇽",
            section = "Grandstand 304",
            row = "Row M",
            seat = "Seat 48",
            gate = "Gate F (East Gate)",
            level = "Level 3",
            qrCodePayload = "FIFA2026-TICKET-USA-MEX-304-M-48",
            date = "Tomorrow, Jul 10",
            time = "20:00",
            tier = "Standard General"
        )
    )

    val communityPosts = listOf(
        CommunityPost("p_1", "Diego_74", "Argentina", "🇦🇷", "What an unbelievable match! Messi's free kick was poetry in motion. Let's hold onto this 2-1 lead! VAMOS ARGENTINA!!! 🇦🇷🔥", "12m ago", 143, "ARG 3 - 1 BRA"),
        CommunityPost("p_2", "ViniFan_2026", "Brazil", "🇧🇷", "We are pressing high in the second half. Marquinhos got so close! Vinicius can definitely bag another one. SUCESSO SELEÇÃO! 🇧🇷⚡", "8m ago", 98, "ARG 2 - 2 BRA"),
        CommunityPost("p_3", "SamSoccerUSA", "USA", "🇺🇸", "MetLife stadium is absolutely packed today. The atmosphere is top tier. Ready for the next clash tomorrow. USA USA USA!!! 🇺🇸⚽", "2m ago", 54)
    )

    val lostItems = listOf(
        LostFoundItem("l_1", "Black Leather Wallet", "Contains driver license and stadium season card, lost near Gate B restrooms.", "Gate B Level 1", "Jul 9, 13:45", true, "Pending"),
        LostFoundItem("l_2", "iPhone 15 Pro", "Silver titanium, has a clear case with a FIFA sticker. Found in seating block 104.", "Block 104 Row G", "Jul 9, 14:10", false, "Matched"),
        LostFoundItem("l_3", "Sling Bag", "Adidas grey bag with car keys inside. Lost near Parking Lot A parking meter.", "Parking Lot A", "Jul 9, 11:30", true, "Claimed")
    )

    val globalStadiums = listOf(
        Stadium(
            id = "metlife",
            name = "MetLife Stadium",
            city = "East Rutherford, NJ",
            country = "USA",
            tournament = "FIFA World Cup 2026",
            league = "MLS / NFL",
            homeTeam = "USA National Team / NY Giants",
            capacity = 82500,
            imageResName = "stadium_metlife",
            gates = listOf("Gate A (East)", "Gate B (West)", "Gate C (North)", "Gate D (South)"),
            parkingLots = listOf("Lot A Premium", "Lot B Standard", "Lot C Economy", "VIP Gold Lot"),
            foodCourts = listOf("Star Stadium Grill", "FIFA Pizza Corner", "Green Field Salads", "Tacorito Express"),
            restrooms = listOf("Restrooms Sec 102", "Restrooms Sec 115", "Restrooms Sec 204", "Restrooms Sec 318"),
            vipLounges = listOf("Platinum Club", "Executive Suite 10", "Gold Lounge"),
            prayerRooms = listOf("Interfaith Room Level 1", "Prayer Hall Gate B"),
            merchandiseStores = listOf("FIFA Megastore", "Fan Zone Corner", "Retro Football Shop"),
            accessibilityFacilities = listOf("Wheelchair ramps at all gates", "Tactile paving paths", "Assistance elevators to all tiers", "Seating section companion spaces"),
            publicTransport = listOf("NJ Transit Rail (MetLife Station)", "Bus Route 351 Express"),
            openingHours = "09:00 AM - 11:00 PM"
        ),
        Stadium(
            id = "wembley",
            name = "Wembley Stadium",
            city = "London",
            country = "United Kingdom",
            tournament = "UEFA Euro / FA Cup",
            league = "English Premier League",
            homeTeam = "England National Team",
            capacity = 90000,
            imageResName = "stadium_wembley",
            gates = listOf("Gate J (Main Entrance)", "Gate P (East)", "Gate G (North)", "Gate K (West)"),
            parkingLots = listOf("Red Parking (Premium)", "Green Parking (Standard)", "Blue Parking (EV Charging)"),
            foodCourts = listOf("The Wembley Burger Co.", "Traditional Fish & Chips", "Pie & Pint Station", "Vegan Hub"),
            restrooms = listOf("Restrooms block 104", "Restrooms block 120", "Restrooms block 215"),
            vipLounges = listOf("Bobby Moore Lounge", "The Atrium", "One Twenty Club"),
            prayerRooms = listOf("Multi-Faith Room Level 2 West"),
            merchandiseStores = listOf("England Football Store", "Wembley Retail Mall"),
            accessibilityFacilities = listOf("Step-free access to all levels", "Changing Places toilet facilities", "Audio descriptive commentary units"),
            publicTransport = listOf("London Underground (Wembley Park Station)", "Overground (Wembley Central)", "National Rail"),
            openingHours = "10:00 AM - 11:30 PM"
        ),
        Stadium(
            id = "lusail",
            name = "Lusail Stadium",
            city = "Doha",
            country = "Qatar",
            tournament = "AFC Asian Cup / FIFA 2022",
            league = "Qatar Stars League",
            homeTeam = "Qatar National Team",
            capacity = 88966,
            imageResName = "stadium_lusail",
            gates = listOf("Entrance 1 (North)", "Entrance 2 (South)", "VIP Entrance West"),
            parkingLots = listOf("Zone A VIP", "Zone B Standard", "Zone C Extended"),
            foodCourts = listOf("Middle Eastern Delights", "Al-Sadd Shawarma", "Doha Dessert Oasis", "World Burger Cup"),
            restrooms = listOf("Restrooms gate 1", "Restrooms gate 5", "Restrooms level 1 west"),
            vipLounges = listOf("Royal Pearl Suite", "The Oasis Club", "Gold Al-Bayt Lounge"),
            prayerRooms = listOf("Men's Prayer Room Level 1", "Women's Prayer Room Level 1", "Prayer Room Level 2"),
            merchandiseStores = listOf("Lusail Megastore", "Qatar Football Shop"),
            accessibilityFacilities = listOf("Accessible shuttle carts", "Elevated viewing platforms", "Tactile indoor navigation"),
            publicTransport = listOf("Doha Metro (Lusail QNB Station)", "Stadium Shuttle Bus Route 10"),
            openingHours = "08:00 AM - 12:00 AM"
        ),
        Stadium(
            id = "campnou",
            name = "Spotify Camp Nou",
            city = "Barcelona",
            country = "Spain",
            tournament = "UEFA Champions League",
            league = "La Liga",
            homeTeam = "FC Barcelona",
            capacity = 99354,
            imageResName = "stadium_campnou",
            gates = listOf("Access 9 (North)", "Access 15 (Tribuna)", "Access 4 (Lateral)", "Access 21 (Gol Nord)"),
            parkingLots = listOf("Main Club Lot", "Lateral Parking", "Travessera de les Corts Public Lot"),
            foodCourts = listOf("Tapas & Football", "Barça Cafe", "La Masia Bocadillo Station", "Estrella Damm Corner"),
            restrooms = listOf("Restrooms sector 112", "Restrooms sector 233", "Restrooms sector 301"),
            vipLounges = listOf("Presidential Box", "Roma Hall Lounge", "Barça VIP Star"),
            prayerRooms = listOf("Multi-faith meditation room Level 1"),
            merchandiseStores = listOf("Barça Store Camp Nou (Mega)", "Gol Nord Merch kiosk"),
            accessibilityFacilities = listOf("Adapted stadium tour access", "Wheelchair bays with companion seats", "Dedicated accessibility assistants"),
            publicTransport = listOf("Metro Line 3 (Les Corts)", "Metro Line 5 (Collblanc)", "TRAM T1/T2/T3"),
            openingHours = "09:30 AM - 10:30 PM"
        ),
        Stadium(
            id = "bernabeu",
            name = "Santiago Bernabéu",
            city = "Madrid",
            country = "Spain",
            tournament = "UEFA Champions League",
            league = "La Liga",
            homeTeam = "Real Madrid CF",
            capacity = 81044,
            imageResName = "stadium_bernabeu",
            gates = listOf("Gate 7 (Castellana)", "Gate 20 (Padre Damián)", "Gate 44 (Concha Espina)"),
            parkingLots = listOf("Underground Bernabéu Lot", "Castellana VIP Valet", "Plaza de los Sagrados Corazones Lot"),
            foodCourts = listOf("Real Madrid Cafe", "La Esquina Bernabéu Grill", "Gourmet Tapas", "Churros Corner"),
            restrooms = listOf("Restrooms block 105", "Restrooms block 210", "Restrooms block 314"),
            vipLounges = listOf("Copa de Europa Room", "Palco de Honor", "Bernabéu 360 Lounge"),
            prayerRooms = listOf("Meditation and Interfaith Center Gate 12"),
            merchandiseStores = listOf("Real Madrid Official Store (New)", "Castellana kiosk"),
            accessibilityFacilities = listOf("Retractable roof climate assist", "Audio guide devices", "Step-free ramped entrances"),
            publicTransport = listOf("Metro Line 10 (Santiago Bernabéu Station)", "Bus Route 14, 27, 43, 120, 150"),
            openingHours = "09:00 AM - 11:00 PM"
        ),
        Stadium(
            id = "azteca",
            name = "Estadio Azteca",
            city = "Mexico City",
            country = "Mexico",
            tournament = "FIFA World Cup 2026",
            league = "Liga MX",
            homeTeam = "Club América / Mexico National Team",
            capacity = 87523,
            imageResName = "stadium_azteca",
            gates = listOf("Acceso Principal A", "Acceso B (Insurgentes)", "Acceso C (Tlalpan)", "Plaza de Toros Gate"),
            parkingLots = listOf("Estacionamiento General 1", "Estacionamiento Preferente West", "VIP Plateas Lot"),
            foodCourts = listOf("El Azteca Taco Corner", "Corona Beer Garden", "Quesadillas & Tortas", "Stadium Hotdogs"),
            restrooms = listOf("Restrooms rampa 1", "Restrooms rampa 3", "Restrooms zona especial 2"),
            vipLounges = listOf("Club Seat Executive Lounge", "Palcos VIP Imperial"),
            prayerRooms = listOf("Capilla de la Virgen de Guadalupe"),
            merchandiseStores = listOf("Tienda Oficial Club América", "Tienda Selección Mexicana"),
            accessibilityFacilities = listOf("Accessible vehicle parking drop-offs", "Lowered ticket counters", "Ramped seating platforms"),
            publicTransport = listOf("Tren Ligero (Estadio Azteca Station)", "Tlalpan Bus Rapid Transit"),
            openingHours = "10:00 AM - 10:00 PM"
        ),
        Stadium(
            id = "maracana",
            name = "Maracanã Stadium",
            city = "Rio de Janeiro",
            country = "Brazil",
            tournament = "Copa América / FIFA 2014",
            league = "Campeonato Brasileiro",
            homeTeam = "Flamengo / Fluminense",
            capacity = 78838,
            imageResName = "stadium_maracana",
            gates = listOf("Portão A (Radial Oeste)", "Portão B (Mata Machado)", "Portão C (Eurico Rabelo)", "Portão D"),
            parkingLots = listOf("Estacionamento Maracanã Premium", "Lot B Radial Oeste", "Uerj University Public Lot"),
            foodCourts = listOf("Rio Samba Burgers", "Coxinha & Pastéis Express", "Pão de Queijo Corner", "Ipanema Refreshments"),
            restrooms = listOf("Restrooms portão A", "Restrooms portão C", "Restrooms setor leste"),
            vipLounges = listOf("Maracanã Mais Lounge", "Tribuna de Honra"),
            prayerRooms = listOf("Meditation Hall Level 2 East"),
            merchandiseStores = listOf("Espaço Rubro-Negro", "Loja Oficial Fluminense", "Maracanã Store"),
            accessibilityFacilities = listOf("Ramps with sensory guidelines", "Adapted wheelchair companion seating", "Accessible priority turnstiles"),
            publicTransport = listOf("SuperVia Train (Maracanã Station)", "Metro Line 2 (Maracanã / São Cristóvão)"),
            openingHours = "09:00 AM - 11:30 PM"
        ),
        Stadium(
            id = "allianz",
            name = "Allianz Arena",
            city = "Munich",
            country = "Germany",
            tournament = "UEFA Champions League / Euro",
            league = "Bundesliga",
            homeTeam = "FC Bayern Munich",
            capacity = 75024,
            imageResName = "stadium_allianz",
            gates = listOf("Kassenhaus Nord", "Kassenhaus Süd", "Sonderzugang VIP Ost"),
            parkingLots = listOf("Parkhaus S1 (VIP)", "Parkhaus P1-P4 (Standard)", "Busparkplatz Nord"),
            foodCourts = listOf("Arena Bistro", "FC Bayern Clubhouse Kitchen", "German Bratwurst Express", "Pretzel & Beer Corner"),
            restrooms = listOf("Restrooms ebene 2", "Restrooms ebene 6", "Restrooms block 112"),
            vipLounges = listOf("Sponsoren Lounge", "Business Club", "Allianz Lounge"),
            prayerRooms = listOf("Allianz Meditation & Prayer Room"),
            merchandiseStores = listOf("FC Bayern Megastore", "Arena Bistro Fan Corner"),
            accessibilityFacilities = listOf("Elevator priority tags", "Audio guide headset counters", "Barrier-free access pathways"),
            publicTransport = listOf("U-Bahn U6 (Fröttmaning Station)", "Shuttle Bus Munich Center"),
            openingHours = "10:00 AM - 11:00 PM"
        )
    )
}

data class Stadium(
    val id: String,
    val name: String,
    val city: String,
    val country: String,
    val tournament: String,
    val league: String,
    val homeTeam: String,
    val capacity: Int,
    val imageResName: String,
    val gates: List<String>,
    val parkingLots: List<String>,
    val foodCourts: List<String>,
    val restrooms: List<String>,
    val vipLounges: List<String>,
    val prayerRooms: List<String>,
    val merchandiseStores: List<String>,
    val accessibilityFacilities: List<String>,
    val publicTransport: List<String>,
    val openingHours: String
)

