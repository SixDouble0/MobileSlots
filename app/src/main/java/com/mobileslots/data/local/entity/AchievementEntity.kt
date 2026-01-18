package com.mobileslots.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey(autoGenerate = true)
    val achievementId: Long = 0,
    val name: String,
    val description: String,
    val iconName: String,
    val requiredValue: Int,
    val category: String // "WINS", "GAMES_PLAYED", "SPECIAL"
)
