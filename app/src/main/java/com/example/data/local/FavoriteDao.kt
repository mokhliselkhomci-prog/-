package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite_ayahs ORDER BY savedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteAyah>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_ayahs WHERE surahNumber = :surahNumber AND ayahNumberInSurah = :ayahNumber)")
    fun isFavorite(surahNumber: Int, ayahNumber: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteAyah)

    @Query("DELETE FROM favorite_ayahs WHERE surahNumber = :surahNumber AND ayahNumberInSurah = :ayahNumber")
    suspend fun deleteFavorite(surahNumber: Int, ayahNumber: Int)

    @Query("DELETE FROM favorite_ayahs WHERE id = :id")
    suspend fun deleteFavoriteById(id: Long)
}
