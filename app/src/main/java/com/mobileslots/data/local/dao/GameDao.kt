package com.mobileslots.data.local.dao

import androidx.room.*
import com.mobileslots.data.local.entity.GameEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM games WHERE gameId = :gameId")
    fun getGameById(gameId: Long): Flow<GameEntity?>

    @Query("SELECT * FROM games WHERE isActive = 1")
    fun getActiveGames(): Flow<List<GameEntity>>

    @Query("SELECT * FROM games WHERE type = :type AND isActive = 1")
    fun getGamesByType(type: String): Flow<List<GameEntity>>

    @Query("SELECT * FROM games WHERE name LIKE '%' || :searchQuery || '%' AND isActive = 1")
    fun searchGames(searchQuery: String): Flow<List<GameEntity>>

    @Query("SELECT * FROM games")
    fun getAllGames(): Flow<List<GameEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(games: List<GameEntity>)

    @Update
    suspend fun updateGame(game: GameEntity)

    @Delete
    suspend fun deleteGame(game: GameEntity)

    @Query("UPDATE games SET isActive = :isActive WHERE gameId = :gameId")
    suspend fun updateGameActive(gameId: Long, isActive: Boolean)
}
