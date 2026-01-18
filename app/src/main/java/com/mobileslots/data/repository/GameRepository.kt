package com.mobileslots.data.repository

import com.mobileslots.data.local.dao.GameDao
import com.mobileslots.data.local.entity.GameEntity
import com.mobileslots.domain.model.Game
import com.mobileslots.domain.model.GameType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GameRepository(private val gameDao: GameDao) {

    fun getActiveGames(): Flow<List<Game>> {
        return gameDao.getActiveGames().map { games ->
            games.map { it.toDomainModel() }
        }
    }

    fun getGamesByType(type: GameType): Flow<List<Game>> {
        return gameDao.getGamesByType(type.name).map { games ->
            games.map { it.toDomainModel() }
        }
    }

    fun searchGames(query: String): Flow<List<Game>> {
        return gameDao.searchGames(query).map { games ->
            games.map { it.toDomainModel() }
        }
    }

    fun getGameById(gameId: Long): Flow<Game?> {
        return gameDao.getGameById(gameId).map { it?.toDomainModel() }
    }

    suspend fun insertGames(games: List<Game>) {
        gameDao.insertGames(games.map { it.toEntity() })
    }

    suspend fun initializeGames() {
        val games = listOf(
            GameEntity(
                gameId = 1,
                name = "Classic Slots",
                type = "SLOTS",
                description = "Classic 3-reel slot machine with fruit symbols",
                minBet = 10,
                maxBet = 1000,
                isActive = true
            ),
            GameEntity(
                gameId = 2,
                name = "Lucky 7 Slots",
                type = "SLOTS",
                description = "5-reel slot machine with lucky sevens",
                minBet = 20,
                maxBet = 2000,
                isActive = true
            ),
            GameEntity(
                gameId = 3,
                name = "European Roulette",
                type = "ROULETTE",
                description = "Classic European roulette with single zero",
                minBet = 5,
                maxBet = 5000,
                isActive = true
            ),
            GameEntity(
                gameId = 4,
                name = "Blackjack",
                type = "BLACKJACK",
                description = "Classic blackjack - beat the dealer to 21!",
                minBet = 10,
                maxBet = 1000,
                isActive = true
            )
        )
        gameDao.insertGames(games)
    }

    private fun GameEntity.toDomainModel() = Game(
        gameId = gameId,
        name = name,
        type = GameType.valueOf(type),
        description = description,
        minBet = minBet,
        maxBet = maxBet,
        isActive = isActive
    )

    private fun Game.toEntity() = GameEntity(
        gameId = gameId,
        name = name,
        type = type.name,
        description = description,
        minBet = minBet,
        maxBet = maxBet,
        isActive = isActive
    )
}
