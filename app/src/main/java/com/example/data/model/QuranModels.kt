package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SurahListResponse(
    @Json(name = "code") val code: Int,
    @Json(name = "status") val status: String,
    @Json(name = "data") val data: List<SurahSummary>
)

@JsonClass(generateAdapter = true)
data class SurahSummary(
    @Json(name = "number") val number: Int,
    @Json(name = "name") val name: String,
    @Json(name = "englishName") val englishName: String,
    @Json(name = "englishNameTranslation") val englishNameTranslation: String? = null,
    @Json(name = "numberOfAyahs") val numberOfAyahs: Int,
    @Json(name = "revelationType") val revelationType: String
) {
    val isMeccan: Boolean get() = revelationType.equals("Meccan", ignoreCase = true)
}

@JsonClass(generateAdapter = true)
data class SurahDetailResponse(
    @Json(name = "code") val code: Int,
    @Json(name = "status") val status: String,
    @Json(name = "data") val data: SurahDetail
)

@JsonClass(generateAdapter = true)
data class SurahDetail(
    @Json(name = "number") val number: Int,
    @Json(name = "name") val name: String,
    @Json(name = "englishName") val englishName: String,
    @Json(name = "englishNameTranslation") val englishNameTranslation: String? = null,
    @Json(name = "numberOfAyahs") val numberOfAyahs: Int,
    @Json(name = "revelationType") val revelationType: String,
    @Json(name = "ayahs") val ayahs: List<AyahItem>
)

@JsonClass(generateAdapter = true)
data class AyahItem(
    @Json(name = "number") val number: Int, // global ayah index (1 to 6236)
    @Json(name = "text") val text: String,
    @Json(name = "numberInSurah") val numberInSurah: Int,
    @Json(name = "juz") val juz: Int? = null,
    @Json(name = "page") val page: Int? = null
)

@JsonClass(generateAdapter = true)
data class SingleAyahResponse(
    @Json(name = "code") val code: Int,
    @Json(name = "status") val status: String,
    @Json(name = "data") val data: SingleAyahData
)

@JsonClass(generateAdapter = true)
data class SingleAyahData(
    @Json(name = "number") val number: Int,
    @Json(name = "text") val text: String,
    @Json(name = "numberInSurah") val numberInSurah: Int,
    @Json(name = "surah") val surah: SurahSummary
)

@JsonClass(generateAdapter = true)
data class TafsirResponse(
    @Json(name = "code") val code: Int,
    @Json(name = "status") val status: String,
    @Json(name = "data") val data: TafsirData
)

@JsonClass(generateAdapter = true)
data class TafsirData(
    @Json(name = "number") val number: Int,
    @Json(name = "text") val text: String,
    @Json(name = "numberInSurah") val numberInSurah: Int
)

data class Reciter(
    val id: String,
    val nameArabic: String,
    val folder: String
) {
    fun getAudioUrl(surahNumber: Int, ayahNumber: Int): String {
        val s = surahNumber.toString().padStart(3, '0')
        val a = ayahNumber.toString().padStart(3, '0')
        return "https://everyayah.com/data/$folder/$s$a.mp3"
    }

    companion object {
        val DEFAULT_RECITERS = listOf(
            Reciter("minshawy", "الشيخ محمد صديق المنشاوي", "Minshawy_Murattal_128kbps"),
            Reciter("dussary", "الشيخ ياسر الدوسري", "Yasser_Ad-Dussary_128kbps"),
            Reciter("alafasy", "الشيخ مشاري راشد العفاسي", "Alafasy_128kbps"),
            Reciter("abdulbasit", "الشيخ عبد الباسط عبد الصمد", "Abdul_Basit_Murattal_192kbps"),
            Reciter("muaiqly", "الشيخ ماهر المعيقلي", "Maher_AlMuaiqly_64kbps")
        )
    }
}
