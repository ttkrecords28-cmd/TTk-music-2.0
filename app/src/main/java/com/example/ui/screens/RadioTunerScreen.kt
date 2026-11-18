package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Song
import com.example.ui.theme.TTKGlassModifiers.glassCard
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun RadioTunerScreen(
    songs: List<Song>,
    currentSong: Song?,
    isPlaying: Boolean,
    audioAmplitude: Float,
    primaryAccent: Color,
    glowColor: Color,
    onSelectSong: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    var dialFrequency by remember(currentSong) {
        mutableFloatStateOf(currentSong?.tunerFrequency ?: 94.7f)
    }

    val animatedFreq by animateFloatAsState(targetValue = dialFrequency, label = "freq_anim")

    // Find nearest station
    val nearestSong = remember(dialFrequency, songs) {
        songs.minByOrNull { abs(it.tunerFrequency - dialFrequency) }
    }
    val isLockedOn = nearestSong != null && abs(nearestSong.tunerFrequency - dialFrequency) <= 0.6f

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 170.dp)
            .testTag("radio_tuner_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Tuner Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(primaryAccent.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Radio,
                        contentDescription = "Radio",
                        tint = primaryAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Acoustic FM Tuner 📻",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Analog frequency dial & broadcast receiver",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = primaryAccent.copy(alpha = 0.85f)
                        )
                    )
                }
            }

            // Signal status badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isLockedOn) primaryAccent.copy(alpha = 0.25f)
                        else Color.White.copy(alpha = 0.08f)
                    )
                    .border(
                        1.dp,
                        if (isLockedOn) primaryAccent else Color.White.copy(alpha = 0.15f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (isLockedOn) "STEREO LOCKED ⚡" else "TUNING 📡",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isLockedOn) primaryAccent else Color.White.copy(alpha = 0.6f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Large Analog Frequency Readout Screen (Clean Apple Music Glass Look)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .glassCard(
                    shape = RoundedCornerShape(26.dp),
                    elevation = 20.dp,
                    surfaceAlpha = 0.18f,
                    borderColor = primaryAccent.copy(alpha = 0.4f),
                    backlightGlow = glowColor
                ),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "FM BROADCAST BAND",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.5.sp,
                            color = Color.White.copy(alpha = 0.6f),
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = if (isLockedOn) "SNR: +24dB • HI-FI" else "SNR: NOISE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isLockedOn) primaryAccent else Color.White.copy(alpha = 0.4f),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Mega Frequency Numbers
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = String.format("%.1f", animatedFreq),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 62.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            lineHeight = 64.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "MHz",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = primaryAccent,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Station Callout / Song Title
                Text(
                    text = if (isLockedOn && nearestSong != null) {
                        "${nearestSong.tunerStation} • ${nearestSong.title}"
                    } else {
                        "Searching sonic airwaves..."
                    },
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (isLockedOn) primaryAccent else Color.White.copy(alpha = 0.6f)
                    ),
                    textAlign = TextAlign.Center
                )

                if (isLockedOn && nearestSong != null) {
                    Text(
                        text = "${nearestSong.artist} (${nearestSong.genre})",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.75f)
                        ),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Real-time Tuner Spectrum Bar Visualizer (#bars inspo)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.35f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    val bars = 24
                    for (i in 0 until bars) {
                        val barWave = sin(i * 0.4f + dialFrequency * 2.0f)
                        val barHeight = if (isLockedOn) {
                            (6f + (audioAmplitude * 26f) + (barWave * 8f)).coerceIn(4f, 28f)
                        } else {
                            (3f + (barWave * 4f)).coerceIn(2f, 10f)
                        }

                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .height(barHeight.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    if (isLockedOn) primaryAccent
                                    else Color.White.copy(alpha = 0.25f)
                                )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Horizontal Physical Tuner Dial Scale (Tick marks & Red Needle)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .glassCard(
                    shape = RoundedCornerShape(20.dp),
                    elevation = 14.dp,
                    surfaceAlpha = 0.12f,
                    borderColor = Color.White.copy(alpha = 0.2f)
                ),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Dial Scale Canvas with tick marks and center needle
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF090E18))
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val deltaMhz = (dragAmount.x / 18f)
                                dialFrequency = (dialFrequency + deltaMhz).coerceIn(87.5f, 108.0f)
                            }
                        }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val minF = 87.5f
                        val maxF = 108.0f

                        // Draw frequency tick marks
                        val totalSteps = ((maxF - minF) * 10).toInt()
                        for (i in 0..totalSteps) {
                            val f = minF + (i * 0.1f)
                            val normX = (f - minF) / (maxF - minF)
                            val x = normX * w
                            val isMajor = (i % 10) == 0
                            val isHalf = (i % 5) == 0

                            val tickH = if (isMajor) h * 0.55f else if (isHalf) h * 0.35f else h * 0.20f
                            val tickColor = if (isMajor) Color.White.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.25f)

                            drawLine(
                                color = tickColor,
                                start = Offset(x, h - tickH),
                                end = Offset(x, h),
                                strokeWidth = if (isMajor) 2f else 1f
                            )
                        }

                        // Draw tuning needle
                        val needleNorm = (dialFrequency - minF) / (maxF - minF)
                        val needleX = needleNorm * w
                        drawLine(
                            color = primaryAccent,
                            start = Offset(needleX, 0f),
                            end = Offset(needleX, h),
                            strokeWidth = 3.5f
                        )
                        drawCircle(
                            color = primaryAccent,
                            radius = 6f,
                            center = Offset(needleX, h * 0.2f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Frequency Slider
                Slider(
                    value = dialFrequency,
                    onValueChange = {
                        dialFrequency = it
                    },
                    onValueChangeFinished = {
                        nearestSong?.let { onSelectSong(it) }
                    },
                    valueRange = 87.5f..108.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = primaryAccent,
                        activeTrackColor = primaryAccent,
                        inactiveTrackColor = Color.White.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.testTag("tuner_slider")
                )

                // Frequency label bounds
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("88.0", style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.5f)))
                    Text("94.0", style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.5f)))
                    Text("100.0", style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.5f)))
                    Text("104.0", style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.5f)))
                    Text("108.0", style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.5f)))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Preset Station Buttons (Click to Tune Instantly)
        Text(
            text = "Preset Radio Frequencies 📻",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.8f)
            ),
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            songs.take(4).forEach { song ->
                val isCurrent = song.id == currentSong?.id
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (isCurrent) primaryAccent.copy(alpha = 0.25f)
                            else Color.White.copy(alpha = 0.07f)
                        )
                        .border(
                            1.dp,
                            if (isCurrent) primaryAccent else Color.White.copy(alpha = 0.12f),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable {
                            dialFrequency = song.tunerFrequency
                            onSelectSong(song)
                        }
                        .padding(vertical = 10.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${song.tunerFrequency} FM",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isCurrent) primaryAccent else Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = song.title.take(10),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                color = Color.White.copy(alpha = 0.65f)
                            ),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
