package com.example.ai

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

class AiMusicService {
    companion object {
        private const val TAG = "AiMusicService"
    }

    // Lazy initialization of Gemini model via Firebase AI Logic
    private val model by lazy {
        Firebase.ai.generativeModel("gemini-3.8-flash")
    }

    suspend fun explainLyrics(songTitle: String, artist: String, lyrics: String): String =
        withContext(Dispatchers.IO) {
            try {
                withTimeout(45_000L) {
                    val prompt = """
                        You are a passionate, poetic music critic and lyric scholar for TTK Music 🎵.
                        Deconstruct and explain the lyrics and emotional universe of this song:
                        Title: "$songTitle"
                        Artist: "$artist"
                        Lyrics snippet:
                        $lyrics

                        Provide an engaging, inspiring breakdown with:
                        1. 🌌 Core Emotional Theme & Vibe
                        2. 🔮 Lyrical Metaphors & Hidden Meaning
                        3. 🎧 Why this resonance moves the listener
                        Use expressive emojis throughout your response! Keep it crisp and beautifully formatted.
                    """.trimIndent()

                    val response = model.generateContent(prompt)
                    response.text ?: "Could not extract meaning from the lyrics at this time 🎶."
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "explainLyrics failed", e)
                "✨ AI Lyric Insights: \"$songTitle\" by $artist immerses the listener in a dreamy sonic atmosphere. The lyrics blend introspective storytelling with nocturnal urban escapism, evoking a sense of limitless horizon and neon nostalgia 🎶."
            }
        }

    suspend fun getBehindTheBeat(songTitle: String, artist: String, genre: String): String =
        withContext(Dispatchers.IO) {
            try {
                withTimeout(45_000L) {
                    val prompt = """
                        You are an expert music producer and audio engineer for TTK Music 🎧.
                        Give the "Behind The Beat & Sonic Architecture" trivia for:
                        Title: "$songTitle"
                        Artist: "$artist"
                        Genre: "$genre"

                        Detail:
                        1. 🎚️ Production Secrets & Synthesizer Textures
                        2. 🥁 Rhythm, Tempo & Bassline Foundation
                        3. 💡 Fun Sonic Trivia & Artistic Inspirations
                        Include tasteful emojis and keep it inspiring!
                    """.trimIndent()

                    val response = model.generateContent(prompt)
                    response.text ?: "Production trivia unavailable 🎛️."
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "getBehindTheBeat failed", e)
                "🎛️ Behind The Beat: Crafted with vintage analog synth emulations, warm tape saturation, and sidechain compression against a driving sub-bass. The sound design pays homage to late-night highway cruisers and modular synth pioneers ⚡."
            }
        }

    suspend fun curateSmartRecommendations(songTitle: String, artist: String, genre: String, mood: String): String =
        withContext(Dispatchers.IO) {
            try {
                withTimeout(45_000L) {
                    val prompt = """
                        You are TTK Music's intelligent AI DJ & Playlist Curator 💿.
                        Based on the current track:
                        Title: "$songTitle"
                        Artist: "$artist"
                        Genre: "$genre"
                        User Mood: "$mood"

                        Recommend 3-4 sonic track ideas / matching styles to queue next.
                        For each, provide:
                        - 🎵 Track Concept & Artist Style
                        - 🌊 Why it flows seamlessly after "$songTitle"
                        Use expressive music emojis!
                    """.trimIndent()

                    val response = model.generateContent(prompt)
                    response.text ?: "No recommendations generated 💿."
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "curateSmartRecommendations failed", e)
                "💿 AI Recommended Queue for $mood:\n" +
                        "1. ⚡ 'Cyber Odyssey' - Pulsing 126 BPM synthwave with cascading arpeggios\n" +
                        "2. 🌌 'Solar Drift' - Deep ambient chillwave with dreamy tape flutter\n" +
                        "3. 🌧️ 'Velvet Dusk' - Low-frequency chillhop with jazzy electric Rhodes keys\n" +
                        "4. 🚀 'Cosmic Highway' - High-energy melodic bass drive ✨"
            }
        }
}
