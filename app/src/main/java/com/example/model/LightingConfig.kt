package com.example.model

data class LightingColorSwatch(
    val name: String,
    val hex: Long
)

enum class LightingMode(val title: String, val emoji: String) {
    BEAT_REACTIVE("Beat Pulse", "⚡"),
    AURORA_DRIFT("Aurora Drift", "🌌"),
    TUNER_SPECTRUM("Tuner Waves", "📻"),
    MINIMAL_HALO("Subtle Halo", "💡"),
    OFF("Off (Canvas)", "🔋")
}

data class LightingPreferences(
    val selectedSwatchHex: Long = 0xFFFA2D48L, // Signature Apple Music red #fa2d48
    val isAutoPalette: Boolean = true,
    val mode: LightingMode = LightingMode.BEAT_REACTIVE,
    val intensity: Float = 0.80f,
    val speedFactor: Float = 1.0f
)

object LightingCatalog {
    val SWATCHES = listOf(
        LightingColorSwatch("Red (Signature)", 0xFFFA2D48L),
        LightingColorSwatch("Crimson", 0xFFDC143CL),
        LightingColorSwatch("Coral", 0xFFFF6B5EL),
        LightingColorSwatch("Orange", 0xFFFF8A1FL),
        LightingColorSwatch("Amber", 0xFFFFB020L),
        LightingColorSwatch("Yellow", 0xFFFFD60AL),
        LightingColorSwatch("Gold", 0xFFE6B800L),
        LightingColorSwatch("Lime", 0xFFB6E63AL),
        LightingColorSwatch("Neon Green", 0xFF30D158L),
        LightingColorSwatch("Spotify Green", 0xFF1ED760L),
        LightingColorSwatch("Forest", 0xFF34A853L),
        LightingColorSwatch("Mint", 0xFF5EE6B0L),
        LightingColorSwatch("Teal", 0xFF19C3B1L),
        LightingColorSwatch("Cyan", 0xFF22D3EEL),
        LightingColorSwatch("Sky Blue", 0xFF4CC2FFL),
        LightingColorSwatch("Ocean", 0xFF0A9BD1L),
        LightingColorSwatch("Royal Blue", 0xFF2F7BFFL),
        LightingColorSwatch("Electric Indigo", 0xFF5E5CE6L),
        LightingColorSwatch("Violet", 0xFF8B5CF6L),
        LightingColorSwatch("Purple", 0xFFA259FFL),
        LightingColorSwatch("Magenta", 0xFFE039D5L),
        LightingColorSwatch("Hot Pink", 0xFFFF2D95L),
        LightingColorSwatch("Rose", 0xFFFF7096L),
        LightingColorSwatch("Peach", 0xFFFFA07AL),
        LightingColorSwatch("Slate", 0xFF8E9BB5L),
        LightingColorSwatch("Silver", 0xFFC7CCD6L),
        LightingColorSwatch("Pure White", 0xFFF2F2F7L)
    )
}
