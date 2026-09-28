package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HadithDetailResponse(
    @Json(name = "hadiths") val hadiths: List<HadithItem>
)

@JsonClass(generateAdapter = true)
data class HadithItem(
    @Json(name = "hadithnumber") val hadithnumber: Double? = null,
    @Json(name = "arabicnumber") val arabicnumber: Double? = null,
    @Json(name = "text") val text: String = ""
) {
    val numberFormatted: String
        get() = (hadithnumber?.toInt() ?: arabicnumber?.toInt() ?: 1).toString()
}

enum class HadithCollection(
    val id: String,
    val titleArabic: String,
    val maxHadiths: Int
) {
    BUKHARI("ara-bukhari", "صحيح البخاري", 7563),
    MUSLIM("ara-muslim", "صحيح مسلم", 3033),
    NAWAWI("ara-nawawi", "الأربعون النووية", 42)
}
