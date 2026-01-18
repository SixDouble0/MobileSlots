package com.mobileslots.data.repository

import com.mobileslots.data.local.dao.UserDao
import com.mobileslots.data.local.dao.UserSettingsDao
import com.mobileslots.data.local.entity.UserEntity
import com.mobileslots.data.local.entity.UserSettingsEntity
import com.mobileslots.domain.model.User
import com.mobileslots.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date

class UserRepository(
    private val userDao: UserDao,
    private val userSettingsDao: UserSettingsDao
) {
    fun getCurrentUser(): Flow<User?> {
        return userDao.getCurrentUser().map { it?.toDomainModel() }
    }

    fun getUserById(userId: Long): Flow<User?> {
        return userDao.getUserById(userId).map { it?.toDomainModel() }
    }

    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)?.toDomainModel()
    }

    suspend fun createUser(username: String): Long {
        val user = UserEntity(username = username)
        val userId = userDao.insertUser(user)
        // Create default settings for new user
        val settings = UserSettingsEntity(userId = userId)
        userSettingsDao.insertSettings(settings)
        return userId
    }

    suspend fun updateBalance(userId: Long, newBalance: Int) {
        userDao.updateBalance(userId, newBalance)
    }

    suspend fun updateLastPlayed(userId: Long) {
        userDao.updateLastPlayed(userId, Date().time)
    }

    fun getUserSettings(userId: Long): Flow<UserSettings?> {
        return userSettingsDao.getSettings(userId).map { it?.toDomainModel() }
    }

    suspend fun updateSettings(settings: UserSettings) {
        userSettingsDao.updateSettings(settings.toEntity())
    }

    suspend fun getUserCount(): Int {
        return userDao.getUserCount()
    }

    // Entity to Domain conversions
    private fun UserEntity.toDomainModel() = User(
        userId = userId,
        username = username,
        balance = balance,
        createdAt = createdAt,
        lastPlayedAt = lastPlayedAt
    )

    private fun UserSettingsEntity.toDomainModel() = UserSettings(
        userId = userId,
        soundEnabled = soundEnabled,
        vibrationEnabled = vibrationEnabled,
        shakeToSpinEnabled = shakeToSpinEnabled,
        language = language
    )

    private fun UserSettings.toEntity() = UserSettingsEntity(
        userId = userId,
        soundEnabled = soundEnabled,
        vibrationEnabled = vibrationEnabled,
        shakeToSpinEnabled = shakeToSpinEnabled,
        language = language
    )
}
