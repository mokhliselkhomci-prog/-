package com.example.data.remote

import com.example.data.model.PrayerApiResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PrayerApiService {
    @GET("v1/timings/{timestamp}")
    suspend fun getTimings(
        @Path("timestamp") timestamp: Long,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("method") method: Int = 3
    ): PrayerApiResponse
}
