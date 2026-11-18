package com.example

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.LightingMode
import com.example.ui.components.AiInsightsSheet
import com.example.ui.components.AmbientBackground
import com.example.ui.components.AppBottomGlassTabs
import com.example.ui.components.AppNavTab
import com.example.ui.components.ArtistHubDialog
import com.example.ui.components.FloatingControlDeck
import com.example.ui.components.FullScreenPlayerSheet
import com.example.ui.components.LightingCustomizerDialog
import com.example.ui.components.ThemeSelectorDialog
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.RadioTunerScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.util.configureAppCheck
import com.example.viewmodel.MusicViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Mandatory App Check configuration with launch intent before any UI or Firebase operations
        configureAppCheck(this, intent)

        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    TTKMusicApp(
                        onPickAudio = { callback ->
                            audioPickerCallback = callback
                            audioPickerLauncher.launch("audio/*")
                        },
                        onPickLrc = { callback ->
                            lrcPickerCallback = callback
                            lrcPickerLauncher.launch("*/*")
                        }
                    )
                }
            }
        }
    }

    private var audioPickerCallback: ((Uri) -> Unit)? = null
    private val audioPickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { audioPickerCallback?.invoke(it) }
    }

    private var lrcPickerCallback: ((String) -> Unit)? = null
    private val lrcPickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { fileUri ->
            try {
                contentResolver.openInputStream(fileUri)?.bufferedReader()?.use { reader ->
                    val content = reader.readText()
                    lrcPickerCallback?.invoke(content)
                }
            } catch (_: Exception) {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TTKMusicApp(
    onPickAudio: ((Uri) -> Unit) -> Unit,
    onPickLrc: ((String) -> Unit) -> Unit,
    viewModel: MusicViewModel = viewModel()
) {
    val songs by viewModel.songs.collectAsStateWithLifecycle()
    val currentSong by viewModel.playerManager.currentSong.collectAsStateWithLifecycle()
    val isPlaying by viewModel.playerManager.isPlaying.collectAsStateWithLifecycle()
    val currentPositionMs by viewModel.playerManager.currentPositionMs.collectAsStateWithLifecycle()
    val durationMs by viewModel.playerManager.durationMs.collectAsStateWithLifecycle()
    val amplitude by viewModel.playerManager.amplitude.collectAsStateWithLifecycle()
    val equalizerPreset by viewModel.playerManager.equalizerPreset.collectAsStateWithLifecycle()
    val isShuffle by viewModel.playerManager.isShuffle.collectAsStateWithLifecycle()
    val isLooping by viewModel.playerManager.isLooping.collectAsStateWithLifecycle()

    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()
    val currentTheme by viewModel.currentTheme.collectAsStateWithLifecycle()
    val lightingPreferences by viewModel.lightingPreferences.collectAsStateWithLifecycle()
    val selectedNavTab by viewModel.selectedNavTab.collectAsStateWithLifecycle()

    val isPlayerExpanded by viewModel.isPlayerExpanded.collectAsStateWithLifecycle()
    val isThemeDialogVisible by viewModel.isThemeDialogVisible.collectAsStateWithLifecycle()
    val isLightingCustomizerVisible by viewModel.isLightingCustomizerVisible.collectAsStateWithLifecycle()
    val selectedArtist by viewModel.selectedArtist.collectAsStateWithLifecycle()
    val isAiSheetVisible by viewModel.isAiSheetVisible.collectAsStateWithLifecycle()

    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
    val lyricsInsight by viewModel.lyricsInsight.collectAsStateWithLifecycle()
    val behindTheBeat by viewModel.behindTheBeat.collectAsStateWithLifecycle()
    val smartQueue by viewModel.smartQueue.collectAsStateWithLifecycle()

    val primaryAccent = Color(lightingPreferences.selectedSwatchHex)
    val glowColor = Color(currentTheme.glowColor)

    val playerSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val aiSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    // Handle back button: dismiss player first, or return to Home if on sub-tab
    if (isPlayerExpanded) {
        BackHandler {
            scope.launch {
                playerSheetState.hide()
                viewModel.setPlayerExpanded(false)
            }
        }
    } else if (selectedNavTab != AppNavTab.HOME) {
        BackHandler {
            viewModel.selectNavTab(AppNavTab.HOME)
        }
    }

    AmbientBackground(
        theme = currentTheme,
        lightingPrefs = lightingPreferences,
        audioAmplitude = amplitude
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Main Screen Routing
            when (selectedNavTab) {
                AppNavTab.TUNER -> {
                    RadioTunerScreen(
                        songs = songs,
                        currentSong = currentSong,
                        isPlaying = isPlaying,
                        audioAmplitude = amplitude,
                        primaryAccent = primaryAccent,
                        glowColor = glowColor,
                        onSelectSong = { song -> viewModel.playSong(song) }
                    )
                }
                else -> {
                    HomeScreen(
                        songs = songs,
                        currentSong = currentSong,
                        isPlaying = isPlaying,
                        favoriteIds = favoriteIds,
                        currentTheme = currentTheme,
                        isAmbientEnabled = lightingPreferences.mode != LightingMode.OFF,
                        primaryAccent = primaryAccent,
                        glowColor = glowColor,
                        navTab = selectedNavTab,
                        onSongClick = { song -> viewModel.playSong(song) },
                        onTogglePlay = { viewModel.playerManager.togglePlayPause() },
                        onExpandPlayer = { viewModel.setPlayerExpanded(true) },
                        onToggleFavorite = { id -> viewModel.toggleFavorite(id) },
                        onOpenLightingCustomizer = { viewModel.setLightingCustomizerVisible(true) },
                        onOpenThemePicker = { viewModel.setThemeDialogVisible(true) },
                        onOpenImportPicker = {
                            onPickAudio { uri ->
                                viewModel.importAudioFile(uri)
                            }
                        },
                        onArtistClick = { artistName -> viewModel.openArtistHub(artistName) },
                        onNavigateToTuner = { viewModel.selectNavTab(AppNavTab.TUNER) }
                    )
                }
            }

            // Floating Bottom Controls: Mini-Player + Frosted Glass Dock Tabs
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (currentSong != null) {
                    FloatingControlDeck(
                        song = currentSong,
                        isPlaying = isPlaying,
                        currentPositionMs = currentPositionMs,
                        durationMs = durationMs,
                        primaryAccent = primaryAccent,
                        onTogglePlay = { viewModel.playerManager.togglePlayPause() },
                        onNext = { viewModel.playerManager.playNext() },
                        onExpand = { viewModel.setPlayerExpanded(true) },
                        onArtistClick = { artistName -> viewModel.openArtistHub(artistName) }
                    )
                }

                AppBottomGlassTabs(
                    selectedTab = selectedNavTab,
                    onTabSelected = { tab -> viewModel.selectNavTab(tab) },
                    primaryAccent = primaryAccent
                )
            }

            // Expanded Full-Screen Player
            if (isPlayerExpanded && currentSong != null) {
                FullScreenPlayerSheet(
                    song = currentSong,
                    isPlaying = isPlaying,
                    currentPositionMs = currentPositionMs,
                    durationMs = durationMs,
                    amplitude = amplitude,
                    isFavorite = currentSong?.let { favoriteIds.contains(it.id) } ?: false,
                    isShuffle = isShuffle,
                    isLooping = isLooping,
                    equalizerPreset = equalizerPreset,
                    primaryAccent = primaryAccent,
                    glowColor = glowColor,
                    sheetState = playerSheetState,
                    onDismiss = {
                        scope.launch {
                            playerSheetState.hide()
                            viewModel.setPlayerExpanded(false)
                        }
                    },
                    onTogglePlay = { viewModel.playerManager.togglePlayPause() },
                    onSeek = { targetMs -> viewModel.playerManager.seekTo(targetMs) },
                    onNext = { viewModel.playerManager.playNext() },
                    onPrevious = { viewModel.playerManager.playPrevious() },
                    onToggleFavorite = { currentSong?.let { viewModel.toggleFavorite(it.id) } },
                    onToggleShuffle = { viewModel.playerManager.toggleShuffle() },
                    onToggleLooping = { viewModel.playerManager.toggleLooping() },
                    onSelectPreset = { preset -> viewModel.playerManager.setEqualizerPreset(preset) },
                    onOpenAiInsights = { viewModel.openAiSheet() },
                    onArtistClick = { artistName -> viewModel.openArtistHub(artistName) }
                )
            }

            // Theme Selector Modal
            if (isThemeDialogVisible) {
                ThemeSelectorDialog(
                    currentTheme = currentTheme,
                    onSelectTheme = { theme -> viewModel.setTheme(theme) },
                    onDismiss = { viewModel.setThemeDialogVisible(false) }
                )
            }

            // Ambient Lighting Customizer Modal
            if (isLightingCustomizerVisible) {
                LightingCustomizerDialog(
                    preferences = lightingPreferences,
                    onUpdatePreferences = { updated -> viewModel.updateLightingPreferences(updated) },
                    onDismiss = { viewModel.setLightingCustomizerVisible(false) }
                )
            }

            // Artist Hub Modal
            selectedArtist?.let { artist ->
                ArtistHubDialog(
                    artist = artist,
                    primaryAccent = primaryAccent,
                    onDismiss = { viewModel.closeArtistHub() },
                    onPlayTopSong = { songTitle ->
                        val match = songs.find { it.title.equals(songTitle, ignoreCase = true) }
                        match?.let { viewModel.playSong(it) }
                    }
                )
            }

            // Gemini AI Insights Sheet
            if (isAiSheetVisible && currentSong != null) {
                AiInsightsSheet(
                    song = currentSong!!,
                    sheetState = aiSheetState,
                    primaryAccent = primaryAccent,
                    isLoading = isAiLoading,
                    lyricsInsight = lyricsInsight,
                    behindTheBeat = behindTheBeat,
                    smartQueue = smartQueue,
                    onRefresh = { tabIndex -> viewModel.refreshAiAnalysis(tabIndex) },
                    onDismiss = {
                        scope.launch {
                            aiSheetState.hide()
                            viewModel.closeAiSheet()
                        }
                    }
                )
            }
        }
    }
}
