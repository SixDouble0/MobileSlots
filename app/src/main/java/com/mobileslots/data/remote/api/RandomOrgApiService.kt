package com.mobileslots.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RandomOrgApiService {
    // Using random.org's JSON-RPC API for true random numbers
    @GET("json-rpc/2/invoke")
    suspend fun generateRandomIntegers(
        @Query("apiKey") apiKey: String = "00000000-0000-0000-0000-000000000000", // Demo key
        @Query("n") count: Int,
        @Query("min") min: Int,
        @Query("max") max: Int,
        @Query("replacement") replacement: Boolean = true
    ): Response<Map<String, Any>>

    companion object {
        const val BASE_URL = "https://api.random.org/"
    }
}
