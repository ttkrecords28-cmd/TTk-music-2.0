package com.example.player

import com.example.model.LyricLine

object LrcParser {
    // Regex for [mm:ss.xx] or [mm:ss] or [mm:ss.xxx]
    private val LRC_REGEX = Regex("""\[(\d{1,2}):(\d{2})(?:\.(\d{1,3}))?\](.*)""")

    fun parse(lrcText: String): List<LyricLine> {
        if (lrcText.isBlank()) return emptyList()

        val lines = mutableListOf<LyricLine>()
        lrcText.lines().forEach { line ->
            val trimmed = line.trim()
            val match = LRC_REGEX.find(trimmed)
            if (match != null) {
                val min = match.groupValues[1].toLongOrNull() ?: 0L
                val sec = match.groupValues[2].toLongOrNull() ?: 0L
                val msPart = match.groupValues[3]
                val ms = when (msPart.length) {
                    1 -> (msPart.toLongOrNull() ?: 0L) * 100L
                    2 -> (msPart.toLongOrNull() ?: 0L) * 10L
                    3 -> msPart.toLongOrNull() ?: 0L
                    else -> 0L
                }
                val totalMs = (min * 60 * 1000) + (sec * 1000) + ms
                val text = match.groupValues[4].trim()
                if (text.isNotEmpty()) {
                    lines.add(LyricLine(timeMs = totalMs, text = text))
                }
            }
        }
        return lines.sortedBy { it.timeMs }
    }

    fun getActiveLineIndex(lyrics: List<LyricLine>, currentPositionMs: Long): Int {
        if (lyrics.isEmpty()) return -1
        for (i in lyrics.indices.reversed()) {
            if (currentPositionMs >= lyrics[i].timeMs) {
                return i
            }
        }
        return 0
    }
}
