package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.EqualizerPreset
import com.example.model.LyricLine
import com.example.model.Song
import com.example.player.LrcParser
import com.example.ui.theme.TTKGlassModifiers.glassCard
import java.util.Locale
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenPlayerSheet(
    song: Song?,
    isPlaying: Boolean,
    currentPositionMs: Long,
    durationMs: Long,
    amplitude: Float,
    isFavorite: Boolean,
    isShuffle: Boolean,
    isLooping: Boolean,
    equalizerPreset: EqualizerPreset,
    primaryAccent: Color,
    glowColor: Color,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onTogglePlay: () -> Unit,
    onSeek: (Long) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleLooping: () -> Unit,
    onSelectPreset: (EqualizerPreset) -> Unit,
    onOpenAiInsights: () -> Unit,
    onArtistClick: (String) -> Unit
) {
    if (song == null) return

    var activeViewTab by remember { mutableIntStateOf(0) } // 0: Cover / Art, 1: Synced Lyrics, 2: Equalizer
    var isSeeking by remember { mutableStateOf(false) }
    var scrubProgress by remember { mutableFloatStateOf(0f) }
    var showShareTileDialog by remember { mutableStateOf(false) }
    var selectedTileLineIndex by remember { mutableIntStateOf(0) }

    val totalMs = durationMs.coerceAtLeast(1L)
    val actualProgress = (currentPositionMs.toFloat() / totalMs.toFloat()).coerceIn(0f, 1f)
    val sliderValue = if (isSeeking) scrubProgress else actualProgress

    val infiniteTransition = rememberInfiniteTransition(label = "cover_pulse")
    val breatheScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cover_breathe"
    )

    val parsedLyrics = remember(song.lrcContent) {
        LrcParser.parse(song.lrcContent)
    }
    val activeLyricIndex = remember(parsedLyrics, currentPositionMs) {
        LrcParser.getActiveLineIndex(parsedLyrics, currentPositionMs)
    }

    val lyricsListState = rememberLazyListState()

    // Auto-scroll lyrics smoothly to active line
    LaunchedEffect(activeLyricIndex) {
        if (activeViewTab == 1 && activeLyricIndex >= 0 && activeLyricIndex < parsedLyrics.size) {
            val target = (activeLyricIndex - 2).coerceAtLeast(0)
            lyricsListState.animateScrollToItem(target)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xF5080E1C),
        dragHandle = null,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .testTag("fullscreen_player_sheet")
        ) {
            // Top Bar: Collapse, Mode Selector Tabs, AI Studio button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("collapse_player_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Collapse Player",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Mode Tabs: Cover / Lyrics / EQ (Apple Music Style)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .padding(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val tabs = listOf("Cover 🖼️", "Lyrics 📜", "EQ 🎚️")
                    tabs.forEachIndexed { index, title ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (activeViewTab == index) primaryAccent.copy(alpha = 0.25f)
                                    else Color.Transparent
                                )
                                .clickable { activeViewTab = index }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (activeViewTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (activeViewTab == index) primaryAccent else Color.White.copy(alpha = 0.7f)
                                )
                            )
                        }
                    }
                }

                // Gemini AI Insights Button
                IconButton(
                    onClick = onOpenAiInsights,
                    modifier = Modifier.testTag("ai_insights_button")
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(primaryAccent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Insights",
                            tint = primaryAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Center Dynamic View: Tab 0 (Clean Minimalist Square Art), Tab 1 (Lyrics + Share Tile), Tab 2 (Equalizer)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when (activeViewTab) {
                    0 -> {
                        // Clean Minimalistic Album Cover Square (Apple Music Style - NO VINYL)
                        Box(
                            modifier = Modifier
                                .size(280.dp)
                                .shadow(
                                    elevation = 28.dp,
                                    shape = RoundedCornerShape(24.dp),
                                    ambientColor = glowColor.copy(alpha = 0.5f),
                                    spotColor = primaryAccent
                                )
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color(0xFF151D2E))
                                .border(
                                    width = 1.5.dp,
                                    brush = Brush.verticalGradient(
                                        listOf(
                                            Color.White.copy(alpha = 0.45f),
                                            primaryAccent.copy(alpha = 0.35f),
                                            Color.White.copy(alpha = 0.1f)
                                        )
                                    ),
                                    shape = RoundedCornerShape(24.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            val coverModifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(24.dp))

                            if (song.localCoverRes != null) {
                                Image(
                                    painter = painterResource(id = song.localCoverRes),
                                    contentDescription = "Album art",
                                    modifier = coverModifier,
                                    contentScale = ContentScale.Crop
                                )
                            } else if (song.coverUrl != null) {
                                AsyncImage(
                                    model = song.coverUrl,
                                    contentDescription = "Album art",
                                    modifier = coverModifier,
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = "Track",
                                    tint = primaryAccent,
                                    modifier = Modifier.size(80.dp)
                                )
                            }
                        }
                    }

                    1 -> {
                        // Synchronized Karaoke Lyrics + Shareable Glass Lyric Tile
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Top banner for Shareable Glass Lyric Tile
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Karaoke Lyrics 📜",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                )

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(primaryAccent.copy(alpha = 0.2f))
                                        .border(1.dp, primaryAccent.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                                        .clickable {
                                            selectedTileLineIndex = activeLyricIndex.coerceAtLeast(0)
                                            showShareTileDialog = true
                                        }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                        .testTag("open_share_tile_button")
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.FormatQuote,
                                            contentDescription = "Share Lyric Tile",
                                            tint = primaryAccent,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Share Lyric Tile 📤",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = primaryAccent
                                            )
                                        )
                                    }
                                }
                            }

                            if (parsedLyrics.isEmpty()) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "No Synchronized Lyrics Found 📜",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.White.copy(alpha = 0.6f)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Import a .lrc file to follow along karaoke-style!",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.4f)
                                    )
                                }
                            } else {
                                LazyColumn(
                                    state = lyricsListState,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    itemsIndexed(parsedLyrics) { index, lyric ->
                                        val isCurrent = index == activeLyricIndex
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(
                                                    if (isCurrent) primaryAccent.copy(alpha = 0.12f)
                                                    else Color.Transparent
                                                )
                                                .clickable { onSeek(lyric.timeMs) }
                                                .padding(vertical = 10.dp, horizontal = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = lyric.text,
                                                style = if (isCurrent) {
                                                    MaterialTheme.typography.titleMedium.copy(
                                                        fontSize = 19.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = primaryAccent,
                                                        lineHeight = 26.sp
                                                    )
                                                } else {
                                                    MaterialTheme.typography.bodyLarge.copy(
                                                        fontSize = 15.sp,
                                                        color = Color.White.copy(alpha = 0.45f),
                                                        lineHeight = 22.sp
                                                    )
                                                },
                                                textAlign = TextAlign.Start,
                                                modifier = Modifier.weight(1f)
                                            )

                                            IconButton(
                                                onClick = {
                                                    selectedTileLineIndex = index
                                                    showShareTileDialog = true
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Share,
                                                    contentDescription = "Share Line",
                                                    tint = if (isCurrent) primaryAccent else Color.White.copy(alpha = 0.35f),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        // Equalizer Visualizer & Preset Selector
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Harmonic Equalizer & Visualizer 🎚️",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            // Dynamic Equalizer Frequency Bars
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .glassCard(surfaceAlpha = 0.1f, borderColor = primaryAccent.copy(alpha = 0.3f))
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                val bars = 14
                                for (i in 0 until bars) {
                                    val barBoost = when {
                                        i < 4 -> equalizerPreset.bassFactor
                                        i < 9 -> equalizerPreset.midFactor
                                        else -> equalizerPreset.trebleFactor
                                    }
                                    val harmonic = sin((i * 0.5f) + (currentPositionMs * 0.005f))
                                    val dynamicHeight = (20f + (amplitude * 70f * barBoost) + (harmonic * 15f))
                                        .coerceIn(12f, 90f)

                                    Box(
                                        modifier = Modifier
                                            .width(12.dp)
                                            .height(dynamicHeight.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                Brush.verticalGradient(
                                                    listOf(primaryAccent, glowColor)
                                                )
                                            )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Equalizer Presets Chips
                            Text(
                                text = "Select Preset 🎧",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                EqualizerPreset.entries.forEach { preset ->
                                    val isSelected = preset == equalizerPreset
                                    Column(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (isSelected) primaryAccent.copy(alpha = 0.25f)
                                                else Color.White.copy(alpha = 0.06f)
                                            )
                                            .clickable { onSelectPreset(preset) }
                                            .padding(horizontal = 8.dp, vertical = 8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = preset.emoji, fontSize = 20.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = preset.title,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) primaryAccent else Color.White.copy(alpha = 0.8f)
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Song Title, Clickable Artist & Favorite Heart
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${song.artist} • ${song.album}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = primaryAccent.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .clickable { onArtistClick(song.artist) }
                            .testTag("fullscreen_artist_name")
                    )
                }

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.testTag("favorite_button")
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFFF416C) else Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Seek Bar Slider & Timestamps
            Column(modifier = Modifier.fillMaxWidth()) {
                Slider(
                    value = sliderValue,
                    onValueChange = {
                        isSeeking = true
                        scrubProgress = it
                    },
                    onValueChangeFinished = {
                        val seekTargetMs = (scrubProgress * totalMs).toLong()
                        onSeek(seekTargetMs)
                        isSeeking = false
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = primaryAccent,
                        activeTrackColor = primaryAccent,
                        inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("playback_slider")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val displayedPos = if (isSeeking) (scrubProgress * totalMs).toLong() else currentPositionMs
                    Text(
                        text = formatTime(displayedPos),
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.6f))
                    )
                    Text(
                        text = formatTime(totalMs),
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.6f))
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Primary Control Deck: Shuffle, Prev, Play/Pause, Next, Loop
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Shuffle Toggle
                IconButton(
                    onClick = onToggleShuffle,
                    modifier = Modifier.testTag("shuffle_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (isShuffle) primaryAccent else Color.White.copy(alpha = 0.5f)
                    )
                }

                // Previous Button
                IconButton(
                    onClick = onPrevious,
                    modifier = Modifier.size(48.dp).testTag("previous_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Play / Pause Giant Button
                FilledIconButton(
                    onClick = onTogglePlay,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = primaryAccent,
                        contentColor = Color(0xFF070B14)
                    ),
                    modifier = Modifier
                        .size(68.dp)
                        .shadow(16.dp, CircleShape, spotColor = primaryAccent)
                        .testTag("play_pause_button")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(38.dp)
                    )
                }

                // Next Button
                IconButton(
                    onClick = onNext,
                    modifier = Modifier.size(48.dp).testTag("next_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Loop Toggle
                IconButton(
                    onClick = onToggleLooping,
                    modifier = Modifier.testTag("loop_button")
                ) {
                    Icon(
                        imageVector = if (isLooping) Icons.Default.Repeat else Icons.Default.RepeatOne,
                        contentDescription = "Looping",
                        tint = if (isLooping) primaryAccent else Color.White.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Shareable Glass Lyrics Tile Modal
        if (showShareTileDialog) {
            ShareableLyricsTileDialog(
                song = song,
                lyrics = parsedLyrics,
                initialSelectedIndices = listOf(selectedTileLineIndex),
                primaryAccent = primaryAccent,
                glowColor = glowColor,
                onDismiss = { showShareTileDialog = false }
            )
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%02d:%02d", minutes, seconds)
}
