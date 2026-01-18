package com.mobileslots.data.repository

import com.mobileslots.data.local.dao.AchievementDao
import com.mobileslots.data.local.dao.UserAchievementDao
import com.mobileslots.data.local.entity.AchievementEntity
import com.mobileslots.data.local.entity.UserAchievementEntity
import com.mobileslots.domain.model.Achievement
import com.mobileslots.domain.model.AchievementCategory
import com.mobileslots.domain.model.UserAchievement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date

class AchievementRepository(
    private val achievementDao: AchievementDao,
    private val userAchievementDao: UserAchievementDao
) {

    fun getAllAchievements(): Flow<List<Achievement>> {
        return achievementDao.getAllAchievements().map { achievements ->
            achievements.map { it.toDomainModel() }
        }
    }

    fun getUserAchievements(userId: Long): Flow<List<Achievement>> {
        return userAchievementDao.getUserAchievements(userId).map { achievements ->
            achievements.map { it.toDomainModel() }
        }
    }

    fun getLockedAchievements(userId: Long): Flow<List<Achievement>> {
        return userAchievementDao.getLockedAchievements(userId).map { achievements ->
            achievements.map { it.toDomainModel() }
        }
    }

    suspend fun unlockAchievement(userId: Long, achievementId: Long) {
        val userAchievement = UserAchievementEntity(
            userId = userId,
            achievementId = achievementId,
            unlockedAt = Date(),
            progress = 100
        )
        userAchievementDao.insertUserAchievement(userAchievement)
    }

    suspend fun updateProgress(userId: Long, achievementId: Long, progress: Int) {
        userAchievementDao.updateProgress(userId, achievementId, progress)
    }

    suspend fun getAchievementProgress(userId: Long, achievementId: Long): Int {
        return userAchievementDao.getAchievementProgress(userId, achievementId) ?: 0
    }

    suspend fun initializeAchievements() {
        val achievements = listOf(
            AchievementEntity(
                achievementId = 1,
                name = "First Win",
                description = "Win your first game",
                iconName = "ic_first_win",
                requiredValue = 1,
                category = "WINS"
            ),
            AchievementEntity(
                achievementId = 2,
                name = "Big Winner",
                description = "Win 1000 chips in a single game",
                iconName = "ic_big_winner",
                requiredValue = 1000,
                category = "WINS"
            ),
            AchievementEntity(
                achievementId = 3,
                name = "Marathon Player",
                description = "Play 100 games",
                iconName = "ic_marathon",
                requiredValue = 100,
                category = "GAMES_PLAYED"
            ),
            AchievementEntity(
                achievementId = 4,
                name = "Lucky Seven",
                description = "Get three sevens in slots",
                iconName = "ic_lucky_seven",
                requiredValue = 1,
                category = "SPECIAL"
            ),
            AchievementEntity(
                achievementId = 5,
                name = "Blackjack Master",
                description = "Get 5 blackjacks",
                iconName = "ic_blackjack",
                requiredValue = 5,
                category = "SPECIAL"
            )
        )
        achievementDao.insertAchievements(achievements)
    }

    private fun AchievementEntity.toDomainModel() = Achievement(
        achievementId = achievementId,
        name = name,
        description = description,
        iconName = iconName,
        requiredValue = requiredValue,
        category = AchievementCategory.valueOf(category)
    )
}
