package com.johnzhou.tomatonotpotato.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

@Entity(tableName = "app_open_days")
data class AppOpenDay (
    @PrimaryKey val date: LocalDate
)

@Dao
interface AppOpenDayDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(day: AppOpenDay)

    @Query("SELECT COUNT(*) FROM app_open_days WHERE date = :day")
    suspend fun count(day: LocalDate): Int

    @Query("SELECT * FROM app_open_days")
    fun getAll(): Flow<List<AppOpenDay>>

    @Query("SELECT MIN(date) FROM app_open_days")
    suspend fun getOldestDate(): LocalDate?

}

class AppOpenRepository(private val dao: AppOpenDayDao) {
    companion object {

    }

    suspend fun logToday() {
        dao.insert(AppOpenDay(LocalDate.now()))

    }

    suspend fun hasOpenedOn(day: LocalDate): Boolean = dao.count(day) > 0

    fun getAllDays(): Flow<List<LocalDate>> = dao.getAll().map { e -> e.map { it.date } }

    suspend fun getOldestDate(): LocalDate? = dao.getOldestDate()
}
