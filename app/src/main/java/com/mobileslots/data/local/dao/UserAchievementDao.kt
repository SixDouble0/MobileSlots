package com.mobileslots.data.local.dao

import androidx.room.*
import com.mobileslots.data.local.entity.AchievementEntity
import com.mobileslots.data.local.entity.UserAchievementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserAchievementDao {
    @Query("""
        SELECT a.* FROM achievements a 
        INNER JOIN user_achievements ua ON a.achievementId = ua.achievementId 
        WHERE ua.userId = :userId 
        ORDER BY ua.unlockedAt DESC
    """)
    fun getUserAchievements(userId: Long): Flow<List<AchievementEntity>>

    @Query("""
        SELECT a.* FROM achievements a 
        WHERE a.achievementId NOT IN (
            SELECT achievementId FROM user_achievements WHERE userId = :userId
        )
    """)
    fun getLockedAchievements(userId: Long): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM user_achievements WHERE userId = :userId AND achievementId = :achievementId")
    suspend fun getUserAchievement(userId: Long, achievementId: Long): UserAchievementEntity?

    @Query("SELECT COUNT(*) FROM user_achievements WHERE userId = :userId")
    suspend fun getUserAchievementCount(userId: Long): Int

    @Query("SELECT progress FROM user_achievements WHERE userId = :userId AND achievementId = :achievementId")
    suspend fun getAchievementProgress(userId: Long, achievementId: Long): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserAchievement(userAchievement: UserAchievementEntity)

    @Update
    suspend fun updateUserAchievement(userAchievement: UserAchievementEntity)

    @Delete
    suspend fun deleteUserAchievement(userAchievement: UserAchievementEntity)

    @Query("UPDATE user_achievements SET progress = :progress WHERE userId = :userId AND achievementId = :achievementId")
    suspend fun updateProgress(userId: Long, achievementId: Long, progress: Int)

    @Query("DELETE FROM user_achievements WHERE userId = :userId")
    suspend fun deleteUserAchievements(userId: Long)
}
