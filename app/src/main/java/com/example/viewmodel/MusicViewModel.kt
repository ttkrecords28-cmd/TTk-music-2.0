package com.example.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiMusicService
import com.example.data.SongRepository
import com.example.model.Artist
import com.example.model.ColorThemeId
import com.example.model.DemoCatalog
import com.example.model.EqualizerPreset
import com.example.model.LightingMode
import com.example.model.LightingPreferences
import com.example.model.Song
import com.example.player.AudioPlayerManager
import com.example.ui.components.AppNavTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MusicViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SongRepository(application)
    val playerManager = AudioPlayerManager(application)
    private val aiService = AiMusicService()

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs.asStateFlow()

    private val _favoriteIds = MutableStateFlow<Set<String>>(repository.getFavoriteIds())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    private val _currentTheme = MutableStateFlow(repository.getTheme())
    val currentTheme: StateFlow<ColorThemeId> = _currentTheme.asStateFlow()

    private val _lightingPreferences = MutableStateFlow(repository.getLightingPreferences())
    val lightingPreferences: StateFlow<LightingPreferences> = _lightingPreferences.asStateFlow()

    private val _selectedNavTab = MutableStateFlow(AppNavTab.HOME)
    val selectedNavTab: StateFlow<AppNavTab> = _selectedNavTab.asStateFlow()

    // Modals & Bottom Sheets
    private val _isPlayerExpanded = MutableStateFlow(false)
    val isPlayerExpanded: StateFlow<Boolean> = _isPlayerExpanded.asStateFlow()

    private val _isThemeDialogVisible = MutableStateFlow(false)
    val isThemeDialogVisible: StateFlow<Boolean> = _isThemeDialogVisible.asStateFlow()

    private val _isLightingCustomizerVisible = MutableStateFlow(false)
    val isLightingCustomizerVisible: StateFlow<Boolean> = _isLightingCustomizerVisible.asStateFlow()

    private val _isShareLyricsVisible = MutableStateFlow(false)
    val isShareLyricsVisible: StateFlow<Boolean> = _isShareLyricsVisible.asStateFlow()

    private val _selectedArtist = MutableStateFlow<Artist?>(null)
    val selectedArtist: StateFlow<Artist?> = _selectedArtist.asStateFlow()

    private val _isAiSheetVisible = MutableStateFlow(false)
    val isAiSheetVisible: StateFlow<Boolean> = _isAiSheetVisible.asStateFlow()

    // AI State
    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _lyricsInsight = MutableStateFlow<String?>(null)
    val lyricsInsight: StateFlow<String?> = _lyricsInsight.asStateFlow()

    private val _behindTheBeat = MutableStateFlow<String?>(null)
    val behindTheBeat: StateFlow<String?> = _behindTheBeat.asStateFlow()

    private val _smartQueue = MutableStateFlow<String?>(null)
    val smartQueue: StateFlow<String?> = _smartQueue.asStateFlow()

    init {
        loadLibrary()
    }

    private fun loadLibrary() {
        val custom = repository.getCustomSongs()
        val allSongs = DemoCatalog.DEFAULT_SONGS + custom
        _songs.value = allSongs
        playerManager.setQueue(allSongs)

        // Restore last played song
        val lastId = repository.getLastPlayedSongId()
        val restoredSong = allSongs.find { it.id == lastId } ?: allSongs.firstOrNull()
        restoredSong?.let { song ->
            // Update without auto-playing immediately
            playerManager.seekTo(0L)
        }
    }

    fun playSong(song: Song) {
        repository.setLastPlayedSongId(song.id)
        playerManager.playSong(song)
        // Reset AI caches for new song
        _lyricsInsight.value = null
        _behindTheBeat.value = null
        _smartQueue.value = null
    }

    fun toggleFavorite(songId: String) {
        val updated = repository.toggleFavorite(songId)
        _favoriteIds.value = updated
    }

    fun setTheme(theme: ColorThemeId) {
        _currentTheme.value = theme
        repository.setTheme(theme)
    }

    fun updateLightingPreferences(preferences: LightingPreferences) {
        _lightingPreferences.value = preferences
        repository.saveLightingPreferences(preferences)
    }

    fun toggleAmbientLighting(enabled: Boolean) {
        val current = _lightingPreferences.value
        val newMode = if (enabled) {
            if (current.mode == LightingMode.OFF) LightingMode.BEAT_REACTIVE else current.mode
        } else {
            LightingMode.OFF
        }
        val updated = current.copy(mode = newMode)
        updateLightingPreferences(updated)
    }

    fun setLightingCustomizerVisible(visible: Boolean) {
        _isLightingCustomizerVisible.value = visible
    }

    fun setShareLyricsVisible(visible: Boolean) {
        _isShareLyricsVisible.value = visible
    }

    fun selectNavTab(tab: AppNavTab) {
        _selectedNavTab.value = tab
    }

    fun setPlayerExpanded(expanded: Boolean) {
        _isPlayerExpanded.value = expanded
    }

    fun setThemeDialogVisible(visible: Boolean) {
        _isThemeDialogVisible.value = visible
    }

    fun openArtistHub(artistName: String) {
        _selectedArtist.value = DemoCatalog.getArtist(artistName)
    }

    fun closeArtistHub() {
        _selectedArtist.value = null
    }

    fun openAiSheet() {
        _isAiSheetVisible.value = true
        val current = playerManager.currentSong.value ?: return
        if (_lyricsInsight.value == null) {
            fetchAiLyricsInsight(current)
        }
    }

    fun closeAiSheet() {
        _isAiSheetVisible.value = false
    }

    fun refreshAiAnalysis(tabIndex: Int) {
        val current = playerManager.currentSong.value ?: return
        when (tabIndex) {
            0 -> fetchAiLyricsInsight(current)
            1 -> fetchAiBehindTheBeat(current)
            2 -> fetchAiSmartQueue(current)
        }
    }

    private fun fetchAiLyricsInsight(song: Song) {
        viewModelScope.launch {
            _isAiLoading.value = true
            try {
                val insight = aiService.explainLyrics(song.title, song.artist, song.lrcContent)
                _lyricsInsight.value = insight
            } finally {
                _isAiLoading.value = false
            }
        }
    }

    private fun fetchAiBehindTheBeat(song: Song) {
        viewModelScope.launch {
            _isAiLoading.value = true
            try {
                val trivia = aiService.getBehindTheBeat(song.title, song.artist, song.genre)
                _behindTheBeat.value = trivia
            } finally {
                _isAiLoading.value = false
            }
        }
    }

    private fun fetchAiSmartQueue(song: Song) {
        viewModelScope.launch {
            _isAiLoading.value = true
            try {
                val queueRec = aiService.curateSmartRecommendations(
                    song.title,
                    song.artist,
                    song.genre,
                    "Nighttime Glass Vibe 🌃"
                )
                _smartQueue.value = queueRec
            } finally {
                _isAiLoading.value = false
            }
        }
    }

    fun importAudioFile(uri: Uri) {
        viewModelScope.launch {
            val importedSong = repository.extractMetadata(uri)
            repository.saveCustomSong(importedSong)
            loadLibrary()
            playSong(importedSong)
        }
    }

    fun importLrcFile(lrcContent: String) {
        val current = playerManager.currentSong.value ?: return
        val updated = current.copy(lrcContent = lrcContent)
        if (current.id.startsWith("local_")) {
            repository.saveCustomSong(updated)
        }
        _songs.value = _songs.value.map { if (it.id == updated.id) updated else it }
        playerManager.setQueue(_songs.value)
    }

    override fun onCleared() {
        super.onCleared()
        playerManager.release()
    }
}
