package com.example.ui.screens

import android.app.Activity
import android.app.PictureInPictureParams
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.util.Rational
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.example.model.PlayerThemeId
import com.example.model.VideoItem
import com.example.player.AspectRatioMode
import com.example.player.PlayerEngine
import com.example.ui.components.GestureOverlay
import com.example.ui.components.HardwareStatsSheet
import com.example.ui.components.PlayerControlsOverlay
import com.example.ui.components.ThemePickerSheet
import com.example.ui.components.VideoPlayerSurface
import kotlinx.coroutines.delay

@Composable
fun PlayerScreen(
    playerEngine: PlayerEngine,
    currentTheme: PlayerThemeId,
    onSelectTheme: (PlayerThemeId) -> Unit,
    playlist: List<VideoItem>,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val engineState by playerEngine.engineState.collectAsState()

    var zoomScale by remember { mutableFloatStateOf(1.0f) }
    var zoomOffset by remember { mutableStateOf(Offset.Zero) }
    var aspectRatioMode by remember { mutableStateOf(AspectRatioMode.FIT) }
    var isControlsVisible by remember { mutableStateOf(true) }
    var isLocked by remember { mutableStateOf(false) }

    var volume by remember { mutableFloatStateOf(1.0f) }
    var brightness by remember { mutableFloatStateOf(0.7f) }

    var showStatsSheet by remember { mutableStateOf(false) }
    var showThemeSheet by remember { mutableStateOf(false) }

    var userInteractionTimestamp by remember { mutableStateOf(System.currentTimeMillis()) }

    // Auto-hide controls timer
    LaunchedEffect(isControlsVisible, isLocked, userInteractionTimestamp, engineState.isPlaying) {
        if (isControlsVisible && !isLocked && engineState.isPlaying) {
            delay(3500)
            isControlsVisible = false
        }
    }

    // Intercept back button
    BackHandler {
        if (showStatsSheet) {
            showStatsSheet = false
        } else if (showThemeSheet) {
            showThemeSheet = false
        } else if (isLocked) {
            isLocked = false
            isControlsVisible = true
        } else {
            onBack()
        }
    }

    fun triggerUserInteraction() {
        userInteractionTimestamp = System.currentTimeMillis()
    }

    fun playNextVideo() {
        val current = engineState.currentVideo ?: return
        val currentIndex = playlist.indexOfFirst { it.id == current.id }
        if (currentIndex != -1 && playlist.isNotEmpty()) {
            val nextIndex = (currentIndex + 1) % playlist.size
            playerEngine.playVideo(playlist[nextIndex])
            zoomScale = 1.0f
            zoomOffset = Offset.Zero
        }
    }

    fun playPreviousVideo() {
        val current = engineState.currentVideo ?: return
        val currentIndex = playlist.indexOfFirst { it.id == current.id }
        if (currentIndex != -1 && playlist.isNotEmpty()) {
            val prevIndex = if (currentIndex - 1 < 0) playlist.size - 1 else currentIndex - 1
            playerEngine.playVideo(playlist[prevIndex])
            zoomScale = 1.0f
            zoomOffset = Offset.Zero
        }
    }

    fun cycleSpeed() {
        val current = engineState.playbackSpeed
        val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
        val nextIndex = (speeds.indexOf(current) + 1).let { if (it in speeds.indices) it else 0 }
        playerEngine.setSpeed(speeds[nextIndex])
    }

    fun enterPictureInPicture() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val activity = context.findActivity()
            if (activity != null) {
                isControlsVisible = false
                val aspectRatio = Rational(
                    engineState.diagnostics.videoWidth.coerceAtLeast(16),
                    engineState.diagnostics.videoHeight.coerceAtLeast(9)
                )
                val params = PictureInPictureParams.Builder()
                    .setAspectRatio(aspectRatio)
                    .build()
                try {
                    activity.enterPictureInPictureMode(params)
                } catch (e: Exception) {
                    // Fallback gracefully
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("player_screen_root")
    ) {
        // Video Surface with Gesture Detection
        GestureOverlay(
            isLocked = isLocked,
            currentPositionMs = engineState.currentPositionMs,
            durationMs = engineState.durationMs,
            zoomScale = zoomScale,
            zoomOffset = zoomOffset,
            onZoomChange = { scale, offset ->
                zoomScale = scale
                zoomOffset = offset
                triggerUserInteraction()
            },
            onSeekRelative = { deltaMs ->
                playerEngine.seekRelative(deltaMs)
                triggerUserInteraction()
            },
            onSeekTo = { targetMs ->
                playerEngine.seekTo(targetMs)
                triggerUserInteraction()
            },
            onSingleTap = {
                isControlsVisible = !isControlsVisible
                triggerUserInteraction()
            },
            volume = volume,
            onVolumeChange = { newVol ->
                volume = newVol
                playerEngine.setVolumeLevel(newVol)
                triggerUserInteraction()
            },
            brightness = brightness,
            onBrightnessChange = { newBright ->
                brightness = newBright
                val activity = context.findActivity()
                activity?.window?.attributes = activity?.window?.attributes?.apply {
                    screenBrightness = newBright
                }
                triggerUserInteraction()
            }
        ) {
            VideoPlayerSurface(
                exoPlayer = playerEngine.exoPlayer,
                aspectRatioMode = aspectRatioMode,
                zoomScale = zoomScale,
                zoomOffset = zoomOffset
            )
        }

        // On-Screen Controls HUD
        PlayerControlsOverlay(
            isVisible = isControlsVisible,
            isLocked = isLocked,
            onToggleLock = { isLocked = !isLocked },
            isPlaying = engineState.isPlaying,
            isBuffering = engineState.isBuffering,
            currentVideo = engineState.currentVideo,
            currentPositionMs = engineState.currentPositionMs,
            durationMs = engineState.durationMs,
            bufferedPositionMs = engineState.bufferedPositionMs,
            aspectRatioMode = aspectRatioMode,
            playbackSpeed = engineState.playbackSpeed,
            onPlayPause = { playerEngine.togglePlayPause() },
            onSeekTo = { playerEngine.seekTo(it) },
            onNext = { playNextVideo() },
            onPrevious = { playPreviousVideo() },
            onCycleAspectRatio = { aspectRatioMode = aspectRatioMode.next() },
            onCycleSpeed = { cycleSpeed() },
            onOpenStats = { showStatsSheet = true },
            onOpenThemes = { showThemeSheet = true },
            onEnterPip = { enterPictureInPicture() },
            onBack = onBack,
            onUserInteraction = { triggerUserInteraction() }
        )

        // Bottom Sheets
        if (showStatsSheet) {
            HardwareStatsSheet(
                diagnostics = engineState.diagnostics,
                onDismiss = { showStatsSheet = false }
            )
        }

        if (showThemeSheet) {
            ThemePickerSheet(
                selectedTheme = currentTheme,
                onSelectTheme = { theme ->
                    onSelectTheme(theme)
                    showThemeSheet = false
                },
                onDismiss = { showThemeSheet = false }
            )
        }
    }
}

fun Context.findActivity(): Activity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) return currentContext
        currentContext = currentContext.baseContext
    }
    return null
}
