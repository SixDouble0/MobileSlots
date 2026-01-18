package com.mobileslots.data.repository

import com.mobileslots.data.local.dao.GameHistoryDao
import com.mobileslots.data.local.entity.GameHistoryEntity
import com.mobileslots.domain.model.GameHistory
import com.mobileslots.domain.model.GameType
import com.mobileslots.domain.model.UserStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date

class GameHistoryRepository(private val gameHistoryDao: GameHistoryDao) {

    fun getHistoryByUser(userId: Long): Flow<List<GameHistory>> {
        return gameHistoryDao.getHistoryByUser(userId).map { histories ->
            histories.map { it.toDomainModel() }
        }
    }

    fun getRecentHistory(userId: Long, limit: Int): Flow<List<GameHistory>> {
        return gameHistoryDao.getRecentHistory(userId, limit).map { histories ->
            histories.map { it.toDomainModel() }
        }
    }

    fun getHistoryByGameType(userId: Long, gameType: GameType): Flow<List<GameHistory>> {
        return gameHistoryDao.getHistoryByGameType(userId, gameType.name).map { histories ->
            histories.map { it.toDomainModel() }
        }
    }

    suspend fun addGameHistory(history: GameHistory): Long {
        return gameHistoryDao.insertHistory(history.toEntity())
    }

    suspend fun getUserStats(userId: Long): UserStats {
        val totalGamesPlayed = gameHistoryDao.getTotalGamesPlayed(userId)
        val totalWins = gameHistoryDao.getTotalWins(userId)
        val totalLosses = gameHistoryDao.getTotalLosses(userId)
        val winRate = gameHistoryDao.getWinRate(userId) ?: 0.0
        val biggestWin = gameHistoryDao.getBiggestWin(userId) ?: 0
        val totalProfit = gameHistoryDao.getTotalProfit(userId) ?: 0

        return UserStats(
            totalGamesPlayed = totalGamesPlayed,
            totalWins = totalWins,
            totalLosses = totalLosses,
            winRate = winRate,
            biggestWin = biggestWin,
            totalProfit = totalProfit
        )
    }

    private fun GameHistoryEntity.toDomainModel() = GameHistory(
        historyId = historyId,
        userId = userId,
        gameId = gameId,
        gameType = GameType.valueOf(gameType),
        betAmount = betAmount,
        winAmount = winAmount,
        balanceAfter = balanceAfter,
        playedAt = playedAt,
        gameDetails = gameDetails
    )

    private fun GameHistory.toEntity() = GameHistoryEntity(
        historyId = historyId,
        userId = userId,
        gameId = gameId,
        gameType = gameType.name,
        betAmount = betAmount,
        winAmount = winAmount,
        balanceAfter = balanceAfter,
        playedAt = playedAt,
        gameDetails = gameDetails
    )
}
