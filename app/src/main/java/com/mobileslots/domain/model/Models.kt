package com.mobileslots.domain.model

import java.util.Date

data class User(
    val userId: Long = 0,
    val username: String,
    val balance: Int,
    val createdAt: Date,
    val lastPlayedAt: Date
)

data class UserSettings(
    val userId: Long,
    val soundEnabled: Boolean,
    val vibrationEnabled: Boolean,
    val shakeToSpinEnabled: Boolean,
    val language: String
)

data class Game(
    val gameId: Long = 0,
    val name: String,
    val type: GameType,
    val description: String,
    val minBet: Int,
    val maxBet: Int,
    val isActive: Boolean
)

enum class GameType {
    SLOTS, ROULETTE, BLACKJACK
}

data class GameHistory(
    val historyId: Long = 0,
    val userId: Long,
    val gameId: Long,
    val gameType: GameType,
    val betAmount: Int,
    val winAmount: Int,
    val balanceAfter: Int,
    val playedAt: Date,
    val gameDetails: String? = null
) {
    val isWin: Boolean = winAmount > betAmount
    val profit: Int = winAmount - betAmount
}

data class Achievement(
    val achievementId: Long = 0,
    val name: String,
    val description: String,
    val iconName: String,
    val requiredValue: Int,
    val category: AchievementCategory
)

enum class AchievementCategory {
    WINS, GAMES_PLAYED, SPECIAL
}

data class UserAchievement(
    val userId: Long,
    val achievement: Achievement,
    val unlockedAt: Date,
    val progress: Int
)

data class UserStats(
    val totalGamesPlayed: Int,
    val totalWins: Int,
    val totalLosses: Int,
    val winRate: Double,
    val biggestWin: Int,
    val totalProfit: Int
)
