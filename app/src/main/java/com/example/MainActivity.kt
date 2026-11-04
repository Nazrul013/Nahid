package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.model.PlayerThemeId
import com.example.model.VideoItem
import com.example.player.PlayerEngine
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PlayerScreen
import com.example.ui.theme.Vision4KTheme

enum class Screen {
    HOME,
    PLAYER
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Vision4KApp()
        }
    }
}

@Composable
fun Vision4KApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var currentTheme by remember { mutableStateOf(PlayerThemeId.OLED_MIDNIGHT) }
    var currentScreen by remember { mutableStateOf(Screen.HOME) }

    val playerEngine = remember { PlayerEngine(context, scope) }
    val playlist = remember { mutableStateListOf<VideoItem>().apply { addAll(VideoItem.SAMPLE_VIDEOS) } }

    DisposableEffect(Unit) {
        onDispose {
            playerEngine.release()
        }
    }

    Vision4KTheme(playerTheme = currentTheme) {
        Surface(modifier = Modifier.fillMaxSize()) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    Screen.HOME -> {
                        HomeScreen(
                            currentTheme = currentTheme,
                            onSelectTheme = { currentTheme = it },
                            onPlayVideo = { video ->
                                playerEngine.playVideo(video)
                                currentScreen = Screen.PLAYER
                            },
                            onAddCustomVideo = { newVideo ->
                                playlist.add(0, newVideo)
                            }
                        )
                    }

                    Screen.PLAYER -> {
                        PlayerScreen(
                            playerEngine = playerEngine,
                            currentTheme = currentTheme,
                            onSelectTheme = { currentTheme = it },
                            playlist = playlist,
                            onBack = {
                                currentScreen = Screen.HOME
                            }
                        )
                    }
                }
            }
        }
    }
}
