package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = SageGreenPrimary,
    onPrimary = SageGreenOnPrimary,
    primaryContainer = SageGreenContainer,
    onPrimaryContainer = SageGreenOnContainer,
    secondary = TerracottaSecondary,
    onSecondary = Color.White,
    secondaryContainer = TerracottaSecondaryContainer,
    onSecondaryContainer = TerracottaOnSecondaryContainer,
    background = WarmBackground,
    onBackground = WarmOnSurface,
    surface = WarmSurface,
    onSurface = WarmOnSurface,
    surfaceVariant = WarmSurfaceVariant,
    onSurfaceVariant = WarmOnSurfaceVariant,
    outline = WarmOutline,
    outlineVariant = WarmOutlineVariant,
    error = ExpiredRed,
    errorContainer = ExpiredRedContainer,
    onError = Color.White,
    onErrorContainer = ExpiredOnRedContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkSagePrimary,
    onPrimary = DarkSageOnPrimary,
    primaryContainer = DarkSageContainer,
    onPrimaryContainer = SageGreenContainer,
    background = DarkBackground,
    onBackground = Color(0xFFE2E3DE),
    surface = DarkSurface,
    onSurface = Color(0xFFE2E3DE),
    surfaceVariant = Color(0xFF282B25),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // We use the warm custom theme for consistent warm aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
