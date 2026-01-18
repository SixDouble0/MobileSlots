package com.mobileslots.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey(autoGenerate = true)
    val gameId: Long = 0,
    val name: String,
    val type: String, // "SLOTS", "ROULETTE", "BLACKJACK"
    val description: String,
    val minBet: Int,
    val maxBet: Int,
    val isActive: Boolean = true
)
