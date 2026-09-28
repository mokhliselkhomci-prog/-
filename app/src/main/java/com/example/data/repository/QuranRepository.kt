package com.example.data.repository

import com.example.data.local.FavoriteAyah
import com.example.data.local.FavoriteDao
import com.example.data.model.SingleAyahData
import com.example.data.model.SurahDetail
import com.example.data.model.SurahSummary
import com.example.data.remote.QuranApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.Calendar

class QuranRepository(
    private val quranApi: QuranApiService,
    private val favoriteDao: FavoriteDao
) {
    private var cachedSurahs: List<SurahSummary>? = null
    private val surahDetailsCache = mutableMapOf<Int, SurahDetail>()
    private val tafsirCache = mutableMapOf<Int, String>()

    suspend fun getSurahs(): Result<List<SurahSummary>> = withContext(Dispatchers.IO) {
        try {
            cachedSurahs?.let { return@withContext Result.success(it) }
            val response = quranApi.getSurahList()
            if (response.code == 200 && response.data.isNotEmpty()) {
                cachedSurahs = response.data
                Result.success(response.data)
            } else {
                Result.failure(Exception("Failed to load surahs: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSurahDetail(surahNumber: Int): Result<SurahDetail> = withContext(Dispatchers.IO) {
        try {
            surahDetailsCache[surahNumber]?.let { return@withContext Result.success(it) }
            val response = quranApi.getSurahDetail(surahNumber)
            if (response.code == 200) {
                surahDetailsCache[surahNumber] = response.data
                Result.success(response.data)
            } else {
                Result.failure(Exception("Failed to load surah detail"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAyahOfDay(): Result<SingleAyahData> = withContext(Dispatchers.IO) {
        try {
            val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
            val ayahNumber = (dayOfYear % 6236) + 1
            val response = quranApi.getAyahOfDay(ayahNumber)
            if (response.code == 200) {
                Result.success(response.data)
            } else {
                Result.failure(Exception("Failed to fetch Ayah of the day"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTafsir(globalAyahNumber: Int): Result<String> = withContext(Dispatchers.IO) {
        try {
            tafsirCache[globalAyahNumber]?.let { return@withContext Result.success(it) }
            val response = quranApi.getTafsir(globalAyahNumber)
            if (response.code == 200) {
                val text = response.data.text
                tafsirCache[globalAyahNumber] = text
                Result.success(text)
            } else {
                Result.failure(Exception("Failed to fetch tafsir"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAllFavorites(): Flow<List<FavoriteAyah>> = favoriteDao.getAllFavorites()

    fun isFavorite(surahNumber: Int, ayahNumber: Int): Flow<Boolean> =
        favoriteDao.isFavorite(surahNumber, ayahNumber)

    suspend fun toggleFavorite(
        surahNumber: Int,
        surahName: String,
        ayahNumberInSurah: Int,
        text: String
    ) = withContext(Dispatchers.IO) {
        val current = favoriteDao.isFavorite(surahNumber, ayahNumberInSurah).first()
        if (current) {
            favoriteDao.deleteFavorite(surahNumber, ayahNumberInSurah)
        } else {
            favoriteDao.insertFavorite(
                FavoriteAyah(
                    surahNumber = surahNumber,
                    surahName = surahName,
                    ayahNumberInSurah = ayahNumberInSurah,
                    text = text
                )
            )
        }
    }

    suspend fun removeFavorite(id: Long) = withContext(Dispatchers.IO) {
        favoriteDao.deleteFavoriteById(id)
    }
}
