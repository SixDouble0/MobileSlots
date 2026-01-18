package com.example.slotsgame.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM users WHERE username = :name")
    suspend fun getUser(name: String): User?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Insert
    suspend fun insertSpin(spin: SpinHistory)

    // Zapytanie z relacją (wymagane w projekcie)
    @Transaction
    @Query("SELECT * FROM users WHERE username = :name")
    fun getUserWithHistory(name: String): Flow<UserWithHistory>

    @Query("SELECT * FROM spin_history WHERE username = :name ORDER BY timestamp DESC")
    fun getHistoryFlow(name: String): Flow<List<SpinHistory>>
}