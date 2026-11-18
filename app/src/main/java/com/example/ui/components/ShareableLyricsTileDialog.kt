package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.model.LyricLine
import com.example.model.Song
import com.example.ui.theme.TTKGlassModifiers.glassCard

@Composable
fun ShareableLyricsTileDialog(
    song: Song,
    lyrics: List<LyricLine>,
    initialSelectedIndices: List<Int>,
    primaryAccent: Color,
    glowColor: Color,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val selectedIndices = remember {
        mutableStateListOf<Int>().apply {
            if (initialSelectedIndices.isNotEmpty()) {
                addAll(initialSelectedIndices)
            } else if (lyrics.isNotEmpty()) {
                add(0)
            }
        }
    }

    val selectedQuoteText = remember(selectedIndices.toList()) {
        selectedIndices.sorted()
            .mapNotNull { if (it in lyrics.indices) lyrics[it].text else null }
            .joinToString("\n")
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("shareable_lyrics_tile_dialog")
                .glassCard(
                    shape = RoundedCornerShape(26.dp),
                    elevation = 28.dp,
                    surfaceAlpha = 0.22f,
                    borderColor = primaryAccent.copy(alpha = 0.45f)
                ),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(primaryAccent.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatQuote,
                                contentDescription = "Lyrics Tile",
                                tint = primaryAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Glass Lyrics Tile 📜✨",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Shareable lyric quote card",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // The Aesthetic Frosted Glass Lyric Tile Preview (Apple Music Style)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 20.dp,
                            shape = RoundedCornerShape(22.dp),
                            ambientColor = glowColor.copy(alpha = 0.5f),
                            spotColor = primaryAccent
                        )
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0x35FFFFFF),
                                    Color(0x1815253A),
                                    Color(0x30080E1C)
                                )
                            )
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.45f),
                                    primaryAccent.copy(alpha = 0.35f),
                                    Color.White.copy(alpha = 0.1f)
                                )
                            ),
                            shape = RoundedCornerShape(22.dp)
                        )
                        .padding(18.dp)
                        .testTag("lyrics_tile_preview")
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Top row of tile: TTK Music Glass Badge & Quote icon
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.12f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "🎵 TTK Music",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = primaryAccent
                                    )
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.FormatQuote,
                                contentDescription = "Quote",
                                tint = primaryAccent.copy(alpha = 0.6f),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Selected Lyric Lines Quote Text (Apple Music clean style)
                        Text(
                            text = if (selectedQuoteText.isNotBlank()) selectedQuoteText else "Tap lines below to select quote...",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                lineHeight = 28.sp,
                                letterSpacing = (-0.3).sp
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Bottom: Clean Minimalistic Album Art Square + Track Info
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black.copy(alpha = 0.25f))
                                .padding(8.dp)
                        ) {
                            // Minimalistic album cover square
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF131A2B)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (song.localCoverRes != null) {
                                    Image(
                                        painter = painterResource(id = song.localCoverRes),
                                        contentDescription = "Cover",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else if (song.coverUrl != null) {
                                    AsyncImage(
                                        model = song.coverUrl,
                                        contentDescription = "Cover",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.MusicNote,
                                        contentDescription = "Song",
                                        tint = primaryAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = song.title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${song.artist} • ${song.album}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = primaryAccent.copy(alpha = 0.85f),
                                        fontSize = 11.sp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Line selection drawer (tap to toggle lines)
                Text(
                    text = "Select Lines to Include in Tile 🎶",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(6.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 140.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .padding(6.dp)
                ) {
                    itemsIndexed(lyrics) { idx, line ->
                        val isChecked = selectedIndices.contains(idx)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isChecked) primaryAccent.copy(alpha = 0.18f)
                                    else Color.Transparent
                                )
                                .clickable {
                                    if (isChecked) {
                                        if (selectedIndices.size > 1) {
                                            selectedIndices.remove(idx)
                                        }
                                    } else {
                                        if (selectedIndices.size < 4) {
                                            selectedIndices.add(idx)
                                        } else {
                                            Toast.makeText(context, "Up to 4 lines allowed per tile 📜", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = line.text,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    color = if (isChecked) Color.White else Color.White.copy(alpha = 0.6f),
                                    fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            if (isChecked) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = primaryAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions: Copy Quote & Share Tile
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Copy Button
                    FilledTonalButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText(
                                "Lyric Quote",
                                "\"$selectedQuoteText\"\n— ${song.title} by ${song.artist} 🎵 (via TTK Music)"
                            )
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Lyric tile quote copied! 📋✨", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.White.copy(alpha = 0.15f),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("copy_lyric_tile_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Quote 📋", style = MaterialTheme.typography.labelMedium)
                    }

                    // Share Native Button
                    ElevatedButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "✨ Glass Lyrics Tile from TTK Music:\n\n\"$selectedQuoteText\"\n\n🎶 ${song.title} — ${song.artist}\n#TTKMusic #Lyrics"
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Lyric Tile via 📤"))
                        },
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = primaryAccent,
                            contentColor = Color(0xFF070B14)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("share_lyric_tile_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share Tile 📤", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
    }
}
