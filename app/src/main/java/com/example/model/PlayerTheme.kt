package com.example.model

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

enum class PlayerThemeId(
    val title: String,
    val description: String,
    val accentColor: Color,
    val backgroundSwatch: Color
) {
    OLED_MIDNIGHT(
        title = "OLED Midnight",
        description = "Pure black with electric cyan accents",
        accentColor = Color(0xFF00E5FF),
        backgroundSwatch = Color(0xFF05070B)
    ),
    CYBER_NEON(
        title = "Cyber Neon",
        description = "Vibrant magenta and ultraviolet glow",
        accentColor = Color(0xFFFF007F),
        backgroundSwatch = Color(0xFF0D0618)
    ),
    SUNSET_AMBER(
        title = "Sunset Amber",
        description = "Warm obsidian with fiery gold highlights",
        accentColor = Color(0xFFFF9100),
        backgroundSwatch = Color(0xFF140D07)
    ),
    DEEP_OCEAN(
        title = "Deep Ocean",
        description = "Abyssal navy with radiant aquamarine",
        accentColor = Color(0xFF00B4D8),
        backgroundSwatch = Color(0xFF06141D)
    ),
    EMERALD_AURORA(
        title = "Emerald Aurora",
        description = "Dark forest with vibrant emerald neon",
        accentColor = Color(0xFF00E676),
        backgroundSwatch = Color(0xFF06150D)
    ),
    ROYAL_AMETHYST(
        title = "Royal Amethyst",
        description = "Deep velvet purple with vivid lavender",
        accentColor = Color(0xFFB388FF),
        backgroundSwatch = Color(0xFF110B1E)
    );

    fun getColorScheme(): ColorScheme {
        return when (this) {
            OLED_MIDNIGHT -> darkColorScheme(
                primary = Color(0xFF00E5FF),
                onPrimary = Color(0xFF00363D),
                primaryContainer = Color(0xFF004F58),
                onPrimaryContainer = Color(0xFF8CF3FF),
                secondary = Color(0xFF4DD0E1),
                onSecondary = Color(0xFF00363D),
                surface = Color(0xFF0E131A),
                onSurface = Color(0xFFE6EEF5),
                surfaceVariant = Color(0xFF161F2B),
                onSurfaceVariant = Color(0xFF90A4AE),
                background = Color(0xFF05070B),
                onBackground = Color(0xFFE6EEF5),
                outline = Color(0xFF263238),
                tertiary = Color(0xFF00B0FF)
            )
            CYBER_NEON -> darkColorScheme(
                primary = Color(0xFFFF007F),
                onPrimary = Color(0xFF40001D),
                primaryContainer = Color(0xFF5F002D),
                onPrimaryContainer = Color(0xFFFFD9E2),
                secondary = Color(0xFF00F5D4),
                onSecondary = Color(0xFF003830),
                surface = Color(0xFF160F25),
                onSurface = Color(0xFFF7ECFF),
                surfaceVariant = Color(0xFF221738),
                onSurfaceVariant = Color(0xFFB39DDB),
                background = Color(0xFF0D0618),
                onBackground = Color(0xFFF7ECFF),
                outline = Color(0xFF4A148C),
                tertiary = Color(0xFFFFEA00)
            )
            SUNSET_AMBER -> darkColorScheme(
                primary = Color(0xFFFF9100),
                onPrimary = Color(0xFF452200),
                primaryContainer = Color(0xFF633300),
                onPrimaryContainer = Color(0xFFFFDCC0),
                secondary = Color(0xFFFF5722),
                onSecondary = Color(0xFF441200),
                surface = Color(0xFF1B140F),
                onSurface = Color(0xFFFCEFE7),
                surfaceVariant = Color(0xFF2B2018),
                onSurfaceVariant = Color(0xFFBCAAA4),
                background = Color(0xFF140D07),
                onBackground = Color(0xFFFCEFE7),
                outline = Color(0xFF4E342E),
                tertiary = Color(0xFFFFD600)
            )
            DEEP_OCEAN -> darkColorScheme(
                primary = Color(0xFF00B4D8),
                onPrimary = Color(0xFF003440),
                primaryContainer = Color(0xFF004D5F),
                onPrimaryContainer = Color(0xFFB8E6F5),
                secondary = Color(0xFF48CAE4),
                onSecondary = Color(0xFF00353E),
                surface = Color(0xFF0D1B2A),
                onSurface = Color(0xFFE0E1DD),
                surfaceVariant = Color(0xFF1B263B),
                onSurfaceVariant = Color(0xFF778DA9),
                background = Color(0xFF06141D),
                onBackground = Color(0xFFE0E1DD),
                outline = Color(0xFF415A77),
                tertiary = Color(0xFF90E0EF)
            )
            EMERALD_AURORA -> darkColorScheme(
                primary = Color(0xFF00E676),
                onPrimary = Color(0xFF003919),
                primaryContainer = Color(0xFF005327),
                onPrimaryContainer = Color(0xFF6CFFA0),
                secondary = Color(0xFF69F0AE),
                onSecondary = Color(0xFF00381E),
                surface = Color(0xFF0C1C13),
                onSurface = Color(0xFFE8F5E9),
                surfaceVariant = Color(0xFF152A1E),
                onSurfaceVariant = Color(0xFFA5D6A7),
                background = Color(0xFF06150D),
                onBackground = Color(0xFFE8F5E9),
                outline = Color(0xFF2E7D32),
                tertiary = Color(0xFFB9F6CA)
            )
            ROYAL_AMETHYST -> darkColorScheme(
                primary = Color(0xFFB388FF),
                onPrimary = Color(0xFF2F0075),
                primaryContainer = Color(0xFF47009C),
                onPrimaryContainer = Color(0xFFECD9FF),
                secondary = Color(0xFF7C4DFF),
                onSecondary = Color(0xFF1C0067),
                surface = Color(0xFF191325),
                onSurface = Color(0xFFEDE7F6),
                surfaceVariant = Color(0xFF261D38),
                onSurfaceVariant = Color(0xFFD1C4E9),
                background = Color(0xFF110B1E),
                onBackground = Color(0xFFEDE7F6),
                outline = Color(0xFF512DA8),
                tertiary = Color(0xFFEA80FC)
            )
        }
    }
}
