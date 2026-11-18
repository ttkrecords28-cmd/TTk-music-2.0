package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.ColorThemeId
import com.example.model.Song
import com.example.ui.components.AppNavTab
import com.example.ui.theme.TTKGlassModifiers.glassCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    songs: List<Song>,
    currentSong: Song?,
    isPlaying: Boolean,
    favoriteIds: Set<String>,
    currentTheme: ColorThemeId,
    isAmbientEnabled: Boolean,
    primaryAccent: Color,
    glowColor: Color,
    navTab: AppNavTab = AppNavTab.HOME,
    onSongClick: (Song) -> Unit,
    onTogglePlay: () -> Unit,
    onExpandPlayer: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onOpenLightingCustomizer: () -> Unit,
    onOpenThemePicker: () -> Unit,
    onOpenImportPicker: () -> Unit,
    onArtistClick: (String) -> Unit,
    onNavigateToTuner: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("All Tracks 🎵") }
    var searchQuery by remember { mutableStateOf("") }
    val categories = listOf("All Tracks 🎵", "Favorites ❤️", "Synthwave ⚡", "Lo-Fi ☕", "Ambient 🌌")

    val filteredSongs = remember(songs, selectedCategory, favoriteIds) {
        when (selectedCategory) {
            "Favorites ❤️" -> songs.filter { favoriteIds.contains(it.id) }
            "Synthwave ⚡" -> songs.filter { it.genre.contains("Synthwave", ignoreCase = true) }
            "Lo-Fi ☕" -> songs.filter { it.genre.contains("Lo-Fi", ignoreCase = true) }
            "Ambient 🌌" -> songs.filter { it.genre.contains("Ambient", ignoreCase = true) || it.genre.contains("Space", ignoreCase = true) }
            else -> songs
        }
    }

    val searchResults = remember(songs, searchQuery) {
        if (searchQuery.isBlank()) songs
        else {
            val q = searchQuery.trim().lowercase()
            songs.filter {
                it.title.lowercase().contains(q) ||
                it.artist.lowercase().contains(q) ||
                it.album.lowercase().contains(q) ||
                it.genre.lowercase().contains(q) ||
                it.lrcContent.lowercase().contains(q)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Clean Apple Music Style Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand Header with Icon
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(primaryAccent, glowColor)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = "Logo",
                            tint = Color(0xFF070B14),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "TTK Music 🎵",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = (-0.5).sp
                            )
                        )
                        Text(
                            text = "Frosted Acoustic Studio ✨",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = primaryAccent.copy(alpha = 0.9f)
                            )
                        )
                    }
                }

                // Action Controls: Tuner Jump + Ambient Lighting Customizer + Palette Theme + Import
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Quick Tuner FM Badge Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(primaryAccent.copy(alpha = 0.15f))
                            .border(1.dp, primaryAccent.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .clickable { onNavigateToTuner() }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .testTag("quick_tuner_jump_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Radio,
                                contentDescription = "Tuner Jump",
                                tint = primaryAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${currentSong?.tunerFrequency ?: 94.7f} FM 📻",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryAccent
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Ambient Lighting Studio Tuner Button
                    IconButton(
                        onClick = onOpenLightingCustomizer,
                        modifier = Modifier.testTag("ambient_lighting_customizer_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Ambient Lighting Studio",
                            tint = if (isAmbientEnabled) primaryAccent else Color.White.copy(alpha = 0.4f),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Theme Picker Button
                    IconButton(
                        onClick = onOpenThemePicker,
                        modifier = Modifier.testTag("theme_picker_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Theme Picker",
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Local Audio File Import Button
                    IconButton(
                        onClick = onOpenImportPicker,
                        modifier = Modifier.testTag("import_files_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileOpen,
                            contentDescription = "Import Audio & LRC Files",
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Tab Content Based on Selected Nav Tab
            when (navTab) {
                AppNavTab.SEARCH -> {
                    // Dedicated Apple Music Search Screen
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp)
                    ) {
                        // Frosted Glass Search Field
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = {
                                Text(
                                    "Search songs, artists, lyrics... 🔍",
                                    color = Color.White.copy(alpha = 0.5f),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = primaryAccent
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear",
                                            tint = Color.White.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                            },
                            shape = RoundedCornerShape(18.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White.copy(alpha = 0.08f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                                focusedBorderColor = primaryAccent,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("search_text_field")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "${searchResults.size} Tracks Found 🔎",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(bottom = 170.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(searchResults) { song ->
                                SongCardItem(
                                    song = song,
                                    isCurrent = song.id == currentSong?.id,
                                    isPlaying = isPlaying,
                                    isFavorite = favoriteIds.contains(song.id),
                                    primaryAccent = primaryAccent,
                                    onSongClick = { onSongClick(song) },
                                    onToggleFavorite = { onToggleFavorite(song.id) },
                                    onArtistClick = { onArtistClick(song.artist) }
                                )
                            }
                        }
                    }
                }

                AppNavTab.LIBRARY -> {
                    // Apple Music Style Library Screen
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        contentPadding = PaddingValues(bottom = 170.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "Your Sound Library 📚✨",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "${songs.size} total tracks • ${favoriteIds.size} favorites",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = primaryAccent.copy(alpha = 0.85f)
                                )
                            )
                        }

                        // Library Hub Quick Cards
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Favorites Quick Tile
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .glassCard(
                                            shape = RoundedCornerShape(18.dp),
                                            elevation = 8.dp,
                                            surfaceAlpha = 0.12f,
                                            borderColor = Color(0xFFFF416C).copy(alpha = 0.35f)
                                        )
                                        .clickable { selectedCategory = "Favorites ❤️" },
                                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Icon(
                                            imageVector = Icons.Default.Favorite,
                                            contentDescription = "Favorites",
                                            tint = Color(0xFFFF416C),
                                            modifier = Modifier.size(28.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Favorites ❤️",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        )
                                        Text(
                                            text = "${favoriteIds.size} tracks",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color.White.copy(alpha = 0.6f)
                                            )
                                        )
                                    }
                                }

                                // Imported Local Audio Tile
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .glassCard(
                                            shape = RoundedCornerShape(18.dp),
                                            elevation = 8.dp,
                                            surfaceAlpha = 0.12f,
                                            borderColor = primaryAccent.copy(alpha = 0.35f)
                                        )
                                        .clickable { onOpenImportPicker() },
                                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Icon(
                                            imageVector = Icons.Default.FileOpen,
                                            contentDescription = "Import",
                                            tint = primaryAccent,
                                            modifier = Modifier.size(28.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Import Local 📁",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        )
                                        Text(
                                            text = "Add MP3/LRC",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color.White.copy(alpha = 0.6f)
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Text(
                                text = "All Library Tracks 🎶",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }

                        items(songs) { song ->
                            SongCardItem(
                                song = song,
                                isCurrent = song.id == currentSong?.id,
                                isPlaying = isPlaying,
                                isFavorite = favoriteIds.contains(song.id),
                                primaryAccent = primaryAccent,
                                onSongClick = { onSongClick(song) },
                                onToggleFavorite = { onToggleFavorite(song.id) },
                                onArtistClick = { onArtistClick(song.artist) }
                            )
                        }
                    }
                }

                else -> {
                    // Default AppNavTab.HOME Screen
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(bottom = 170.dp)
                    ) {
                        // Persistent Landing State Hero Card (Apple Music Clean Minimalistic Style)
                        item {
                            currentSong?.let { song ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp, vertical = 6.dp)
                                        .testTag("hero_landing_card")
                                        .glassCard(
                                            shape = RoundedCornerShape(22.dp),
                                            elevation = 16.dp,
                                            surfaceAlpha = 0.16f,
                                            borderColor = primaryAccent.copy(alpha = 0.35f)
                                        )
                                        .clickable { onExpandPlayer() },
                                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Clean Minimalistic Album Cover Square (NO VINYL)
                                        Box(
                                            modifier = Modifier
                                                .size(76.dp)
                                                .clip(RoundedCornerShape(16.dp))
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
                                                    contentDescription = "Music",
                                                    tint = primaryAccent,
                                                    modifier = Modifier.size(36.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(16.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = if (isPlaying) "NOW PLAYING 🎶" else "LAST PLAYED 🎧",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = primaryAccent,
                                                        fontWeight = FontWeight.Bold,
                                                        letterSpacing = 1.sp
                                                    )
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "• ${song.tunerFrequency} FM 📻",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = Color.White.copy(alpha = 0.5f),
                                                        fontSize = 10.sp
                                                    )
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = song.title,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                ),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "${song.artist} • ${song.album}",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = Color.White.copy(alpha = 0.7f)
                                                ),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        FilledIconButton(
                                            onClick = onTogglePlay,
                                            colors = IconButtonDefaults.filledIconButtonColors(
                                                containerColor = primaryAccent,
                                                contentColor = Color(0xFF070B14)
                                            ),
                                            modifier = Modifier.size(46.dp).testTag("hero_play_button")
                                        ) {
                                            Icon(
                                                imageVector = if (isPlaying) Icons.Default.GraphicEq else Icons.Default.PlayArrow,
                                                contentDescription = "Play",
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Import Files Banner Card
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 6.dp)
                                    .testTag("import_banner_card")
                                    .glassCard(
                                        shape = RoundedCornerShape(18.dp),
                                        elevation = 8.dp,
                                        surfaceAlpha = 0.08f,
                                        borderColor = Color.White.copy(alpha = 0.2f)
                                    )
                                    .clickable { onOpenImportPicker() },
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color.White.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.FileOpen,
                                            contentDescription = "Import",
                                            tint = primaryAccent,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Import Local Audio & LRC Files 📁",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White
                                            )
                                        )
                                        Text(
                                            text = "Supports .mp3, .wav, .flac, .ogg, .m4a and synced .lrc lyrics",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color.White.copy(alpha = 0.65f),
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        // Filter Category Chips
                        item {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                contentPadding = PaddingValues(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(categories) { category ->
                                    val isSelected = category == selectedCategory
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(
                                                if (isSelected) primaryAccent.copy(alpha = 0.25f)
                                                else Color.White.copy(alpha = 0.08f)
                                            )
                                            .border(
                                                1.dp,
                                                if (isSelected) primaryAccent else Color.Transparent,
                                                RoundedCornerShape(16.dp)
                                            )
                                            .clickable { selectedCategory = category }
                                            .padding(horizontal = 14.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = category,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) primaryAccent else Color.White.copy(alpha = 0.8f)
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        // Song List Header
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${filteredSongs.size} Songs in Deck 🎶",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                        }

                        // Song Items with clean square artwork
                        items(filteredSongs) { song ->
                            SongCardItem(
                                song = song,
                                isCurrent = song.id == currentSong?.id,
                                isPlaying = isPlaying,
                                isFavorite = favoriteIds.contains(song.id),
                                primaryAccent = primaryAccent,
                                onSongClick = { onSongClick(song) },
                                onToggleFavorite = { onToggleFavorite(song.id) },
                                onArtistClick = { onArtistClick(song.artist) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SongCardItem(
    song: Song,
    isCurrent: Boolean,
    isPlaying: Boolean,
    isFavorite: Boolean,
    primaryAccent: Color,
    onSongClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onArtistClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .testTag("song_item_${song.id}")
            .glassCard(
                shape = RoundedCornerShape(16.dp),
                elevation = if (isCurrent) 12.dp else 4.dp,
                surfaceAlpha = if (isCurrent) 0.18f else 0.07f,
                borderColor = if (isCurrent) primaryAccent.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.15f)
            )
            .clickable { onSongClick() },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Clean Minimalistic Album Cover Square (NO VINYL)
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF161E30)),
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
                        contentDescription = "Music",
                        tint = primaryAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }

                if (isCurrent && isPlaying) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Playing",
                            tint = primaryAccent,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                        color = if (isCurrent) primaryAccent else Color.White
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = song.artist,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.65f)
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.clickable { onArtistClick() }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${song.tunerFrequency} FM 📻",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = primaryAccent.copy(alpha = 0.75f),
                            fontSize = 9.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Favorite Icon
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color(0xFFFF416C) else Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
