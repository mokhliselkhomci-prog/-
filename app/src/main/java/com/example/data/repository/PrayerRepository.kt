package com.example.data.repository

import com.example.data.model.CityPreset
import com.example.data.model.PrayerData
import com.example.data.model.PrayerTimings
import com.example.data.remote.PrayerApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class NextPrayerInfo(
    val prayerNameArabic: String,
    val prayerTime: String,
    val minutesRemaining: Long
)

class PrayerRepository(
    private val prayerApi: PrayerApiService
) {
    private var lastPrayerData: PrayerData? = null

    suspend fun getPrayerTimes(
        latitude: Double,
        longitude: Double,
        method: Int = 19
    ): Result<PrayerData> = withContext(Dispatchers.IO) {
        try {
            val ts = System.currentTimeMillis() / 1000
            val response = prayerApi.getTimings(ts, latitude, longitude, method)
            if (response.code == 200) {
                lastPrayerData = response.data
                Result.success(response.data)
            } else {
                Result.failure(Exception("Failed to get prayer timings"))
            }
        } catch (e: Exception) {
            lastPrayerData?.let { return@withContext Result.success(it) }
            // Fallback default Moroccan timings if offline
            val fallback = PrayerData(
                timings = PrayerTimings(
                    fajr = "05:35",
                    sunrise = "07:05",
                    dhuhr = "13:25",
                    asr = "16:48",
                    maghrib = "19:42",
                    isha = "21:02"
                )
            )
            Result.success(fallback)
        }
    }

    suspend fun getPrayerTimesForCity(city: CityPreset): Result<PrayerData> {
        return getPrayerTimes(city.latitude, city.longitude, city.method)
    }

    fun calculateNextPrayer(timings: PrayerTimings): NextPrayerInfo {
        val now = Calendar.getInstance()
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        val prayerList = listOf(
            "الفجر" to timings.fajr,
            "الشروق" to timings.sunrise,
            "الظهر" to timings.dhuhr,
            "العصر" to timings.asr,
            "المغرب" to timings.maghrib,
            "العشاء" to timings.isha
        )

        val currentHour = now.get(Calendar.HOUR_OF_DAY)
        val currentMinute = now.get(Calendar.MINUTE)
        val currentTotalMinutes = currentHour * 60 + currentMinute

        for ((name, timeStr) in prayerList) {
            val cleanTime = timeStr.trim().split(" ").firstOrNull() ?: continue
            val parts = cleanTime.split(":")
            if (parts.size >= 2) {
                val h = parts[0].toIntOrNull() ?: 0
                val m = parts[1].toIntOrNull() ?: 0
                val prayerMinutes = h * 60 + m
                if (prayerMinutes > currentTotalMinutes) {
                    return NextPrayerInfo(
                        prayerNameArabic = name,
                        prayerTime = cleanTime,
                        minutesRemaining = (prayerMinutes - currentTotalMinutes).toLong()
                    )
                }
            }
        }

        // If after Isha, next prayer is Fajr tomorrow
        val fajrClean = timings.fajr.trim().split(" ").firstOrNull() ?: "05:30"
        val fajrParts = fajrClean.split(":")
        val h = fajrParts.getOrNull(0)?.toIntOrNull() ?: 5
        val m = fajrParts.getOrNull(1)?.toIntOrNull() ?: 30
        val fajrMinutes = (24 * 60 - currentTotalMinutes) + (h * 60 + m)

        return NextPrayerInfo(
            prayerNameArabic = "الفجر",
            prayerTime = fajrClean,
            minutesRemaining = fajrMinutes.toLong()
        )
    }
}
