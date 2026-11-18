package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.model.ColorThemeId
import com.example.model.LightingMode
import com.example.model.LightingPreferences
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AmbientBackground(
    theme: ColorThemeId,
    lightingPrefs: LightingPreferences,
    audioAmplitude: Float,
    dynamicColor: Color? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val activeColor = if (lightingPrefs.isAutoPalette && dynamicColor != null) {
        dynamicColor
    } else {
        Color(lightingPrefs.selectedSwatchHex)
    }

    val glowColor = Color(theme.glowColor)
    val isLightingOn = lightingPrefs.mode != LightingMode.OFF
    val intensity = lightingPrefs.intensity

    val infiniteTransition = rememberInfiniteTransition(label = "ambient_lighting_transition")

    val durationMillis = (14000L / lightingPrefs.speedFactor).toLong().coerceIn(4000L, 30000L)
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis.toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val secondaryPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = (durationMillis * 1.3f).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sec_phase"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF04060A))
    ) {
        if (isLightingOn) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                if (w <= 0f || h <= 0f) return@Canvas

                val pulse = if (lightingPrefs.mode == LightingMode.BEAT_REACTIVE) {
                    audioAmplitude * 0.55f
                } else {
                    0.1f * sin(phase)
                }

                when (lightingPrefs.mode) {
                    LightingMode.BEAT_REACTIVE, LightingMode.AURORA_DRIFT -> {
                        // 7 Layered Harmonic Radial Light Orbs (inspired by Apple Music & HTML canvas engine)
                        for (i in 0 until 7) {
                            val orbPhase = i * 1.57f
                            val ox = w * (0.5f + 0.38f * sin(phase * (0.8f + i * 0.15f) + orbPhase))
                            val oy = h * (0.45f + 0.36f * cos(secondaryPhase * (0.7f + i * 0.12f) + orbPhase * 1.2f))
                            val baseRadius = (w * (0.40f + 0.08f * (i % 3))) * (1.0f + pulse)
                            val orbAlpha = ((0.16f + 0.18f * (i % 2) + pulse * 0.35f) * intensity).coerceIn(0.04f, 0.75f)

                            val orbColor = if (i % 2 == 0) activeColor else glowColor

                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        orbColor.copy(alpha = orbAlpha),
                                        orbColor.copy(alpha = orbAlpha * 0.35f),
                                        Color.Transparent
                                    ),
                                    center = Offset(ox, oy),
                                    radius = baseRadius
                                ),
                                radius = baseRadius,
                                center = Offset(ox, oy)
                            )
                        }
                    }

                    LightingMode.TUNER_SPECTRUM -> {
                        // Radio tuner frequency beams bouncing across the glass
                        val beamCount = 12
                        val colWidth = w / beamCount
                        for (i in 0 until beamCount) {
                            val beamHeight = h * (0.25f + 0.5f * sin((i * 0.7f) + phase) + pulse * 0.4f).coerceIn(0.1f, 0.95f)
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        activeColor.copy(alpha = 0.22f * intensity),
                                        activeColor.copy(alpha = 0.05f)
                                    ),
                                    startY = h - beamHeight,
                                    endY = h
                                ),
                                topLeft = Offset(i * colWidth, h - beamHeight),
                                size = androidx.compose.ui.geometry.Size(colWidth * 0.85f, beamHeight)
                            )
                        }
                    }

                    LightingMode.MINIMAL_HALO -> {
                        // Soft clean halo centered behind player
                        val haloRadius = w * 0.75f
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    activeColor.copy(alpha = 0.32f * intensity),
                                    activeColor.copy(alpha = 0.10f * intensity),
                                    Color.Transparent
                                ),
                                center = Offset(w * 0.5f, h * 0.42f),
                                radius = haloRadius
                            ),
                            radius = haloRadius,
                            center = Offset(w * 0.5f, h * 0.42f)
                        )
                    }

                    LightingMode.OFF -> {}
                }
            }
        }

        // Clean Frosted Diffuser Vignette
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.20f),
                            Color.Black.copy(alpha = 0.45f),
                            Color(0xEE04060A)
                        )
                    )
                )
        )

        content()
    }
}
