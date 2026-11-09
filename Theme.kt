package com.example.moneytracker.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.moneytracker.data.DarkMode

/** A selectable color theme. Light and dark schemes are both derived from these few colors. */
data class AppPalette(
    val id: String,
    val name: String,
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val lightBackground: Color,
    val darkBackground: Color,
    val darkSurface: Color
)

val Palettes: List<AppPalette> = listOf(
    AppPalette(
        "ocean", "Ocean",
        Color(0xFF0E7490), Color(0xFF0369A1), Color(0xFF0D9488),
        Color(0xFFF2F8FA), Color(0xFF0B1418), Color(0xFF14232A)
    ),
    AppPalette(
        "forest", "Forest",
        Color(0xFF2E7D32), Color(0xFF558B2F), Color(0xFF00796B),
        Color(0xFFF4F8F3), Color(0xFF0C150D), Color(0xFF16241A)
    ),
    AppPalette(
        "sunset", "Sunset",
        Color(0xFFE65100), Color(0xFFD84315), Color(0xFFF9A825),
        Color(0xFFFFF7F0), Color(0xFF1A0F0A), Color(0xFF2A1A12)
    ),
    AppPalette(
        "royal", "Royal",
        Color(0xFF6D28D9), Color(0xFF7C3AED), Color(0xFFDB2777),
        Color(0xFFF8F5FF), Color(0xFF120D1F), Color(0xFF1D1633)
    ),
    AppPalette(
        "rose", "Rose",
        Color(0xFFBE185D), Color(0xFFE11D48), Color(0xFFC2410C),
        Color(0xFFFFF5F8), Color(0xFF1A0C13), Color(0xFF2A1520)
    ),
    AppPalette(
        "midnight", "Midnight",
        Color(0xFF2563EB), Color(0xFF4F46E5), Color(0xFF059669),
        Color(0xFFF3F6FB), Color(0xFF000000), Color(0xFF0D1117)
    )
)

private fun AppPalette.toScheme(dark: Boolean): ColorScheme {
    val white = Color.White
    val black = Color.Black
    return if (!dark) {
        lightColorScheme(
            primary = primary,
            onPrimary = white,
            primaryContainer = lerp(primary, white, 0.82f),
            onPrimaryContainer = lerp(primary, black, 0.55f),
            secondary = secondary,
            onSecondary = white,
            secondaryContainer = lerp(secondary, white, 0.85f),
            onSecondaryContainer = lerp(secondary, black, 0.55f),
            tertiary = tertiary,
            onTertiary = white,
            tertiaryContainer = lerp(tertiary, white, 0.85f),
            onTertiaryContainer = lerp(tertiary, black, 0.55f),
            background = lightBackground,
            onBackground = Color(0xFF191C1D),
            surface = white,
            onSurface = Color(0xFF191C1D),
            surfaceVariant = lerp(primary, white, 0.90f),
            onSurfaceVariant = Color(0xFF444B4E),
            outline = Color(0xFF7A8386),
            outlineVariant = lerp(primary, white, 0.80f),
            surfaceContainerLowest = white,
            surfaceContainerLow = white,
            surfaceContainer = lerp(primary, white, 0.94f),
            surfaceContainerHigh = lerp(primary, white, 0.91f),
            surfaceContainerHighest = lerp(primary, white, 0.88f)
        )
    } else {
        darkColorScheme(
            primary = lerp(primary, white, 0.40f),
            onPrimary = lerp(primary, black, 0.70f),
            primaryContainer = lerp(primary, black, 0.40f),
            onPrimaryContainer = lerp(primary, white, 0.85f),
            secondary = lerp(secondary, white, 0.40f),
            onSecondary = lerp(secondary, black, 0.70f),
            secondaryContainer = lerp(secondary, black, 0.45f),
            onSecondaryContainer = lerp(secondary, white, 0.85f),
            tertiary = lerp(tertiary, white, 0.40f),
            onTertiary = lerp(tertiary, black, 0.70f),
            tertiaryContainer = lerp(tertiary, black, 0.45f),
            onTertiaryContainer = lerp(tertiary, white, 0.85f),
            background = darkBackground,
            onBackground = Color(0xFFE6EAEB),
            surface = darkSurface,
            onSurface = Color(0xFFE6EAEB),
            surfaceVariant = lerp(darkSurface, white, 0.10f),
            onSurfaceVariant = Color(0xFFB4BCBF),
            outline = Color(0xFF7F898C),
            outlineVariant = lerp(darkSurface, white, 0.16f),
            surfaceContainerLowest = darkBackground,
            surfaceContainerLow = darkSurface,
            surfaceContainer = lerp(darkSurface, white, 0.04f),
            surfaceContainerHigh = lerp(darkSurface, white, 0.07f),
            surfaceContainerHighest = lerp(darkSurface, white, 0.10f)
        )
    }
}

/** Colors with a fixed meaning (green = money in, red = money out) regardless of the theme. */
@Immutable
data class MoneyColors(val income: Color, val expense: Color)

val LocalMoneyColors = staticCompositionLocalOf {
    MoneyColors(income = Color(0xFF1B8A4B), expense = Color(0xFFC62828))
}

private val base = Typography()

val AppTypography = base.copy(
    headlineLarge = base.headlineLarge.copy(fontWeight = FontWeight.Bold),
    headlineMedium = base.headlineMedium.copy(fontWeight = FontWeight.Bold),
    headlineSmall = base.headlineSmall.copy(fontWeight = FontWeight.Bold),
    titleLarge = base.titleLarge.copy(fontWeight = FontWeight.SemiBold),
    titleMedium = base.titleMedium.copy(fontWeight = FontWeight.SemiBold)
)

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun MoneyTrackerTheme(
    themeId: String,
    darkMode: DarkMode,
    dynamicColor: Boolean,
    content: @Composable () -> Unit
) {
    val dark = when (darkMode) {
        DarkMode.SYSTEM -> isSystemInDarkTheme()
        DarkMode.LIGHT -> false
        DarkMode.DARK -> true
    }
    val palette = Palettes.firstOrNull { it.id == themeId } ?: Palettes.first()
    val context = LocalContext.current

    val colorScheme = if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        palette.toScheme(dark)
    }

    val moneyColors = if (dark) {
        MoneyColors(income = Color(0xFF5FD38D), expense = Color(0xFFFF8A80))
    } else {
        MoneyColors(income = Color(0xFF1B8A4B), expense = Color(0xFFC62828))
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !dark
            controller.isAppearanceLightNavigationBars = !dark
        }
    }

    CompositionLocalProvider(LocalMoneyColors provides moneyColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content
        )
    }
}
