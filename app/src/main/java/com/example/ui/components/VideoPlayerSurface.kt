package com.example.ui.components

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.player.AspectRatioMode

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerSurface(
    exoPlayer: ExoPlayer,
    aspectRatioMode: AspectRatioMode,
    zoomScale: Float,
    zoomOffset: Offset,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val resizeMode = when (aspectRatioMode) {
        AspectRatioMode.FIT -> AspectRatioFrameLayout.RESIZE_MODE_FIT
        AspectRatioMode.FILL -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        AspectRatioMode.STRETCH -> AspectRatioFrameLayout.RESIZE_MODE_FILL
        AspectRatioMode.RATIO_16_9 -> AspectRatioFrameLayout.RESIZE_MODE_FIT
        AspectRatioMode.RATIO_21_9 -> AspectRatioFrameLayout.RESIZE_MODE_FIT
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    useController = false
                    this.resizeMode = resizeMode
                    player = exoPlayer
                    keepScreenOn = true
                }
            },
            update = { playerView ->
                playerView.player = exoPlayer
                playerView.resizeMode = resizeMode
            },
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = zoomScale
                    scaleY = zoomScale
                    translationX = zoomOffset.x
                    translationY = zoomOffset.y
                }
        )
    }
}
