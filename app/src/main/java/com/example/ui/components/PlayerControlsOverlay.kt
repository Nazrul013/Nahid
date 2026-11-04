package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VideoItem
import com.example.player.AspectRatioMode
import kotlinx.coroutines.delay

@Composable
fun PlayerControlsOverlay(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    isLocked: Boolean,
    onToggleLock: () -> Unit,
    isPlaying: Boolean,
    isBuffering: Boolean,
    currentVideo: VideoItem?,
    currentPositionMs: Long,
    durationMs: Long,
    bufferedPositionMs: Long,
    aspectRatioMode: AspectRatioMode,
    playbackSpeed: Float,
    onPlayPause: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onCycleAspectRatio: () -> Unit,
    onCycleSpeed: () -> Unit,
    onOpenStats: () -> Unit,
    onOpenThemes: () -> Unit,
    onEnterPip: () -> Unit,
    onBack: () -> Unit,
    onUserInteraction: () -> Unit
) {
    // If locked, show only the unlock button
    if (isLocked) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Surface(
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 28.dp)
                        .size(52.dp)
                        .clip(CircleShape)
                        .clickable {
                            onToggleLock()
                            onUserInteraction()
                        }
                        .testTag("unlock_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Unlock Controls",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
        return
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to Color.Black.copy(alpha = 0.78f),
                        0.25f to Color.Black.copy(alpha = 0.25f),
                        0.75f to Color.Black.copy(alpha = 0.25f),
                        1.0f to Color.Black.copy(alpha = 0.85f)
                    )
                )
        ) {
            // TOP BAR
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("player_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = currentVideo?.title ?: "4K Video Stream",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            BadgePill(currentVideo?.resolutionLabel ?: "4K UHD")
                            BadgePill("${currentVideo?.fps ?: 60} FPS")
                            HwBadgePill(onClick = onOpenStats)
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onOpenThemes,
                        modifier = Modifier.testTag("theme_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Theme Settings",
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = onOpenStats,
                        modifier = Modifier.testTag("hw_stats_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Decoder Statistics",
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = onEnterPip,
                        modifier = Modifier.testTag("pip_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureInPictureAlt,
                            contentDescription = "Picture in Picture",
                            tint = Color.White
                        )
                    }
                }
            }

            // CENTER PLAYBACK CONTROLS
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
            ) {
                // Lock Button on Left
                Surface(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = CircleShape,
                    modifier = Modifier
                        .padding(end = 32.dp)
                        .size(46.dp)
                        .clip(CircleShape)
                        .clickable {
                            onToggleLock()
                            onUserInteraction()
                        }
                        .testTag("lock_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.LockOpen,
                            contentDescription = "Lock Screen Gestures",
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Previous
                IconButton(
                    onClick = {
                        onPrevious()
                        onUserInteraction()
                    },
                    modifier = Modifier
                        .size(54.dp)
                        .testTag("prev_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous Video",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                // Primary Play / Pause / Replay Button
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .clickable {
                            onPlayPause()
                            onUserInteraction()
                        }
                        .testTag("play_pause_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isBuffering) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(34.dp)
                            )
                        } else {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))

                // Next
                IconButton(
                    onClick = {
                        onNext()
                        onUserInteraction()
                    },
                    modifier = Modifier
                        .size(54.dp)
                        .testTag("next_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next Video",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            // BOTTOM BAR & SCRUBBER
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                // Time & Quick Stats
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${formatTime(currentPositionMs)} / ${formatTime(durationMs)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Aspect Ratio Mode Switcher
                        Surface(
                            color = Color.White.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onCycleAspectRatio()
                                    onUserInteraction()
                                }
                                .testTag("aspect_ratio_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AspectRatio,
                                    contentDescription = "Aspect Ratio",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = aspectRatioMode.shortLabel,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Playback Speed Switcher
                        Surface(
                            color = Color.White.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onCycleSpeed()
                                    onUserInteraction()
                                }
                                .testTag("speed_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = "Playback Speed",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${playbackSpeed}x",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Custom Scrubber Slider with Buffer indicator
                var isScrubbing by remember { mutableStateOf(false) }
                var scrubValue by remember { mutableFloatStateOf(0f) }

                val maxDuration = durationMs.coerceAtLeast(1L).toFloat()
                val currentSliderValue = if (isScrubbing) scrubValue else currentPositionMs.toFloat()

                Slider(
                    value = currentSliderValue.coerceIn(0f, maxDuration),
                    onValueChange = { newValue ->
                        isScrubbing = true
                        scrubValue = newValue
                        onUserInteraction()
                    },
                    onValueChangeFinished = {
                        isScrubbing = false
                        onSeekTo(scrubValue.toLong())
                        onUserInteraction()
                    },
                    valueRange = 0f..maxDuration,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = Color.White.copy(alpha = 0.25f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("timeline_slider")
                )
            }
        }
    }
}

@Composable
private fun BadgePill(text: String) {
    Surface(
        color = Color.White.copy(alpha = 0.18f),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun HwBadgePill(onClick: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = "HW+ MULTICORE",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
