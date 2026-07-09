package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FoodItem
import com.example.data.Language
import com.example.data.LocalizedStrings
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel
import java.util.Locale

@Composable
fun FoodBeverageScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.currentLanguage.collectAsState()
    val foodItems by viewModel.foodItems.collectAsState()
    val foodCart by viewModel.foodCart.collectAsState()
    val activeFilter by viewModel.foodFilter.collectAsState()

    var checkoutComplete by remember { mutableStateOf(false) }
    var pickupMinutesSimulated by remember { mutableStateOf(0) }

    // Filter items based on selected category
    val filteredItems = foodItems.filter { item ->
        when (activeFilter) {
            "All" -> true
            "Veg" -> item.isVeg
            "Vegan" -> item.isVegan
            "Halal" -> item.isHalal
            "Gluten Free" -> item.isGlutenFree
            else -> true
        }
    }

    val cartItemsList = foodCart.entries.toList()
    val subtotal = foodCart.entries.sumOf { it.key.price * it.value }
    val serviceFee = if (subtotal > 0) 1.50 else 0.00
    val total = subtotal + serviceFee

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ElegantDarkBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // --- RESTAURANT FILTER ROW ---
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filtersList = listOf("All", "Veg", "Vegan", "Halal", "Gluten Free")
                items(filtersList) { filter ->
                    val isSelected = filter == activeFilter
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
                            .clickable { viewModel.setFoodFilter(filter) }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = filter,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.Black else Color.White
                        )
                    }
                }
            }

            // --- MAIN FOOD ITEMS LIST ---
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Success banner if checked out
                if (checkoutComplete) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize(),
                            colors = CardDefaults.cardColors(containerColor = TurfGreen.copy(alpha = 0.15f)),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, TurfGreen.copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Paid", tint = TurfGreen)
                                    Text("ORDER COMPLETED SUCCESSFULLY", fontWeight = FontWeight.Bold, color = TurfGreen, fontSize = 14.sp)
                                }
                                Text(
                                    text = "Your cashless pay transaction of $${String.format(Locale.getDefault(), "%.2f", total)} was processed secure.",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.8f),
                                    textAlign = TextAlign.Center
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White.copy(alpha = 0.08f))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "Pickup at Star Stadium Grill in $pickupMinutesSimulated mins (Fast lane)",
                                        color = ElegantDarkSecondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                                Button(
                                    onClick = { checkoutComplete = false },
                                    colors = ButtonDefaults.buttonColors(containerColor = TurfGreen),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Order Again", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                items(filteredItems) { item ->
                    val inCartCount = foodCart[item] ?: 0
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = ElegantDarkSurface),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, ElegantDarkBorderSubtle)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Food details column
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = item.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    // Food category markers
                                    if (item.isVeg) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .border(1.dp, TurfGreen)
                                                .padding(2.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(TurfGreen)
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = item.stallName,
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.5f)
                                )

                                Text(
                                    text = item.imageDescription,
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.6f),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "$${item.price}",
                                        fontWeight = FontWeight.Bold,
                                        color = ElegantDarkPrimary,
                                        fontSize = 14.sp
                                    )

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color.White.copy(alpha = 0.05f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(Icons.Default.AccessTime, contentDescription = "Queue wait", tint = ElegantDarkSecondary, modifier = Modifier.size(10.dp))
                                            Text("${item.waitingTimeMinutes} mins wait", color = ElegantDarkSecondary, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }
                            }

                            // Add/remove cart controls
                            if (inCartCount > 0) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White.copy(alpha = 0.08f))
                                        .padding(4.dp)
                                ) {
                                    IconButton(
                                        onClick = { viewModel.removeFoodFromCart(item) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(14.dp))
                                    }

                                    Text(
                                        text = inCartCount.toString(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )

                                    IconButton(
                                        onClick = { viewModel.addFoodToCart(item) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(14.dp))
                                    }
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.addFoodToCart(item) },
                                    colors = ButtonDefaults.buttonColors(containerColor = ElegantDarkPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("add_to_cart_${item.id}")
                                ) {
                                    Text("Add", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(100.dp)) // Extra space to scroll above cart bottom drawer
                }
            }
        }

        // --- STICKY BOTTOM CART DRAWER ---
        AnimatedVisibility(
            visible = foodCart.isNotEmpty(),
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(1.dp, ElegantDarkBorder), RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
                colors = CardDefaults.cardColors(containerColor = ElegantDarkSurface),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Checkout Order",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = ElegantDarkSecondary
                        )

                        TextButton(onClick = { viewModel.clearCart() }) {
                            Text("Clear All", color = EmergencyCoral, fontSize = 12.sp)
                        }
                    }

                    // Price totals
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Subtotal", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                            Text("Secure Terminal fee", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("$${String.format(Locale.getDefault(), "%.2f", subtotal)}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("$${String.format(Locale.getDefault(), "%.2f", serviceFee)}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Divider(color = Color.White.copy(alpha = 0.12f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Amount", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                        Text(
                            text = "$${String.format(Locale.getDefault(), "%.2f", total)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = ElegantDarkPrimary
                        )
                    }

                    // Pay Now Action Button
                    Button(
                        onClick = {
                            pickupMinutesSimulated = foodCart.keys.maxOfOrNull { it.waitingTimeMinutes } ?: 5
                            viewModel.clearCart()
                            checkoutComplete = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("checkout_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = ElegantDarkPrimary),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.CreditCard, contentDescription = "Pay Secure", tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pay Now Cashless", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)
                    }
                }
            }
        }
    }
}
