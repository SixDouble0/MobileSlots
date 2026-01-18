package com.mobileslots.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "user_settings",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"], unique = true)]
)
data class UserSettingsEntity(
    @PrimaryKey
    val userId: Long,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val shakeToSpinEnabled: Boolean = true,
    val language: String = "en"
)
