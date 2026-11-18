package com.example.data

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import com.example.model.ColorThemeId
import com.example.model.DemoCatalog
import com.example.model.LightingMode
import com.example.model.LightingPreferences
import com.example.model.Song
import org.json.JSONArray
import org.json.JSONObject

class SongRepository(private val context: Context) {
    private val prefs = context.getSharedPreferences("ttk_music_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_LAST_PLAYED_ID = "last_played_song_id"
        private const val KEY_THEME = "selected_theme"
        private const val KEY_AMBIENT_LIGHTING = "ambient_lighting_enabled"
        private const val KEY_FAVORITES = "favorite_song_ids"
        private const val KEY_CUSTOM_SONGS = "custom_imported_songs_json"
        private const val KEY_LIGHTING_SWATCH = "lighting_selected_swatch"
        private const val KEY_LIGHTING_AUTO_PALETTE = "lighting_auto_palette"
        private const val KEY_LIGHTING_MODE = "lighting_mode"
        private const val KEY_LIGHTING_INTENSITY = "lighting_intensity"
        private const val KEY_LIGHTING_SPEED = "lighting_speed"
    }

    fun getLastPlayedSongId(): String {
        return prefs.getString(KEY_LAST_PLAYED_ID, DemoCatalog.DEFAULT_SONGS.first().id)
            ?: DemoCatalog.DEFAULT_SONGS.first().id
    }

    fun setLastPlayedSongId(songId: String) {
        prefs.edit().putString(KEY_LAST_PLAYED_ID, songId).apply()
    }

    fun getTheme(): ColorThemeId {
        val name = prefs.getString(KEY_THEME, ColorThemeId.AMBIENT_MIDNIGHT.name)
        return try {
            ColorThemeId.valueOf(name ?: ColorThemeId.AMBIENT_MIDNIGHT.name)
        } catch (_: Exception) {
            ColorThemeId.AMBIENT_MIDNIGHT
        }
    }

    fun setTheme(themeId: ColorThemeId) {
        prefs.edit().putString(KEY_THEME, themeId.name).apply()
    }

    fun isAmbientLightingEnabled(): Boolean {
        return prefs.getBoolean(KEY_AMBIENT_LIGHTING, true)
    }

    fun setAmbientLightingEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AMBIENT_LIGHTING, enabled).apply()
    }

    fun getLightingPreferences(): LightingPreferences {
        val swatchHex = prefs.getLong(KEY_LIGHTING_SWATCH, 0xFFFA2D48L)
        val isAuto = prefs.getBoolean(KEY_LIGHTING_AUTO_PALETTE, true)
        val modeStr = prefs.getString(KEY_LIGHTING_MODE, LightingMode.BEAT_REACTIVE.name)
        val mode = try {
            LightingMode.valueOf(modeStr ?: LightingMode.BEAT_REACTIVE.name)
        } catch (_: Exception) {
            LightingMode.BEAT_REACTIVE
        }
        val intensity = prefs.getFloat(KEY_LIGHTING_INTENSITY, 0.80f)
        val speed = prefs.getFloat(KEY_LIGHTING_SPEED, 1.0f)
        return LightingPreferences(
            selectedSwatchHex = swatchHex,
            isAutoPalette = isAuto,
            mode = mode,
            intensity = intensity,
            speedFactor = speed
        )
    }

    fun saveLightingPreferences(preferences: LightingPreferences) {
        prefs.edit()
            .putLong(KEY_LIGHTING_SWATCH, preferences.selectedSwatchHex)
            .putBoolean(KEY_LIGHTING_AUTO_PALETTE, preferences.isAutoPalette)
            .putString(KEY_LIGHTING_MODE, preferences.mode.name)
            .putFloat(KEY_LIGHTING_INTENSITY, preferences.intensity)
            .putFloat(KEY_LIGHTING_SPEED, preferences.speedFactor)
            .apply()
    }

    fun getFavoriteIds(): Set<String> {
        return prefs.getStringSet(KEY_FAVORITES, setOf("demo_1", "demo_2")) ?: emptySet()
    }

    fun toggleFavorite(songId: String): Set<String> {
        val current = getFavoriteIds().toMutableSet()
        if (current.contains(songId)) {
            current.remove(songId)
        } else {
            current.add(songId)
        }
        prefs.edit().putStringSet(KEY_FAVORITES, current).apply()
        return current
    }

    fun getCustomSongs(): List<Song> {
        val jsonStr = prefs.getString(KEY_CUSTOM_SONGS, null) ?: return emptyList()
        val list = mutableListOf<Song>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    Song(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        artist = obj.getString("artist"),
                        album = obj.getString("album"),
                        durationMs = obj.getLong("durationMs"),
                        uriString = obj.optString("uriString", null),
                        genre = obj.optString("genre", "Imported 🎵"),
                        lrcContent = obj.optString("lrcContent", ""),
                        bpm = obj.optInt("bpm", 120)
                    )
                )
            }
        } catch (e: Exception) {
            Log.e("SongRepository", "Failed to parse custom songs", e)
        }
        return list
    }

    fun saveCustomSong(song: Song) {
        val current = getCustomSongs().filter { it.id != song.id }.toMutableList()
        current.add(song)
        val array = JSONArray()
        current.forEach { s ->
            val obj = JSONObject().apply {
                put("id", s.id)
                put("title", s.title)
                put("artist", s.artist)
                put("album", s.album)
                put("durationMs", s.durationMs)
                put("uriString", s.uriString)
                put("genre", s.genre)
                put("lrcContent", s.lrcContent)
                put("bpm", s.bpm)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_CUSTOM_SONGS, array.toString()).apply()
    }

    /**
     * Extracts ID3 metadata from a user-picked audio file URI client-side.
     */
    fun extractMetadata(uri: Uri): Song {
        val retriever = MediaMetadataRetriever()
        var title = "Imported Audio Track 🎵"
        var artist = "Local Artist 🎧"
        var album = "Local Storage 📁"
        var durationMs = 180000L
        var genre = "Local Audio 🎶"

        try {
            retriever.setDataSource(context, uri)
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)?.let {
                if (it.isNotBlank()) title = it
            }
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)?.let {
                if (it.isNotBlank()) artist = it
            }
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)?.let {
                if (it.isNotBlank()) album = it
            }
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)?.let {
                if (it.isNotBlank()) genre = it
            }
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()?.let {
                if (it > 0) durationMs = it
            }
        } catch (e: Exception) {
            Log.w("SongRepository", "Metadata extraction warning for $uri", e)
            uri.lastPathSegment?.let { seg ->
                title = seg.substringAfterLast("/").substringBeforeLast(".")
            }
        } finally {
            try {
                retriever.release()
            } catch (_: Exception) {}
        }

        return Song(
            id = "local_${System.currentTimeMillis()}",
            title = title,
            artist = artist,
            album = album,
            durationMs = durationMs,
            uriString = uri.toString(),
            genre = genre,
            lrcContent = "",
            isFavorite = false
        )
    }
}
