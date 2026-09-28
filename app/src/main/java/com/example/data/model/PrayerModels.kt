package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PrayerApiResponse(
    @Json(name = "code") val code: Int,
    @Json(name = "status") val status: String,
    @Json(name = "data") val data: PrayerData
)

@JsonClass(generateAdapter = true)
data class PrayerData(
    @Json(name = "timings") val timings: PrayerTimings,
    @Json(name = "date") val date: PrayerDateInfo? = null
)

@JsonClass(generateAdapter = true)
data class PrayerTimings(
    @Json(name = "Fajr") val fajr: String = "",
    @Json(name = "Sunrise") val sunrise: String = "",
    @Json(name = "Dhuhr") val dhuhr: String = "",
    @Json(name = "Asr") val asr: String = "",
    @Json(name = "Maghrib") val maghrib: String = "",
    @Json(name = "Isha") val isha: String = ""
)

@JsonClass(generateAdapter = true)
data class PrayerDateInfo(
    @Json(name = "readable") val readable: String? = null,
    @Json(name = "hijri") val hijri: HijriDateInfo? = null,
    @Json(name = "gregorian") val gregorian: GregorianDateInfo? = null
)

@JsonClass(generateAdapter = true)
data class HijriDateInfo(
    @Json(name = "day") val day: String? = null,
    @Json(name = "year") val year: String? = null,
    @Json(name = "month") val month: HijriMonth? = null
) {
    val formatted: String
        get() = "${day ?: ""} ${month?.ar ?: ""} ${year ?: ""} هـ".trim()
}

@JsonClass(generateAdapter = true)
data class HijriMonth(
    @Json(name = "number") val number: Int? = null,
    @Json(name = "ar") val ar: String? = null
)

@JsonClass(generateAdapter = true)
data class GregorianDateInfo(
    @Json(name = "day") val day: String? = null,
    @Json(name = "year") val year: String? = null,
    @Json(name = "month") val month: GregorianMonth? = null
)

@JsonClass(generateAdapter = true)
data class GregorianMonth(
    @Json(name = "en") val en: String? = null
)

data class CityPreset(
    val nameArabic: String,
    val countryArabic: String,
    val latitude: Double,
    val longitude: Double,
    val method: Int = 3 // 3 = Muslim World League, 19 = Morocco Ministry of Islamic Affairs
) {
    companion object {
        val PRESETS = listOf(
            CityPreset("الدار البيضاء", "المغرب", 33.5731, -7.5898, 19),
            CityPreset("الرباط", "المغرب", 34.0209, -6.8416, 19),
            CityPreset("طنجة", "المغرب", 35.7595, -5.8340, 19),
            CityPreset("فاس", "المغرب", 34.0181, -5.0078, 19),
            CityPreset("مراكش", "المغرب", 31.6295, -7.9811, 19),
            CityPreset("أكادير", "المغرب", 30.4278, -9.5981, 19),
            CityPreset("وجدة", "المغرب", 34.6814, -1.9086, 19),
            CityPreset("مكة المكرمة", "السعودية", 21.4225, 39.8262, 4),
            CityPreset("المدينة المنورة", "السعودية", 24.5247, 39.5692, 4),
            CityPreset("القدس الشريف", "فلسطين", 31.7683, 35.2137, 3),
            CityPreset("القاهرة", "مصر", 30.0444, 31.2357, 5),
            CityPreset("الرياض", "السعودية", 24.7136, 46.6753, 4),
            CityPreset("دبي", "الإمارات", 25.2048, 55.2708, 3),
            CityPreset("الجزائر العاصمة", "الجزائر", 36.7538, 3.0588, 3),
            CityPreset("تونس", "تونس", 36.8065, 10.1815, 3)
        )
    }
}
