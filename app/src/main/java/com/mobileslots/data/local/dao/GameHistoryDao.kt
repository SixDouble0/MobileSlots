package com.mobileslots.data.local.dao

import androidx.room.*
import com.mobileslots.data.local.entity.GameHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameHistoryDao {
    @Query("SELECT * FROM game_history WHERE historyId = :historyId")
    fun getHistoryById(historyId: Long): Flow<GameHistoryEntity?>

    @Query("SELECT * FROM game_history WHERE userId = :userId ORDER BY playedAt DESC")
    fun getHistoryByUser(userId: Long): Flow<List<GameHistoryEntity>>

    @Query("SELECT * FROM game_history WHERE userId = :userId ORDER BY playedAt DESC LIMIT :limit")
    fun getRecentHistory(userId: Long, limit: Int): Flow<List<GameHistoryEntity>>

    @Query("SELECT * FROM game_history WHERE userId = :userId AND gameType = :gameType ORDER BY playedAt DESC")
    fun getHistoryByGameType(userId: Long, gameType: String): Flow<List<GameHistoryEntity>>

    @Query("""
        SELECT gh.* FROM game_history gh 
        INNER JOIN games g ON gh.gameId = g.gameId 
        WHERE gh.userId = :userId AND g.type = :gameType 
        ORDER BY gh.playedAt DESC
    """)
    fun getHistoryWithGameType(userId: Long, gameType: String): Flow<List<GameHistoryEntity>>

    @Query("""
        SELECT COUNT(*) FROM game_history 
        WHERE userId = :userId AND winAmount > betAmount
    """)
    suspend fun getTotalWins(userId: Long): Int

    @Query("""
        SELECT COUNT(*) FROM game_history 
        WHERE userId = :userId AND winAmount < betAmount
    """)
    suspend fun getTotalLosses(userId: Long): Int

    @Query("SELECT COUNT(*) FROM game_history WHERE userId = :userId")
    suspend fun getTotalGamesPlayed(userId: Long): Int

    @Query("SELECT MAX(winAmount - betAmount) FROM game_history WHERE userId = :userId")
    suspend fun getBiggestWin(userId: Long): Int?

    @Query("SELECT SUM(winAmount - betAmount) FROM game_history WHERE userId = :userId")
    suspend fun getTotalProfit(userId: Long): Int?

    @Query("""
        SELECT AVG(CAST((winAmount > betAmount) AS INTEGER) * 100.0) 
        FROM game_history 
        WHERE userId = :userId
    """)
    suspend fun getWinRate(userId: Long): Double?

    @Query("""
        SELECT * FROM game_history 
        WHERE userId = :userId 
        AND playedAt >= :startDate 
        ORDER BY playedAt DESC
    """)
    fun getHistorySince(userId: Long, startDate: Long): Flow<List<GameHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: GameHistoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistories(histories: List<GameHistoryEntity>)

    @Update
    suspend fun updateHistory(history: GameHistoryEntity)

    @Delete
    suspend fun deleteHistory(history: GameHistoryEntity)

    @Query("DELETE FROM game_history WHERE userId = :userId")
    suspend fun deleteUserHistory(userId: Long)
}
