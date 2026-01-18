package com.mobileslots.data.remote.api

import com.mobileslots.data.remote.dto.LeaderboardResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GameApiService {
    @GET("leaderboard")
    suspend fun getLeaderboard(
        @Query("limit") limit: Int = 10
    ): Response<LeaderboardResponse>

    // Mock API endpoint - in real app this would be actual external service
    companion object {
        const val BASE_URL = "https://api.mobileslots.com/v1/"
    }
}
