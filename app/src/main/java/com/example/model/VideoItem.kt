package com.example.model

import android.net.Uri

data class VideoItem(
    val id: String,
    val title: String,
    val description: String,
    val uriString: String,
    val resolutionLabel: String = "4K UHD",
    val codecLabel: String = "HEVC/H.265",
    val fps: Int = 60,
    val isSample: Boolean = true,
    val durationLabel: String = "Demo",
    val tag: String = "Ultra HD",
    val isHardwareAccelerated: Boolean = true
) {
    fun toUri(): Uri = Uri.parse(uriString)

    companion object {
        val SAMPLE_VIDEOS = listOf(
            VideoItem(
                id = "sample_4k_tears_of_steel",
                title = "Cosmic Odyssey: Sci-Fi 4K",
                description = "4K Ultra-HD 60FPS Sci-Fi cinematic showcase with HDR color range and multi-core spatial audio decoding.",
                uriString = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                resolutionLabel = "3840x2160 4K",
                codecLabel = "HEVC 4K Main 10",
                fps = 60,
                durationLabel = "12:14",
                tag = "4K 60FPS"
            ),
            VideoItem(
                id = "sample_4k_big_buck_bunny",
                title = "Nature Sanctuary: 4K Vivid Animation",
                description = "Ultra high bitrate animation demo rendering rich forest micro-textures and fluid multi-thread physics.",
                uriString = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                resolutionLabel = "3840x2160 UHD",
                codecLabel = "AVC / H.264 High 4K",
                fps = 60,
                durationLabel = "09:56",
                tag = "4K UHD"
            ),
            VideoItem(
                id = "sample_4k_sintel",
                title = "Dragon Flight: Cinematic Master",
                description = "High dynamic range fantasy sequence highlighting dark shadows, neon highlights, and hardware accelerated motion interpolation.",
                uriString = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
                resolutionLabel = "4096x1744 4K DCI",
                codecLabel = "HEVC / H.265",
                fps = 48,
                durationLabel = "00:52",
                tag = "4K DCI"
            ),
            VideoItem(
                id = "sample_4k_elephants_dream",
                title = "Machine Core: Industrial Precision",
                description = "Complex geometry and multicore decoder stress test video testing asynchronous pipeline buffer queues.",
                uriString = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                resolutionLabel = "3840x2160 4K",
                codecLabel = "AV1 / Multicore Dec",
                fps = 60,
                durationLabel = "10:53",
                tag = "High Bitrate"
            ),
            VideoItem(
                id = "sample_for_bigger_blazes",
                title = "Chroma Blaze: High Frame-Rate Colors",
                description = "Vibrant motion color bursts benchmark for hardware surface rendering and screen refresh rate synchronization.",
                uriString = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                resolutionLabel = "1920x1080 60FPS",
                codecLabel = "Hardware Fast Decode",
                fps = 60,
                durationLabel = "00:15",
                tag = "Action HDR"
            )
        )
    }
}
