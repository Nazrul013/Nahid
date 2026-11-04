package com.example.player

enum class AspectRatioMode(val label: String, val shortLabel: String) {
    FIT("Fit to Screen", "FIT"),
    FILL("Fill / Zoom", "FILL"),
    STRETCH("Stretch", "STRETCH"),
    RATIO_16_9("16 : 9", "16:9"),
    RATIO_21_9("Cinema 21 : 9", "21:9");

    fun next(): AspectRatioMode {
        val values = entries
        val nextIndex = (ordinal + 1) % values.size
        return values[nextIndex]
    }
}
