package com.example.data.remote

import com.example.data.model.SingleAyahResponse
import com.example.data.model.SurahDetailResponse
import com.example.data.model.SurahListResponse
import com.example.data.model.TafsirResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface QuranApiService {
    @GET("v1/surah")
    suspend fun getSurahList(): SurahListResponse

    @GET("v1/surah/{number}/quran-uthmani")
    suspend fun getSurahDetail(@Path("number") number: Int): SurahDetailResponse

    @GET("v1/ayah/{number}/quran-uthmani")
    suspend fun getAyahOfDay(@Path("number") number: Int): SingleAyahResponse

    @GET("v1/ayah/{number}/ar.muyassar")
    suspend fun getTafsir(@Path("number") number: Int): TafsirResponse
}
