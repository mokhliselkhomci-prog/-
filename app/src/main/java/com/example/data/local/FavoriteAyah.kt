package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_ayahs")
data class FavoriteAyah(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val surahNumber: Int,
    val surahName: String,
    val ayahNumberInSurah: Int,
    val text: String,
    val savedAt: Long = System.currentTimeMillis()
)
