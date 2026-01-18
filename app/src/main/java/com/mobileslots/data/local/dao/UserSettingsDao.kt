package com.mobileslots.data.local.dao

import androidx.room.*
import com.mobileslots.data.local.entity.UserSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSettingsDao {
    @Query("SELECT * FROM user_settings WHERE userId = :userId")
    fun getSettings(userId: Long): Flow<UserSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: UserSettingsEntity)

    @Update
    suspend fun updateSettings(settings: UserSettingsEntity)

    @Delete
    suspend fun deleteSettings(settings: UserSettingsEntity)

    @Query("UPDATE user_settings SET soundEnabled = :enabled WHERE userId = :userId")
    suspend fun updateSoundEnabled(userId: Long, enabled: Boolean)

    @Query("UPDATE user_settings SET vibrationEnabled = :enabled WHERE userId = :userId")
    suspend fun updateVibrationEnabled(userId: Long, enabled: Boolean)

    @Query("UPDATE user_settings SET shakeToSpinEnabled = :enabled WHERE userId = :userId")
    suspend fun updateShakeToSpinEnabled(userId: Long, enabled: Boolean)

    @Query("UPDATE user_settings SET language = :language WHERE userId = :userId")
    suspend fun updateLanguage(userId: Long, language: String)
}
