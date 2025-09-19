package com.johnzhou.tomatonotpotato.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.time.LocalDate
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pomodoro_records")
data class PomodoroRecord(
    @PrimaryKey val date: LocalDate,
    val completedSessions: Int,
)

@Dao
interface PomodoroDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: PomodoroRecord)

    @Query("SELECT * FROM pomodoro_records WHERE date = :date")
    suspend fun getByDate(date: LocalDate): PomodoroRecord?



    @Query("SELECT * FROM pomodoro_records ORDER BY date ASC")
    fun getAll(): kotlinx.coroutines.flow.Flow<List<PomodoroRecord>>

    @Query("DELETE FROM pomodoro_records")
    fun deleteAll(): Unit
}
