package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.roundToInt

enum class GestureFeedbackType {
    NONE,
    SEEK,
    VOLUME,
    BRIGHTNESS,
    ZOOM,
    DOUBLE_TAP_FORWARD,
    DOUBLE_TAP_REWIND
}

@Composable
fun GestureOverlay(
    modifier: Modifier = Modifier,
    isLocked: Boolean,
    currentPositionMs: Long,
    durationMs: Long,
    zoomScale: Float,
    zoomOffset: Offset,
    onZoomChange: (Float, Offset) -> Unit,
    onSeekRelative: (Long) -> Unit,
    onSeekTo: (Long) -> Unit,
    onSingleTap: () -> Unit,
    volume: Float,
    onVolumeChange: (Float) -> Unit,
    brightness: Float,
    onBrightnessChange: (Float) -> Unit,
    content: @Composable () -> Unit
) {
    var activeFeedback by remember { mutableStateOf(GestureFeedbackType.NONE) }
    var seekDeltaMs by remember { mutableLongStateOf(0L) }
    var previewSeekMs by remember { mutableLongStateOf(0L) }

    var dragStartX by remember { mutableFloatStateOf(0f) }
    var dragStartY by remember { mutableFloatStateOf(0f) }
    var isDraggingHorizontal by remember { mutableStateOf(false) }
    var isDraggingVertical by remember { mutableStateOf(false) }
    var dragSideIsLeft by remember { mutableStateOf(true) }

    // Auto-dismiss gesture feedback HUDs
    LaunchedEffect(activeFeedback) {
        if (activeFeedback != GestureFeedbackType.NONE &&
            activeFeedback != GestureFeedbackType.SEEK
        ) {
            delay(1200)
            activeFeedback = GestureFeedbackType.NONE
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(isLocked) {
                if (isLocked) {
                    detectTapGestures(onTap = { onSingleTap() })
                    return@pointerInput
                }

                detectTapGestures(
                    onTap = { onSingleTap() },
                    onDoubleTap = { offset ->
                        val screenWidth = size.width
                        if (zoomScale > 1.05f) {
                            // Reset zoom on double tap
                            onZoomChange(1.0f, Offset.Zero)
                            activeFeedback = GestureFeedbackType.ZOOM
                        } else {
                            if (offset.x < screenWidth * 0.35f) {
                                // Double tap left: Rewind 10s
                                onSeekRelative(-10_000L)
                                activeFeedback = GestureFeedbackType.DOUBLE_TAP_REWIND
                            } else if (offset.x > screenWidth * 0.65f) {
                                // Double tap right: Fast forward 10s
                                onSeekRelative(10_000L)
                                activeFeedback = GestureFeedbackType.DOUBLE_TAP_FORWARD
                            } else {
                                // Double tap center: Zoom 2.0x
                                onZoomChange(2.0f, Offset.Zero)
                                activeFeedback = GestureFeedbackType.ZOOM
                            }
                        }
                    }
                )
            }
            .pointerInput(isLocked, zoomScale) {
                if (isLocked) return@pointerInput

                detectTransformGestures { centroid, pan, zoom, _ ->
                    if (zoom != 1f || pan != Offset.Zero) {
                        val newScale = (zoomScale * zoom).coerceIn(1.0f, 4.0f)
                        val maxPanX = (newScale - 1f) * size.width * 0.5f
                        val maxPanY = (newScale - 1f) * size.height * 0.5f

                        val newOffsetX = if (newScale > 1.0f) {
                            (zoomOffset.x + pan.x).coerceIn(-maxPanX, maxPanX)
                        } else 0f

                        val newOffsetY = if (newScale > 1.0f) {
                            (zoomOffset.y + pan.y).coerceIn(-maxPanY, maxPanY)
                        } else 0f

                        onZoomChange(newScale, Offset(newOffsetX, newOffsetY))
                        if (abs(zoom - 1f) > 0.02f) {
                            activeFeedback = GestureFeedbackType.ZOOM
                        }
                    }
                }
            }
            .pointerInput(isLocked, currentPositionMs, durationMs, volume, brightness) {
                if (isLocked) return@pointerInput

                awaitPointerEventScope {
                    while (true) {
                        val downEvent = awaitPointerEvent()
                        val down = downEvent.changes.firstOrNull() ?: continue
                        if (down.isConsumed || downEvent.changes.size > 1) continue

                        dragStartX = down.position.x
                        dragStartY = down.position.y
                        dragSideIsLeft = dragStartX < size.width * 0.5f
                        isDraggingHorizontal = false
                        isDraggingVertical = false
                        seekDeltaMs = 0L

                        var pointerMoved = false
                        while (true) {
                            val moveEvent = awaitPointerEvent()
                            val change = moveEvent.changes.firstOrNull() ?: break
                            if (!change.pressed) {
                                // Pointer up: Commit actions
                                if (isDraggingHorizontal) {
                                    onSeekTo(previewSeekMs)
                                    activeFeedback = GestureFeedbackType.NONE
                                }
                                isDraggingHorizontal = false
                                isDraggingVertical = false
                                break
                            }

                            val dx = change.position.x - dragStartX
                            val dy = change.position.y - dragStartY

                            if (!isDraggingHorizontal && !isDraggingVertical) {
                                if (abs(dx) > 30f && abs(dx) > abs(dy) * 1.5f && zoomScale <= 1.05f) {
                                    isDraggingHorizontal = true
                                    pointerMoved = true
                                } else if (abs(dy) > 30f && abs(dy) > abs(dx) * 1.5f && zoomScale <= 1.05f) {
                                    isDraggingVertical = true
                                    pointerMoved = true
                                }
                            }

                            if (isDraggingHorizontal) {
                                change.consume()
                                val seekRatio = dx / size.width.toFloat()
                                val maxSeekWindow = 90_000L // +/- 90 seconds
                                val delta = (seekRatio * maxSeekWindow).toLong()
                                seekDeltaMs = delta
                                previewSeekMs = (currentPositionMs + delta).coerceIn(0L, durationMs.coerceAtLeast(1L))
                                activeFeedback = GestureFeedbackType.SEEK
                            } else if (isDraggingVertical) {
                                change.consume()
                                val deltaFraction = -dy / (size.height * 0.6f)
                                if (dragSideIsLeft) {
                                    val newBrightness = (brightness + deltaFraction * 0.05f).coerceIn(0.05f, 1f)
                                    onBrightnessChange(newBrightness)
                                    activeFeedback = GestureFeedbackType.BRIGHTNESS
                                } else {
                                    val newVolume = (volume + deltaFraction * 0.05f).coerceIn(0f, 1f)
                                    onVolumeChange(newVolume)
                                    activeFeedback = GestureFeedbackType.VOLUME
                                }
                            }
                        }
                    }
                }
            }
    ) {
        content()

        // Visual Gesture Feedback Overlays
        // 1. Seek HUD Overlay
        AnimatedVisibility(
            visible = activeFeedback == GestureFeedbackType.SEEK,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.85f),
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 8.dp,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    val sign = if (seekDeltaMs >= 0) "+" else ""
                    val deltaSec = seekDeltaMs / 1000
                    Text(
                        text = "$sign${deltaSec}s",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${formatTime(previewSeekMs)} / ${formatTime(durationMs)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            }
        }

        // 2. Volume HUD Overlay
        AnimatedVisibility(
            visible = activeFeedback == GestureFeedbackType.VOLUME,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 32.dp)
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.82f),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(8.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 18.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Volume Level",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "${(volume * 100).roundToInt()}%",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { volume },
                        modifier = Modifier
                            .width(8.dp)
                            .height(90.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.White.copy(alpha = 0.2f),
                    )
                }
            }
        }

        // 3. Brightness HUD Overlay
        AnimatedVisibility(
            visible = activeFeedback == GestureFeedbackType.BRIGHTNESS,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 32.dp)
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.82f),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(8.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 18.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.BrightnessMedium,
                        contentDescription = "Brightness Level",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "${(brightness * 100).roundToInt()}%",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { brightness },
                        modifier = Modifier
                            .width(8.dp)
                            .height(90.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.White.copy(alpha = 0.2f),
                    )
                }
            }
        }

        // 4. Double Tap Skip Overlays (Left Rewind & Right Forward)
        AnimatedVisibility(
            visible = activeFeedback == GestureFeedbackType.DOUBLE_TAP_REWIND,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 48.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FastRewind,
                        contentDescription = "Rewind 10 seconds",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "-10s",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = activeFeedback == GestureFeedbackType.DOUBLE_TAP_FORWARD,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 48.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FastForward,
                        contentDescription = "Forward 10 seconds",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "+10s",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // 5. Zoom Scale HUD
        AnimatedVisibility(
            visible = activeFeedback == GestureFeedbackType.ZOOM && zoomScale > 1.05f,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp)
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.75f),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.testTag("zoom_indicator")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ZoomIn,
                        contentDescription = "Zoom Scale",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Zoom: ${String.format("%.1fx", zoomScale)} • Double tap to reset",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

fun formatTime(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val hours = minutes / 60
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes % 60, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
