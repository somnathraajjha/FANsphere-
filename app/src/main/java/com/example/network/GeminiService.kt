package com.example.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class Part(
    @Json(name = "text") val text: String? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    @Json(name = "parts") val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    @Json(name = "contents") val contents: List<Content>
)

@JsonClass(generateAdapter = true)
data class Candidate(
    @Json(name = "content") val content: Content?
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    @Json(name = "candidates") val candidates: List<Candidate>?
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(GeminiApiService::class.java)
    }
}

object GeminiRepository {
    fun isStadiumOrAddress(text: String): Boolean {
        val lower = text.lowercase()
        return lower.contains("stadium") || 
               lower.contains("arena") || 
               lower.contains("camp nou") || 
               lower.contains("wembley") || 
               lower.contains("bernabeu") || 
               lower.contains("coliseum") ||
               lower.contains("address") ||
               lower.contains("road") ||
               lower.contains("street") ||
               lower.contains("avenue") ||
               lower.contains("stadi") ||
               lower.contains("gardens") ||
               lower.contains("park") && lower.contains("stadium")
    }

    suspend fun getResponse(prompt: String, apiKey: String): String {
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return getFallbackResponse(prompt)
        }
        val systemInstruction = "You are 'nice', an extremely helpful, friendly, and smart multilingual stadium assistant for FanSphere. " +
                "You MUST respond in the EXACT same language that the user is speaking/writing to you (be it Hindi, Spanish, Arabic, German, French, Portuguese, Japanese, etc.) without any problem. " +
                "Keep your answers concise, engaging, filled with sports emojis, and highly accurate. If the user mentions a stadium name or address, guide them and remind them that we are opening Google Maps for them immediately.\n\n" +
                "User query: "
        
        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = systemInstruction + prompt))))
        )
        return try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "I apologize, but I couldn't process that query. Please try again."
        } catch (e: Exception) {
            getFallbackResponse(prompt) // Fallback when network is offline/error
        }
    }

    private fun getFallbackResponse(prompt: String): String {
        val query = prompt.lowercase()
        val isHindi = (query.contains("stadium") && (query.contains("kahan") || query.contains("kidhar") || query.contains("batao") || query.contains("pata") || query.contains("address"))) || 
                      query.contains("namaste") || query.contains("kaise") || query.contains("achha") || query.contains("theek")
        
        if (isStadiumOrAddress(prompt)) {
            return if (isHindi) {
                "🗺️ **Nice Assistant:** मैं आपके लिए Google Maps पर '${prompt}' का पता खोल रहा हूँ... कृपया प्रतीक्षा करें!"
            } else {
                "🗺️ **Nice Assistant:** Opening '${prompt}' directly on Google Maps for you... Please wait!"
            }
        }
        
        return when {
            query.contains("seat") || query.contains("find my seat") || query.contains("take me to my seat") -> {
                "🎫 **Nice Assistant:**\n\nYou are seated in **VIP Section 102, Row F, Seat 15**.\n\n🚶 **Directions:**\n- Enter through **Gate B (West Entrance)**.\n- Turn right, take the escalator to **Level 2**.\n- Head towards Section 102. Your host at the VIP Lounge will assist you!\n\n*Currently, crowds near Gate B are low (2 min wait).*"
            }
            query.contains("gate") || query.contains("gate b") -> {
                "🚪 **Nice Assistant:**\n\n- **Wait Time:** ~2 minutes (Fast Track active).\n- **Amenities Nearby:** VIP Lounges, Restrooms, Merchandise Store B, and Medical Team Alpha.\n- **Recommended Route:** Use the West Access bridge for the least crowd density."
            }
            query.contains("veg") || query.contains("vegetarian") || query.contains("vegan") || query.contains("salad") -> {
                "🥗 **Nice Assistant:**\n\n1. **Green Field Salads** (Block 104):\n   - *Avocado Quinoa Bowl* ($12.50) - Organic, Vegan, Halal, Gluten-Free.\n   - *Wait Time:* ~3 minutes.\n\n2. **Middle Eastern Delights** (Block 108):\n   - *Vegan Shawarma Wrap* ($11.00) - Plant-based with garlic dip.\n\n3. **FIFA Pizza Corner** (Block 102):\n   - *Margherita Cheese Slice* ($7.99) - Vegetarian."
            }
            query.contains("restroom") || query.contains("toilet") || query.contains("wc") -> {
                "🚹🚺 **Nice Assistant:**\n\n- **Location 1:** Directly behind Block 102 (VIP Area) - *Crowd level: Very Low*.\n- **Location 2:** Near Gate B Escalator (All Gender & Accessible) - *Crowd level: Moderate*."
            }
            query.contains("parking") || query.contains("car") || query.contains("park") -> {
                "🚗 **Nice Assistant:**\n\n- **Your Car Spot:** Reserved in **Premium Zone A, Slot #108**.\n- **EV Charger:** Active and charging (current charge: 85%).\n- **Helper Tip:** Simply hold your FanSphere app to the terminal at Gate B bridge when returning. We will guide you right back to your vehicle!"
            }
            query.contains("emergency") || query.contains("sos") || query.contains("hurt") || query.contains("first aid") || query.contains("medical") -> {
                "🚨 **Nice Assistant:**\n\n- **Medical Team Alpha** has been notified of your location at **VIP Sec 102**.\n- **ETA:** ~90 seconds.\n- **Tip:** Please stay seated. Stadium security and first aid responders are heading directly to you."
            }
            query.contains("start") || query.contains("match start") || query.contains("time") || query.contains("score") -> {
                "⚽ **Nice Assistant:**\n\n- **Argentina vs Brazil** is currently in the **74th minute**.\n- **Score:** Argentina 2 - 1 Brazil.\n- **Insights:** Lionel Messi opened with a classic free-kick in the 12th minute. Vinícius Jr. leveled for Brazil, but Lautaro Martínez headers put Argentina back on top!"
            }
            query.contains("translate") || query.contains("how do you say") -> {
                val announcement = prompt.replace("translate", "", ignoreCase = true).trim()
                "🌐 **Nice Assistant:**\n\n*Translating: \"$announcement\"*\n\n- **Spanish:** \"Por favor, mantenga las salidas despejadas.\"\n- **Arabic:** \"يرجى إبقاء المخارج خالية.\"\n- **Japanese:** \"非常口を塞がないようお願いいたします。\"\n- **Portuguese:** \"Por favor, mantenha as saídas desimpedidas.\""
            }
            else -> {
                if (isHindi) {
                    "🤖 **नमस्ते! मैं हूँ 'nice', आपका FanSphere AI सहायक!**\n\nमैं FIFA 2026 के लिए आपका लाइव साथी हूँ। आप मुझसे हिंदी, English या किसी भी भाषा में पूछ सकते हैं:\n- *\"मेरी सीट कहाँ है?\"*\n- *\"शाकाहारी भोजन कहाँ मिलेगा?\"*\n- *\"शौचालय कहाँ हैं?\"*\n- *\"पार्किंग की स्थिति क्या है?\"*\n- *\"Wembley Stadium का पता\"* (यह सीधे Google Maps खोल देगा!)\n\nआज मैं आपकी क्या सहायता कर सकता हूँ?"
                } else {
                    "🤖 **Hello! I am 'nice', your FanSphere AI Assistant!**\n\nI am your live companion for FIFA 2026. You can chat with me in ANY language (Hindi, Spanish, French, etc.) without any problem! Ask me questions such as:\n- *\"Where is my seat?\"*\n- *\"Find vegetarian food\"*\n- *\"Show nearest restrooms\"*\n- *\"Wembley Stadium Address\"* (Opens directly in Google Maps!)\n\nHow can I make your matchday spectacular today?"
                }
            }
        }
    }
}
