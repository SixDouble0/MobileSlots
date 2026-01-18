package com.mobileslots.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LeaderboardResponse(
    @SerializedName("leaderboard")
    val leaderboard: List<LeaderboardEntry>
)

data class LeaderboardEntry(
    @SerializedName("rank")
    val rank: Int,
    @SerializedName("username")
    val username: String,
    @SerializedName("balance")
    val balance: Int,
    @SerializedName("total_wins")
    val totalWins: Int,
    @SerializedName("win_rate")
    val winRate: Double
)
