package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.model.PlayerThemeId

@Composable
fun Vision4KTheme(
    playerTheme: PlayerThemeId = PlayerThemeId.OLED_MIDNIGHT,
    content: @Composable () -> Unit,
) {
    val colorScheme = playerTheme.getColorScheme()
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Backward-compatible alias
@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    Vision4KTheme(playerTheme = PlayerThemeId.OLED_MIDNIGHT, content = content)
}
