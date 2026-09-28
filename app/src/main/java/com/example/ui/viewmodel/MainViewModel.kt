package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioPlayerManager
import com.example.data.local.AppDatabase
import com.example.data.local.FavoriteAyah
import com.example.data.model.AzkarCategory
import com.example.data.model.AzkarDataProvider
import com.example.data.model.CityPreset
import com.example.data.model.HadithCollection
import com.example.data.model.HadithItem
import com.example.data.model.PrayerData
import com.example.data.model.Reciter
import com.example.data.model.SingleAyahData
import com.example.data.model.SurahDetail
import com.example.data.model.SurahSummary
import com.example.data.remote.NetworkModule
import com.example.data.repository.HadithRepository
import com.example.data.repository.NextPrayerInfo
import com.example.data.repository.PrayerRepository
import com.example.data.repository.QuranRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Screen {
    HOME,
    QURAN,
    SURAH_READER,
    HADITH,
    AZKAR,
    TASBIH,
    PRAYER,
    FAVORITES,
    SUPPORT
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    val quranRepo = QuranRepository(NetworkModule.quranApi, database.favoriteDao())
    val hadithRepo = HadithRepository(NetworkModule.hadithApi)
    val prayerRepo = PrayerRepository(NetworkModule.prayerApi)
    val audioPlayer = AudioPlayerManager(application)

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    // Navigation State
    private val _currentScreen = MutableStateFlow(Screen.HOME)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val navigationBackStack = mutableListOf<Screen>()

    // Quran State
    private val _surahList = MutableStateFlow<List<SurahSummary>>(emptyList())
    val surahList: StateFlow<List<SurahSummary>> = _surahList.asStateFlow()

    private val _isSurahListLoading = MutableStateFlow(false)
    val isSurahListLoading: StateFlow<Boolean> = _isSurahListLoading.asStateFlow()

    private val _surahSearchQuery = MutableStateFlow("")
    val surahSearchQuery: StateFlow<String> = _surahSearchQuery.asStateFlow()

    private val _selectedSurah = MutableStateFlow<SurahDetail?>(null)
    val selectedSurah: StateFlow<SurahDetail?> = _selectedSurah.asStateFlow()

    private val _isSurahDetailLoading = MutableStateFlow(false)
    val isSurahDetailLoading: StateFlow<Boolean> = _isSurahDetailLoading.asStateFlow()

    private val _ayahFontSize = MutableStateFlow(24f)
    val ayahFontSize: StateFlow<Float> = _ayahFontSize.asStateFlow()

    // Tafsir map: globalAyahNumber -> Tafsir text
    private val _tafsirMap = MutableStateFlow<Map<Int, String>>(emptyMap())
    val tafsirMap: StateFlow<Map<Int, String>> = _tafsirMap.asStateFlow()

    private val _tafsirLoadingMap = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val tafsirLoadingMap: StateFlow<Map<Int, Boolean>> = _tafsirLoadingMap.asStateFlow()

    // Home Screen Data
    private val _ayahOfDay = MutableStateFlow<SingleAyahData?>(null)
    val ayahOfDay: StateFlow<SingleAyahData?> = _ayahOfDay.asStateFlow()

    private val _hadithOfDay = MutableStateFlow<Pair<HadithItem, String>?>(null)
    val hadithOfDay: StateFlow<Pair<HadithItem, String>?> = _hadithOfDay.asStateFlow()

    // Hadith Screen Data
    private val _selectedHadithCollection = MutableStateFlow(HadithCollection.BUKHARI)
    val selectedHadithCollection: StateFlow<HadithCollection> = _selectedHadithCollection.asStateFlow()

    private val _currentHadith = MutableStateFlow<HadithItem?>(null)
    val currentHadith: StateFlow<HadithItem?> = _currentHadith.asStateFlow()

    private val _isHadithLoading = MutableStateFlow(false)
    val isHadithLoading: StateFlow<Boolean> = _isHadithLoading.asStateFlow()

    // Azkar State
    private val _selectedAzkarCategory = MutableStateFlow(AzkarCategory.MORNING)
    val selectedAzkarCategory: StateFlow<AzkarCategory> = _selectedAzkarCategory.asStateFlow()

    private val _azkarCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val azkarCounts: StateFlow<Map<String, Int>> = _azkarCounts.asStateFlow()

    // Tasbih State
    private val _tasbihCount = MutableStateFlow(0)
    val tasbihCount: StateFlow<Int> = _tasbihCount.asStateFlow()

    private val _tasbihTarget = MutableStateFlow(33)
    val tasbihTarget: StateFlow<Int> = _tasbihTarget.asStateFlow()

    private val _tasbihZikr = MutableStateFlow("سُبْحَانَ اللَّهِ")
    val tasbihZikr: StateFlow<String> = _tasbihZikr.asStateFlow()

    // Prayer Times State
    private val _selectedCity = MutableStateFlow(CityPreset.PRESETS.first())
    val selectedCity: StateFlow<CityPreset> = _selectedCity.asStateFlow()

    private val _prayerData = MutableStateFlow<PrayerData?>(null)
    val prayerData: StateFlow<PrayerData?> = _prayerData.asStateFlow()

    private val _nextPrayer = MutableStateFlow<NextPrayerInfo?>(null)
    val nextPrayer: StateFlow<NextPrayerInfo?> = _nextPrayer.asStateFlow()

    private val _isPrayerLoading = MutableStateFlow(false)
    val isPrayerLoading: StateFlow<Boolean> = _isPrayerLoading.asStateFlow()

    // Favorites
    val favorites: StateFlow<List<FavoriteAyah>> = quranRepo.getAllFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        loadSurahList()
        loadAyahOfDay()
        loadHadithOfDay()
        loadPrayerTimes(selectedCity.value)
        loadRandomHadith(selectedHadithCollection.value)
    }

    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            navigationBackStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        return if (navigationBackStack.isNotEmpty()) {
            val prev = navigationBackStack.removeAt(navigationBackStack.size - 1)
            _currentScreen.value = prev
            true
        } else {
            false
        }
    }

    fun setSurahSearchQuery(query: String) {
        _surahSearchQuery.value = query
    }

    fun loadSurahList() {
        viewModelScope.launch {
            _isSurahListLoading.value = true
            quranRepo.getSurahs()
                .onSuccess { list ->
                    _surahList.value = list
                    _isSurahListLoading.value = false
                }
                .onFailure {
                    _isSurahListLoading.value = false
                }
        }
    }

    fun openSurah(surahNumber: Int) {
        viewModelScope.launch {
            _isSurahDetailLoading.value = true
            navigateTo(Screen.SURAH_READER)
            quranRepo.getSurahDetail(surahNumber)
                .onSuccess { detail ->
                    _selectedSurah.value = detail
                    _isSurahDetailLoading.value = false
                }
                .onFailure {
                    _isSurahDetailLoading.value = false
                }
        }
    }

    fun toggleTafsir(globalAyahNumber: Int) {
        val current = _tafsirMap.value
        if (current.containsKey(globalAyahNumber)) {
            _tafsirMap.value = current - globalAyahNumber
        } else {
            viewModelScope.launch {
                _tafsirLoadingMap.value = _tafsirLoadingMap.value + (globalAyahNumber to true)
                quranRepo.getTafsir(globalAyahNumber)
                    .onSuccess { text ->
                        _tafsirMap.value = _tafsirMap.value + (globalAyahNumber to text)
                        _tafsirLoadingMap.value = _tafsirLoadingMap.value - globalAyahNumber
                    }
                    .onFailure {
                        _tafsirLoadingMap.value = _tafsirLoadingMap.value - globalAyahNumber
                    }
            }
        }
    }

    fun toggleFavorite(surahNumber: Int, surahName: String, ayahNumber: Int, text: String) {
        viewModelScope.launch {
            quranRepo.toggleFavorite(surahNumber, surahName, ayahNumber, text)
            triggerVibration(30)
        }
    }

    fun removeFavorite(id: Long) {
        viewModelScope.launch {
            quranRepo.removeFavorite(id)
        }
    }

    fun setFontSize(size: Float) {
        _ayahFontSize.value = size.coerceIn(18f, 36f)
    }

    // Audio Playback
    fun playAyahAudio(surahNumber: Int, ayahNumber: Int, totalAyahs: Int) {
        audioPlayer.playAyah(surahNumber, ayahNumber, totalAyahs)
    }

    fun setReciter(reciter: Reciter) {
        audioPlayer.setReciter(reciter)
    }

    // Home ayahs & hadith
    fun loadAyahOfDay() {
        viewModelScope.launch {
            quranRepo.getAyahOfDay().onSuccess {
                _ayahOfDay.value = it
            }
        }
    }

    fun loadHadithOfDay() {
        viewModelScope.launch {
            hadithRepo.getHadithOfDay().onSuccess {
                _hadithOfDay.value = it
            }
        }
    }

    // Hadiths
    fun selectHadithCollection(collection: HadithCollection) {
        _selectedHadithCollection.value = collection
        loadRandomHadith(collection)
    }

    fun loadRandomHadith(collection: HadithCollection = _selectedHadithCollection.value) {
        viewModelScope.launch {
            _isHadithLoading.value = true
            hadithRepo.getRandomHadith(collection)
                .onSuccess {
                    _currentHadith.value = it
                    _isHadithLoading.value = false
                }
                .onFailure {
                    _isHadithLoading.value = false
                }
        }
    }

    // Azkar
    fun selectAzkarCategory(cat: AzkarCategory) {
        _selectedAzkarCategory.value = cat
    }

    fun incrementZikrCount(id: String, target: Int) {
        val current = _azkarCounts.value[id] ?: 0
        val next = current + 1
        _azkarCounts.value = _azkarCounts.value + (id to next)

        if (next == target) {
            triggerVibration(100)
        } else {
            triggerVibration(25)
        }
    }

    fun resetAzkarCountsForCategory(category: AzkarCategory) {
        val items = AzkarDataProvider.getAzkar(category)
        val mutable = _azkarCounts.value.toMutableMap()
        items.forEach { mutable.remove(it.id) }
        _azkarCounts.value = mutable
    }

    // Tasbih
    fun tapTasbih() {
        val current = _tasbihCount.value
        val next = current + 1
        _tasbihCount.value = next

        val target = _tasbihTarget.value
        if (target > 0 && next % target == 0) {
            triggerVibration(120)
        } else {
            triggerVibration(20)
        }
    }

    fun resetTasbih() {
        _tasbihCount.value = 0
        triggerVibration(50)
    }

    fun setTasbihTarget(target: Int) {
        _tasbihTarget.value = target
    }

    fun setTasbihZikr(zikr: String) {
        _tasbihZikr.value = zikr
    }

    // Prayer times
    fun selectCity(city: CityPreset) {
        _selectedCity.value = city
        loadPrayerTimes(city)
    }

    fun loadPrayerTimes(city: CityPreset) {
        viewModelScope.launch {
            _isPrayerLoading.value = true
            prayerRepo.getPrayerTimesForCity(city)
                .onSuccess { data ->
                    _prayerData.value = data
                    _nextPrayer.value = prayerRepo.calculateNextPrayer(data.timings)
                    _isPrayerLoading.value = false
                }
                .onFailure {
                    _isPrayerLoading.value = false
                }
        }
    }

    fun setLocationCoords(lat: Double, lng: Double) {
        viewModelScope.launch {
            _isPrayerLoading.value = true
            prayerRepo.getPrayerTimes(lat, lng)
                .onSuccess { data ->
                    _prayerData.value = data
                    _nextPrayer.value = prayerRepo.calculateNextPrayer(data.timings)
                    _isPrayerLoading.value = false
                }
                .onFailure {
                    _isPrayerLoading.value = false
                }
        }
    }

    private fun triggerVibration(durationMs: Long) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.stop()
    }
}
