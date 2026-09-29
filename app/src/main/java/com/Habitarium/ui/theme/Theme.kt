package com.example.Habitarium.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ─── Paleta Habitarium ───────────────────────────────────────────────────────

val BrandGreen        = Color(0xFF1D9E75)
val BrandGreenLight   = Color(0xFFE1F5EE)
val BrandGreenDark    = Color(0xFF0F6E56)
val BrandGreenMid     = Color(0xFF9FE1CB)

val SponsoredAmber       = Color(0xFFFAEEDA)
val SponsoredAmberDark   = Color(0xFF854F0B)
val SponsoredAmberBorder = Color(0xFFFAC775)

val NeutralSurface = Color(0xFFF8F8F8)
val NeutralBorder  = Color(0xFFE0E0E0)
val NeutralMuted   = Color(0xFF9E9E9E)

// ─── Esquemas de cor ─────────────────────────────────────────────────────────

private val LightColors = lightColorScheme(
    primary             = BrandGreen,
    onPrimary           = Color.White,
    primaryContainer    = BrandGreenLight,
    onPrimaryContainer  = BrandGreenDark,
    secondary           = BrandGreenMid,
    surface             = Color.White,
    onSurface           = Color(0xFF1C1C1C),
    background          = NeutralSurface,
    onBackground        = Color(0xFF1C1C1C),
    outline             = NeutralBorder
)

private val DarkColors = darkColorScheme(
    primary             = BrandGreenMid,
    onPrimary           = BrandGreenDark,
    primaryContainer    = BrandGreenDark,
    onPrimaryContainer  = BrandGreenLight,
    surface             = Color(0xFF1E1E1E),
    onSurface           = Color(0xFFECECEC),
    background          = Color(0xFF121212),
    onBackground        = Color(0xFFECECEC),
    outline             = Color(0xFF3A3A3A)
)

// ─── Tema principal ──────────────────────────────────────────────────────────

@Composable
fun HabitariumTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography  = HabitariumTypography,
        content     = content
    )
}