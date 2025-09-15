package com.example.tomatonotpotato.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey val id: Int = 0, // Single-row table
    val bestStreak: Int,
    val totalPomodori: Int = 0
)

@Dao
interface UserStatsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stats: UserStats)

    @Update
    suspend fun update(stats: UserStats)

    @Query("SELECT * FROM user_stats WHERE id = 0")
    suspend fun getStats(): UserStats?
}

class UserStatsRepository(private val dao: UserStatsDao) {
    suspend fun getBestStreak(): Int = dao.getStats()?.bestStreak ?: 0
    suspend fun updateBestStreak(streak: Int) {
        val current = dao.getStats()
        if (current == null) {
            dao.insert(UserStats(bestStreak = streak))
        } else if (streak > current.bestStreak) {
            dao.update(current.copy(bestStreak = streak))
        }
    }
    suspend fun updateTotalPomodori(pomodori: Int) {
        val current = dao.getStats()
        if (current != null) {
            dao.update(current.copy(totalPomodori = pomodori))
        } else {
            dao.insert(UserStats(bestStreak = 0, totalPomodori = pomodori))
        }
    }

    suspend fun getTotalPomodori(): Int = dao.getStats()?.totalPomodori ?: 0
}
