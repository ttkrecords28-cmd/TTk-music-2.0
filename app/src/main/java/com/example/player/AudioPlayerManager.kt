package com.example.player

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.example.model.DemoCatalog
import com.example.model.EqualizerPreset
import com.example.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class AudioPlayerManager(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    private var mediaPlayer: MediaPlayer? = null
    private var synthTrack: AudioTrack? = null
    private var synthJob: Job? = null
    private var progressJob: Job? = null

    private val _currentSong = MutableStateFlow<Song?>(DemoCatalog.DEFAULT_SONGS.firstOrNull())
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(DemoCatalog.DEFAULT_SONGS.firstOrNull()?.durationMs ?: 180000L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _amplitude = MutableStateFlow(0f)
    val amplitude: StateFlow<Float> = _amplitude.asStateFlow()

    private val _queue = MutableStateFlow(DemoCatalog.DEFAULT_SONGS)
    val queue: StateFlow<List<Song>> = _queue.asStateFlow()

    private val _equalizerPreset = MutableStateFlow(EqualizerPreset.ELECTRONIC)
    val equalizerPreset: StateFlow<EqualizerPreset> = _equalizerPreset.asStateFlow()

    private val _isShuffle = MutableStateFlow(false)
    val isShuffle: StateFlow<Boolean> = _isShuffle.asStateFlow()

    private val _isLooping = MutableStateFlow(true)
    val isLooping: StateFlow<Boolean> = _isLooping.asStateFlow()

    init {
        startProgressTracker()
    }

    fun setQueue(songs: List<Song>) {
        _queue.value = songs
    }

    fun playSong(song: Song) {
        _currentSong.value = song
        _currentPositionMs.value = 0L
        _durationMs.value = song.durationMs
        startPlayback(song)
    }

    fun togglePlayPause() {
        if (_isPlaying.value) {
            pause()
        } else {
            val song = _currentSong.value ?: _queue.value.firstOrNull() ?: return
            _currentSong.value = song
            startPlayback(song)
        }
    }

    fun pause() {
        _isPlaying.value = false
        mediaPlayer?.pause()
        synthJob?.cancel()
        synthJob = null
        _amplitude.value = 0f
    }

    fun seekTo(positionMs: Long) {
        val clamped = positionMs.coerceIn(0L, _durationMs.value)
        _currentPositionMs.value = clamped
        mediaPlayer?.let {
            if (it.isPlaying || _isPlaying.value) {
                it.seekTo(clamped.toInt())
            }
        }
    }

    fun playNext() {
        val current = _currentSong.value ?: return
        val currentQueue = _queue.value
        if (currentQueue.isEmpty()) return

        val nextIndex = if (_isShuffle.value) {
            currentQueue.indices.random()
        } else {
            val currentIndex = currentQueue.indexOfFirst { it.id == current.id }
            if (currentIndex >= 0 && currentIndex + 1 < currentQueue.size) {
                currentIndex + 1
            } else if (_isLooping.value) {
                0
            } else {
                pause()
                return
            }
        }
        playSong(currentQueue[nextIndex])
    }

    fun playPrevious() {
        val current = _currentSong.value ?: return
        val currentQueue = _queue.value
        if (currentQueue.isEmpty()) return

        // If played more than 3 seconds, restart current track
        if (_currentPositionMs.value > 3000L) {
            seekTo(0L)
            return
        }

        val currentIndex = currentQueue.indexOfFirst { it.id == current.id }
        val prevIndex = if (currentIndex > 0) {
            currentIndex - 1
        } else {
            currentQueue.size - 1
        }
        playSong(currentQueue[prevIndex])
    }

    fun setEqualizerPreset(preset: EqualizerPreset) {
        _equalizerPreset.value = preset
    }

    fun toggleShuffle() {
        _isShuffle.value = !_isShuffle.value
    }

    fun toggleLooping() {
        _isLooping.value = !_isLooping.value
    }

    private fun startPlayback(song: Song) {
        // Stop any current playback
        stopPlayers()

        if (song.uriString != null) {
            // Play user-imported file via Android MediaPlayer
            playUri(song.uriString)
        } else {
            // Procedurally synthesize music chords & rhythm for built-in demo track
            playProceduralSynth(song)
        }
        _isPlaying.value = true
    }

    private fun playUri(uriString: String) {
        try {
            val uri = Uri.parse(uriString)
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(context, uri)
                prepare()
                _durationMs.value = duration.toLong()
                seekTo(_currentPositionMs.value.toInt())
                start()
                setOnCompletionListener {
                    playNext()
                }
            }
        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "Failed to play URI: $uriString", e)
            // Fallback to synth
            _currentSong.value?.let { playProceduralSynth(it) }
        }
    }

    private fun playProceduralSynth(song: Song) {
        val sampleRate = 22050
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ).coerceAtLeast(4096)

        try {
            synthTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            synthTrack?.play()
        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "Failed to init AudioTrack", e)
            return
        }

        // Chords based on genre/bpm
        val chords = when {
            song.genre.contains("Lo-Fi", ignoreCase = true) -> listOf(
                doubleArrayOf(261.63, 311.13, 392.00, 466.16), // Cm7
                doubleArrayOf(220.00, 261.63, 329.63, 392.00), // Am7
                doubleArrayOf(174.61, 220.00, 261.63, 329.63), // Fmaj7
                doubleArrayOf(196.00, 246.94, 293.66, 349.23)  // G7
            )
            song.genre.contains("Space", ignoreCase = true) -> listOf(
                doubleArrayOf(146.83, 220.00, 293.66, 440.00), // Dm open
                doubleArrayOf(130.81, 196.00, 261.63, 392.00), // C open
                doubleArrayOf(116.54, 174.61, 233.08, 349.23), // Bb open
                doubleArrayOf(146.83, 220.00, 293.66, 370.00)  // Dmadd9
            )
            else -> listOf( // Synthwave
                doubleArrayOf(220.00, 277.18, 329.63, 440.00), // A maj
                doubleArrayOf(174.61, 220.00, 261.63, 349.23), // F maj
                doubleArrayOf(130.81, 164.81, 196.00, 261.63), // C maj
                doubleArrayOf(196.00, 246.94, 293.66, 392.00)  // G maj
            )
        }

        synthJob = scope.launch(Dispatchers.Default) {
            val shortBuffer = ShortArray(1024)
            var sampleIndex = 0L

            while (isActive && _isPlaying.value) {
                val currentPreset = _equalizerPreset.value
                val bassBoost = currentPreset.bassFactor
                val trebleBoost = currentPreset.trebleFactor

                val chordDurationSamples = (sampleRate * 2.0).toLong() // 2 sec per chord
                val chordIndex = ((sampleIndex / chordDurationSamples) % chords.size).toInt()
                val currentNotes = chords[chordIndex]
                val subBassFreq = currentNotes[0] * 0.5

                var bufferEnergy = 0.0

                for (i in shortBuffer.indices) {
                    val t = sampleIndex.toDouble() / sampleRate
                    // Harmonic chord wave
                    var wave = 0.0
                    for (note in currentNotes) {
                        wave += sin(2.0 * PI * note * t) * 0.2
                    }
                    // Sub bass kick / pulse
                    val beatTime = (t * (song.bpm / 60.0)) % 1.0
                    val kickEnvelope = (1.0 - beatTime).coerceIn(0.0, 1.0)
                    wave += sin(2.0 * PI * subBassFreq * t) * kickEnvelope * 0.45 * bassBoost

                    // Arpeggio chime
                    val arpIndex = ((t * 8.0).toInt()) % currentNotes.size
                    val arpFreq = currentNotes[arpIndex] * 2.0
                    wave += sin(2.0 * PI * arpFreq * t) * 0.15 * trebleBoost

                    // Master volume & soft clip
                    val sample = (wave * 0.35 * 32767.0).coerceIn(-32767.0, 32767.0).toInt().toShort()
                    shortBuffer[i] = sample
                    bufferEnergy += Math.abs(sample.toDouble())
                    sampleIndex++
                }

                synthTrack?.write(shortBuffer, 0, shortBuffer.size)

                // Update real-time audio amplitude for glowing orbs / equalizer visualizer
                val normAmp = (bufferEnergy / (shortBuffer.size * 16384.0)).toFloat().coerceIn(0.05f, 1f)
                _amplitude.value = normAmp
            }
        }
    }

    private fun startProgressTracker() {
        progressJob = scope.launch {
            while (isActive) {
                if (_isPlaying.value) {
                    mediaPlayer?.let { player ->
                        try {
                            if (player.isPlaying) {
                                _currentPositionMs.value = player.currentPosition.toLong()
                            }
                        } catch (e: Exception) {
                            Log.w("AudioPlayerManager", "Progress read error", e)
                        }
                    } ?: run {
                        // For synth mode, advance by 100ms
                        val newPos = _currentPositionMs.value + 100L
                        if (newPos >= _durationMs.value) {
                            playNext()
                        } else {
                            _currentPositionMs.value = newPos
                        }
                    }
                }
                delay(100L)
            }
        }
    }

    private fun stopPlayers() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null

        synthJob?.cancel()
        synthJob = null

        try {
            synthTrack?.stop()
            synthTrack?.release()
        } catch (_: Exception) {}
        synthTrack = null
    }

    fun release() {
        stopPlayers()
        progressJob?.cancel()
    }
}
