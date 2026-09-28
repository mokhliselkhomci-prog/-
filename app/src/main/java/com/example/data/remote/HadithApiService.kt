package com.example.data.remote

import com.example.data.model.HadithDetailResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface HadithApiService {
    @GET("editions/{book}/{number}.json")
    suspend fun getHadith(
        @Path("book") book: String,
        @Path("number") number: Int
    ): HadithDetailResponse
}
