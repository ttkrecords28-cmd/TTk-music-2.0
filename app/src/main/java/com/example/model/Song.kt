package com.example.model

import com.example.R

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val uriString: String? = null,
    val localCoverRes: Int? = null,
    val coverUrl: String? = null,
    val genre: String = "Electronic",
    val lrcContent: String = "",
    val isFavorite: Boolean = false,
    val bpm: Int = 120,
    val tunerFrequency: Float = 98.5f,
    val tunerStation: String = "TTK 98.5 FM"
)

data class Artist(
    val name: String,
    val bio: String,
    val monthlyListeners: String,
    val genre: String,
    val topSongs: List<String>
)

data class LyricLine(
    val timeMs: Long,
    val text: String
)

enum class EqualizerPreset(
    val title: String,
    val emoji: String,
    val bassFactor: Float,
    val midFactor: Float,
    val trebleFactor: Float
) {
    BASS_BOOST("Bass Boost", "🔊", 1.55f, 0.90f, 0.85f),
    VOCAL_CLARITY("Vocal Clarity", "🎙️", 0.80f, 1.45f, 1.25f),
    ELECTRONIC("Electronic", "⚡", 1.35f, 1.05f, 1.40f),
    CHILL_ACOUSTIC("Chill Acoustic", "🎸", 1.05f, 1.20f, 1.00f),
    FLAT("Flat Balanced", "⚖️", 1.00f, 1.00f, 1.00f)
}

enum class ColorThemeId(
    val displayName: String,
    val emoji: String,
    val bgGradientStart: Long,
    val bgGradientEnd: Long,
    val primaryAccent: Long,
    val glowColor: Long,
    val isDynamic: Boolean = false
) {
    AMBIENT_MIDNIGHT(
        displayName = "Ambient Midnight Blue",
        emoji = "🌌",
        bgGradientStart = 0xFF070B16,
        bgGradientEnd = 0xFF0E1A33,
        primaryAccent = 0xFF00E5FF,
        glowColor = 0xFF7928CA
    ),
    EMERALD_AURA(
        displayName = "Emerald Aura",
        emoji = "🌿",
        bgGradientStart = 0xFF04140D,
        bgGradientEnd = 0xFF0A2B1D,
        primaryAccent = 0xFF00F5A0,
        glowColor = 0xFF00D9F5
    ),
    CRIMSON_SUNSET(
        displayName = "Crimson Sunset",
        emoji = "🌅",
        bgGradientStart = 0xFF19060F,
        bgGradientEnd = 0xFF350E23,
        primaryAccent = 0xFFFF416C,
        glowColor = 0xFFFF8C00
    ),
    OBSIDIAN_GOLD(
        displayName = "Obsidian Gold",
        emoji = "✨",
        bgGradientStart = 0xFF0F0E0B,
        bgGradientEnd = 0xFF242018,
        primaryAccent = 0xFFFFD700,
        glowColor = 0xFFE5A93C
    ),
    DYNAMIC_CHROMA(
        displayName = "Dynamic Song Chroma",
        emoji = "🎨",
        bgGradientStart = 0xFF0C0A1A,
        bgGradientEnd = 0xFF1E1538,
        primaryAccent = 0xFFB388FF,
        glowColor = 0xFF00E5FF,
        isDynamic = true
    )
}

object DemoCatalog {
    val ARTIST_CYBERPULSE = Artist(
        name = "CyberPulse & Luna Echo",
        bio = "Electronic synthwave duo fusing 80s analog nostalgia with futuristic cybernetic soundscapes and hypnotic sub-bass.",
        monthlyListeners = "1,842,500 monthly listeners",
        genre = "Synthwave / Cyberpunk ⚡",
        topSongs = listOf("Midnight Neon Horizon", "Laser City Run", "Cyber Skyline")
    )

    val ARTIST_ARIA = Artist(
        name = "Aria & The Lo-Fi Collective",
        bio = "Tokyo-based beatmaker crafting gentle vinyl-textured chillhop, rain ambience, and warm nostalgic chord progressions for focus and study.",
        monthlyListeners = "2,410,900 monthly listeners",
        genre = "Lo-Fi Chill / Ambient ☕",
        topSongs = listOf("Rainy Cafe Reverie", "Matcha Dreams", "Library Dusk")
    )

    val ARTIST_COSMIC = Artist(
        name = "Cosmic Odyssey",
        bio = "Cinematic space-ambient explorer shaping stellar reverberations, analog orbital pads, and interstellar frequencies.",
        monthlyListeners = "920,400 monthly listeners",
        genre = "Space Ambient / Electronic 🪐",
        topSongs = listOf("Starlight Voyage", "Ring of Saturn", "Nebula Passage")
    )

    val ARTIST_SOLARIS = Artist(
        name = "Solaris Beats",
        bio = "Tropical chillwave producer weaving golden sunset acoustics with sun-drenched melodic percussion and shimmering synths.",
        monthlyListeners = "1,350,100 monthly listeners",
        genre = "Chillwave / Sunset Melodic 🌅",
        topSongs = listOf("Sunset Prism", "Pacific Tropic", "Coastline Glow")
    )

    val DEFAULT_SONGS = listOf(
        Song(
            id = "demo_1",
            title = "Midnight Neon Horizon",
            artist = "CyberPulse & Luna Echo",
            album = "Neon Velocity 🏎️",
            durationMs = 195000L, // 3:15
            localCoverRes = R.drawable.cover_synthwave,
            genre = "Synthwave ⚡",
            bpm = 128,
            lrcContent = """
                [00:00.00] 🌌 Gliding through neon streets at midnight...
                [00:04.50] ⚡ Chromatic reflections in the digital rain...
                [00:09.00] 🎶 Synths pulsing with the beating heart of the city...
                [00:14.20] 🏎️ Accelerating past the holographic towers...
                [00:19.80] ✨ Shadows fade into violet luminescence...
                [00:25.10] 🎧 We are the dreamers of the silicon dawn...
                [00:30.50] 🌌 Electric skies and boundless endless horizons...
                [00:36.00] 💫 Feel the bass resonate through the glass deck...
                [00:42.00] 🎵 Tomorrow is just a frequency away...
                [00:48.50] ⚡ Speed of light in our veins...
                [00:54.00] 🌌 Midnight Neon Horizon forever!
            """.trimIndent(),
            isFavorite = true,
            tunerFrequency = 94.7f,
            tunerStation = "CYBR 94.7 FM"
        ),
        Song(
            id = "demo_2",
            title = "Rainy Cafe Reverie",
            artist = "Aria & The Lo-Fi Collective",
            album = "Warm Brew & Vinyl ☕",
            durationMs = 168000L, // 2:48
            localCoverRes = R.drawable.cover_lofi,
            genre = "Lo-Fi Beats 🌧️",
            bpm = 84,
            lrcContent = """
                [00:00.00] ☕ Steam rising from the matcha ceramic cup...
                [00:05.00] 🌧️ Raindrops sketching quiet patterns on glass...
                [00:10.50] 📻 The gentle needle crackle of an old record...
                [00:16.00] 📖 Lost between pages of nostalgic memories...
                [00:22.00] 🌿 Time slows down while city lights blur...
                [00:28.00] 🍵 Warm amber glow wraps around the room...
                [00:34.00] 🎶 Dusty piano chords float into the breeze...
                [00:40.00] 😴 Resting your mind in pure tranquil peace...
                [00:46.00] ☕ Another sip of comfort in the cozy dusk...
                [00:52.00] 🌧️ Let the gentle rhythm wash over you...
            """.trimIndent(),
            isFavorite = true,
            tunerFrequency = 101.3f,
            tunerStation = "CHILL 101.3 FM"
        ),
        Song(
            id = "demo_3",
            title = "Starlight Voyage",
            artist = "Cosmic Odyssey",
            album = "Interstellar Echoes 🪐",
            durationMs = 210000L, // 3:30
            localCoverRes = R.drawable.ttk_music_icon_1794930593644,
            genre = "Space Ambient 🌌",
            bpm = 110,
            lrcContent = """
                [00:00.00] 🚀 Engines hum beneath a canopy of stars...
                [00:06.00] 🪐 Drifting silently around Saturn's glowing rings...
                [00:12.50] 🌌 A cosmic symphony echoes through the void...
                [00:19.00] 🌠 Gravity loses hold as dreams ignite...
                [00:26.00] ✨ Celestial frequencies calling us home...
                [00:33.00] 🔭 Mapping uncharted constellations tonight...
                [00:40.00] 🛸 Floating into eternity and peace...
                [00:48.00] 💫 Beyond the galaxy where light is born...
            """.trimIndent(),
            isFavorite = false,
            tunerFrequency = 88.5f,
            tunerStation = "COSMO 88.5 FM"
        ),
        Song(
            id = "demo_4",
            title = "Sunset Prism",
            artist = "Solaris Beats",
            album = "Golden Hour Vibes 🌅",
            durationMs = 184000L, // 3:04
            localCoverRes = R.drawable.cover_synthwave,
            genre = "Chillwave 🌴",
            bpm = 118,
            lrcContent = """
                [00:00.00] 🌅 Golden hues bleeding into purple twilight...
                [00:06.00] 🌊 Ocean waves catching the evening fire...
                [00:12.00] 🏖️ Warm breeze carrying the melody away...
                [00:18.00] 🌴 Palm trees whispering against the twilight sky...
                [00:25.00] 🍹 Sip the moment, let the rhythm take over...
                [00:32.00] 🔮 Prismatic colors dancing across the shore...
                [00:39.00] 🌅 Sunset prism reflecting in our eyes...
            """.trimIndent(),
            isFavorite = false,
            tunerFrequency = 106.2f,
            tunerStation = "SOLAR 106.2 FM"
        )
    )

    fun getArtist(name: String): Artist {
        return when {
            name.contains("CyberPulse", ignoreCase = true) -> ARTIST_CYBERPULSE
            name.contains("Aria", ignoreCase = true) -> ARTIST_ARIA
            name.contains("Cosmic", ignoreCase = true) -> ARTIST_COSMIC
            name.contains("Solaris", ignoreCase = true) -> ARTIST_SOLARIS
            else -> Artist(
                name = name,
                bio = "A passionate music creator producing sonic artistry for listeners worldwide 🎵.",
                monthlyListeners = "1,120,000 monthly listeners",
                genre = "Modern Melodic 🎶",
                topSongs = listOf("Aura Track 1", "Echo Beats", "Starlight Harmony")
            )
        }
    }
}
