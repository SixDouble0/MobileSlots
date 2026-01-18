package com.example.slotsgame.data.local

import androidx.room.*

@Entity(tableName = "users")
data class User(
    @PrimaryKey val username: String,
    val balance: Double
)

@Entity(tableName = "spin_history")
data class SpinHistory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String, // Klucz obcy
    val winAmount: Double,
    val timestamp: Long,
    val symbols: String // Np. "7-7-7"
)

// Relacja 1 do wielu (dla punktów za Relacje w bazie)
data class UserWithHistory(
    @Embedded val user: User,
    @Relation(
        parentColumn = "username",
        entityColumn = "username"
    )
    val history: List<SpinHistory>
)