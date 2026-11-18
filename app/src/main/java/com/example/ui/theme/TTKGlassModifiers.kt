package com.example.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object TTKGlassModifiers {
    val GlassWhiteG1 = Color(0x1AFFFFFF) // rgba(255,255,255,0.10)
    val GlassWhiteG2 = Color(0x06FFFFFF) // rgba(255,255,255,0.02)
    val GlassBorderLuminous = Color(0x2EFFFFFF) // rgba(255,255,255,0.18)

    /**
     * Physical frosted glass slab implementation inspired by Apple Music & high-end acoustics:
     * - Top specular highlight
     * - Multi-point silica gradient
     * - Soft lateral rim
     * - Deep ambient elevation shadow with optional backlight tint
     */
    fun Modifier.glassCard(
        shape: Shape = RoundedCornerShape(22.dp),
        elevation: Dp = 16.dp,
        surfaceAlpha: Float = 0.12f,
        borderColor: Color = Color(0x33FFFFFF),
        topHighlightAlpha: Float = 0.40f,
        backlightGlow: Color? = null
    ): Modifier {
        return this
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = backlightGlow?.copy(alpha = 0.4f) ?: Color.Black.copy(alpha = 0.6f),
                spotColor = Color.Black.copy(alpha = 0.85f)
            )
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = surfaceAlpha + 0.06f),
                        Color.White.copy(alpha = surfaceAlpha * 0.45f),
                        Color(0x380A0E1A),
                        Color.White.copy(alpha = surfaceAlpha * 0.7f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(400f, 800f)
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = topHighlightAlpha),
                        borderColor,
                        Color.White.copy(alpha = 0.08f)
                    )
                ),
                shape = shape
            )
            .drawBehind {
                // Inset specular top catchlight
                drawLine(
                    color = Color.White.copy(alpha = topHighlightAlpha),
                    start = Offset(16f, 1f),
                    end = Offset(size.width - 16f, 1f),
                    strokeWidth = 2.5f
                )
                // Subtle bottom edge rim
                drawLine(
                    color = Color.White.copy(alpha = 0.08f),
                    start = Offset(24f, size.height - 1f),
                    end = Offset(size.width - 24f, size.height - 1f),
                    strokeWidth = 1f
                )
            }
    }

    /**
     * Lit Frosted Glass Pill Dock (like #tabs in Apple Music)
     */
    fun Modifier.glassPill(
        borderColor: Color = Color(0x38FFFFFF),
        surfaceColor: Color = Color(0xD0080D1A),
        accentGlow: Color? = null
    ): Modifier {
        val shape = RoundedCornerShape(32.dp)
        return this
            .shadow(
                elevation = 20.dp,
                shape = shape,
                ambientColor = accentGlow?.copy(alpha = 0.45f) ?: Color.Black.copy(alpha = 0.6f),
                spotColor = Color.Black.copy(alpha = 0.8f)
            )
            .clip(shape)
            .background(surfaceColor)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.42f),
                        borderColor,
                        Color.White.copy(alpha = 0.12f)
                    )
                ),
                shape = shape
            )
            .drawBehind {
                drawLine(
                    color = Color.White.copy(alpha = 0.35f),
                    start = Offset(28f, 1f),
                    end = Offset(size.width - 28f, 1f),
                    strokeWidth = 2f
                )
            }
    }
}
