package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import com.example.data.model.Reciter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AudioPlayerManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentSurahNumber = MutableStateFlow<Int?>(null)
    val currentSurahNumber: StateFlow<Int?> = _currentSurahNumber.asStateFlow()

    private val _currentAyahNumber = MutableStateFlow<Int?>(null)
    val currentAyahNumber: StateFlow<Int?> = _currentAyahNumber.asStateFlow()

    private val _currentReciter = MutableStateFlow(Reciter.DEFAULT_RECITERS.first())
    val currentReciter: StateFlow<Reciter> = _currentReciter.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isContinuousPlay = MutableStateFlow(false)
    val isContinuousPlay: StateFlow<Boolean> = _isContinuousPlay.asStateFlow()

    private var totalAyahsInCurrentSurah: Int = 0

    fun setReciter(reciter: Reciter) {
        _currentReciter.value = reciter
        // If playing, restart with new reciter
        val s = _currentSurahNumber.value
        val a = _currentAyahNumber.value
        if (s != null && a != null && _isPlaying.value) {
            playAyah(s, a, totalAyahsInCurrentSurah, reciter, _isContinuousPlay.value)
        }
    }

    fun toggleContinuousPlay() {
        _isContinuousPlay.value = !_isContinuousPlay.value
    }

    fun playAyah(
        surahNumber: Int,
        ayahNumber: Int,
        totalAyahs: Int = 0,
        reciter: Reciter = _currentReciter.value,
        continuous: Boolean = _isContinuousPlay.value
    ) {
        totalAyahsInCurrentSurah = totalAyahs
        _isContinuousPlay.value = continuous

        // If clicking the currently playing ayah, toggle pause/play
        if (_currentSurahNumber.value == surahNumber && _currentAyahNumber.value == ayahNumber && mediaPlayer != null) {
            if (_isPlaying.value) {
                mediaPlayer?.pause()
                _isPlaying.value = false
            } else {
                mediaPlayer?.start()
                _isPlaying.value = true
            }
            return
        }

        releasePlayer()

        _currentSurahNumber.value = surahNumber
        _currentAyahNumber.value = ayahNumber
        _currentReciter.value = reciter
        _isLoading.value = true

        val url = reciter.getAudioUrl(surahNumber, ayahNumber)

        try {
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                setOnPreparedListener { mp ->
                    _isLoading.value = false
                    mp.start()
                    _isPlaying.value = true
                }
                setOnCompletionListener {
                    _isPlaying.value = false
                    if (_isContinuousPlay.value && ayahNumber < totalAyahsInCurrentSurah) {
                        playAyah(surahNumber, ayahNumber + 1, totalAyahsInCurrentSurah, reciter, true)
                    }
                }
                setOnErrorListener { _, _, _ ->
                    _isLoading.value = false
                    _isPlaying.value = false
                    true
                }
                prepareAsync()
            }
            mediaPlayer = player
        } catch (e: Exception) {
            _isLoading.value = false
            _isPlaying.value = false
        }
    }

    fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _isPlaying.value = false
            }
        }
    }

    fun resume() {
        mediaPlayer?.let {
            it.start()
            _isPlaying.value = true
        }
    }

    fun stop() {
        releasePlayer()
        _currentSurahNumber.value = null
        _currentAyahNumber.value = null
        _isPlaying.value = false
        _isLoading.value = false
    }

    fun playNext() {
        val s = _currentSurahNumber.value ?: return
        val a = _currentAyahNumber.value ?: return
        if (a < totalAyahsInCurrentSurah) {
            playAyah(s, a + 1, totalAyahsInCurrentSurah, _currentReciter.value, _isContinuousPlay.value)
        }
    }

    fun playPrevious() {
        val s = _currentSurahNumber.value ?: return
        val a = _currentAyahNumber.value ?: return
        if (a > 1) {
            playAyah(s, a - 1, totalAyahsInCurrentSurah, _currentReciter.value, _isContinuousPlay.value)
        }
    }

    private fun releasePlayer() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {
        }
        mediaPlayer = null
    }
}
