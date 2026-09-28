package com.example.data.repository

import com.example.data.model.HadithCollection
import com.example.data.model.HadithItem
import com.example.data.remote.HadithApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import kotlin.random.Random

class HadithRepository(
    private val hadithApi: HadithApiService
) {
    private val fallbackHadiths = listOf(
        HadithItem(
            hadithnumber = 1.0,
            text = "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ، وَإِنَّمَا لِكُلِّ امْرِئٍ مَا نَوَى، فَمَنْ كَانَتْ هِجْرَتُهُ إِلَى دُنْيَا يُصِيبُهَا أَوْ إِلَى امْرَأَةٍ يَنْكِحُهَا فَهِجْرَتُهُ إِلَى مَا هَاجَرَ إِلَيْهِ."
        ),
        HadithItem(
            hadithnumber = 13.0,
            text = "لاَ يُؤْمِنُ أَحَدُكُمْ حَتَّى يُحِبَّ لأَخِيهِ مَا يُحِبُّ لِنَفْسِهِ."
        ),
        HadithItem(
            hadithnumber = 5027.0,
            text = "خَيْرُكُمْ مَنْ تَعَلَّمَ القُرْآنَ وَعَلَّمَهُ."
        ),
        HadithItem(
            hadithnumber = 6011.0,
            text = "مَنْ كَانَ يُؤْمِنُ بِاللَّهِ وَاليَوْمِ الآخِرِ فَلْيَقُلْ خَيْرًا أَوْ لِيَصْمُتْ، وَمَنْ كَانَ يُؤْمِنُ بِاللَّهِ وَاليَوْمِ الآخِرِ فَلْيُكْرِمْ جَارَهُ، وَمَنْ كَانَ يُؤْمِنُ بِاللَّهِ وَاليَوْمِ الآخِرِ فَلْيُكْرِمْ ضَيْفَهُ."
        ),
        HadithItem(
            hadithnumber = 6044.0,
            text = "تَبَسُّمُكَ فِي وَجْهِ أَخِيكَ لَكَ صَدَقَةٌ."
        ),
        HadithItem(
            hadithnumber = 2699.0,
            text = "مَنْ سَلَكَ طَرِيقًا يَلْتَمِسُ فِيهِ عِلْمًا سَهَّلَ اللَّهُ لَهُ بِهِ طَرِيقًا إِلَى الجَنَّةِ."
        ),
        HadithItem(
            hadithnumber = 2564.0,
            text = "اتَّقِ اللَّهَ حَيْثُمَا كُنْتَ، وَأَتْبِعِ السَّيِّئَةَ الحَسَنَةَ تَمْحُهَا، وَخَالِقِ النَّاسَ بِخُلُقٍ حَسَنٍ."
        )
    )

    suspend fun getHadithOfDay(): Result<Pair<HadithItem, String>> = withContext(Dispatchers.IO) {
        try {
            val day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
            val number = (day % 1500) + 1
            val response = hadithApi.getHadith(HadithCollection.BUKHARI.id, number)
            val item = response.hadiths.firstOrNull() ?: fallbackHadiths[day % fallbackHadiths.size]
            Result.success(Pair(item, HadithCollection.BUKHARI.titleArabic))
        } catch (e: Exception) {
            val idx = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) % fallbackHadiths.size
            Result.success(Pair(fallbackHadiths[idx], HadithCollection.BUKHARI.titleArabic))
        }
    }

    suspend fun getRandomHadith(collection: HadithCollection): Result<HadithItem> = withContext(Dispatchers.IO) {
        try {
            val max = when (collection) {
                HadithCollection.BUKHARI -> 2500
                HadithCollection.MUSLIM -> 2000
                HadithCollection.NAWAWI -> 42
            }
            val num = Random.nextInt(1, max + 1)
            val response = hadithApi.getHadith(collection.id, num)
            val item = response.hadiths.firstOrNull()
            if (item != null && item.text.isNotBlank()) {
                Result.success(item)
            } else {
                Result.success(fallbackHadiths.random())
            }
        } catch (e: Exception) {
            Result.success(fallbackHadiths.random())
        }
    }

    suspend fun getSpecificHadith(collection: HadithCollection, number: Int): Result<HadithItem> = withContext(Dispatchers.IO) {
        try {
            val response = hadithApi.getHadith(collection.id, number)
            val item = response.hadiths.firstOrNull()
            if (item != null) {
                Result.success(item)
            } else {
                Result.failure(Exception("Hadith not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
