package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4ADE80),
    onPrimary = Color(0xFF003915),
    primaryContainer = Color(0xFF005322),
    onPrimaryContainer = Color(0xFF70FA98),
    secondary = Color(0xFF90CDF4),
    onSecondary = Color(0xFF0B2942),
    secondaryContainer = Color(0xFF173653),
    onSecondaryContainer = Color(0xFFD6E4FF),
    background = Color(0xFF111413),
    surface = Color(0xFF1A1D1C),
    onBackground = Color(0xFFE2E3E1),
    onSurface = Color(0xFFE2E3E1),
    surfaceVariant = Color(0xFF262C29),
    onSurfaceVariant = Color(0xFFC2C8C4),
    outline = Color(0xFF8B938D)
)

private val LightColorScheme = lightColorScheme(
    primary = IHCPrimaryGreen,
    onPrimary = Color.White,
    primaryContainer = IHCPrimaryGreenLight,
    onPrimaryContainer = IHCPrimaryGreenDark,
    secondary = IHCNavyBlue,
    onSecondary = Color.White,
    secondaryContainer = IHCNavyBlueLight,
    onSecondaryContainer = IHCNavyBlueDark,
    background = IHCBackground,
    surface = IHCSurface,
    onBackground = IHCTextPrimary,
    onSurface = IHCTextPrimary,
    surfaceVariant = IHCSurfaceVariant,
    onSurfaceVariant = IHCTextSecondary,
    outline = IHCOutline
)

@Composable
fun InstaHomeCareTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    // InstaHomeCare branding uses the clean, crisp light theme with white surfaces, dark navy text, and green accents
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun appTextFieldColors() = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
    focusedTextColor = IHCNavyBlue,
    unfocusedTextColor = IHCNavyBlue,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    cursorColor = IHCPrimaryGreen,
    focusedBorderColor = IHCPrimaryGreen,
    unfocusedBorderColor = Color(0xFFCBD5E1),
    focusedLabelColor = IHCPrimaryGreen,
    unfocusedLabelColor = IHCNavyBlue,
    focusedPlaceholderColor = Color(0xFF94A3B8),
    unfocusedPlaceholderColor = Color(0xFF94A3B8),
    focusedSupportingTextColor = Color(0xFF64748B),
    unfocusedSupportingTextColor = Color(0xFF64748B),
    errorTextColor = IHCRed,
    errorContainerColor = Color.White,
    errorBorderColor = IHCRed,
    errorCursorColor = IHCRed,
    errorLabelColor = IHCRed,
    errorSupportingTextColor = IHCRed
)
