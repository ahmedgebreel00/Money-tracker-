package com.example.moneytracker.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Celebration
import androidx.compose.material.icons.rounded.ChildCare
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Laptop
import androidx.compose.material.icons.rounded.LocalCafe
import androidx.compose.material.icons.rounded.LocalGasStation
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.MedicalServices
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Park
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material.icons.rounded.Work
import androidx.compose.ui.graphics.vector.ImageVector

/** Icons a category can use. The map key is what gets stored in the database. */
object CategoryIcons {
    val all: Map<String, ImageVector> = linkedMapOf(
        "restaurant" to Icons.Rounded.Restaurant,
        "cafe" to Icons.Rounded.LocalCafe,
        "cart" to Icons.Rounded.ShoppingCart,
        "bag" to Icons.Rounded.ShoppingBag,
        "car" to Icons.Rounded.DirectionsCar,
        "fuel" to Icons.Rounded.LocalGasStation,
        "flight" to Icons.Rounded.Flight,
        "home" to Icons.Rounded.Home,
        "bolt" to Icons.Rounded.Bolt,
        "wifi" to Icons.Rounded.Wifi,
        "phone" to Icons.Rounded.PhoneAndroid,
        "receipt" to Icons.Rounded.Receipt,
        "hospital" to Icons.Rounded.LocalHospital,
        "medical" to Icons.Rounded.MedicalServices,
        "fitness" to Icons.Rounded.FitnessCenter,
        "school" to Icons.Rounded.School,
        "book" to Icons.Rounded.Book,
        "movie" to Icons.Rounded.Movie,
        "games" to Icons.Rounded.SportsEsports,
        "music" to Icons.Rounded.MusicNote,
        "party" to Icons.Rounded.Celebration,
        "clothes" to Icons.Rounded.Checkroom,
        "pets" to Icons.Rounded.Pets,
        "kids" to Icons.Rounded.ChildCare,
        "park" to Icons.Rounded.Park,
        "tools" to Icons.Rounded.Build,
        "art" to Icons.Rounded.Brush,
        "gift" to Icons.Rounded.CardGiftcard,
        "work" to Icons.Rounded.Work,
        "laptop" to Icons.Rounded.Laptop,
        "payments" to Icons.Rounded.Payments,
        "money" to Icons.Rounded.AttachMoney,
        "bank" to Icons.Rounded.AccountBalance,
        "savings" to Icons.Rounded.Savings,
        "trending" to Icons.Rounded.TrendingUp,
        "category" to Icons.Rounded.Category
    )

    fun get(key: String?): ImageVector = all[key] ?: Icons.Rounded.Category
}

/** Colors offered in the category editor (ARGB ints). */
val CategoryColors: List<Int> = listOf(
    0xFFEF4444, 0xFFF97316, 0xFFEAB308, 0xFF22C55E,
    0xFF14B8A6, 0xFF06B6D4, 0xFF3B82F6, 0xFF6366F1,
    0xFF8B5CF6, 0xFFEC4899, 0xFFF43F5E, 0xFF64748B
).map { it.toInt() }
