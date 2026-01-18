package com.mobileslots.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Ignore
import java.util.Date

@Entity(
    tableName = "game_history",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = GameEntity::class,
            parentColumns = ["gameId"],
            childColumns = ["gameId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["gameId"]),
        Index(value = ["playedAt"])
    ]
)
data class GameHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val historyId: Long = 0,
    val userId: Long,
    val gameId: Long,
    val gameType: String, // "SLOTS", "ROULETTE", "BLACKJACK"
    val betAmount: Int,
    val winAmount: Int,
    val balanceAfter: Int,
    val playedAt: Date = Date(),
    val gameDetails: String? = null // JSON string with game-specific details
) {
    @Ignore
    val isWin: Boolean = winAmount > betAmount
}
