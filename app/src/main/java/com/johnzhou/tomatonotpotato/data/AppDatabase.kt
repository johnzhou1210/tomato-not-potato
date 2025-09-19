package com.johnzhou.tomatonotpotato.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.TypeConverter
import java.time.LocalDate
import android.content.Context
import androidx.room.Room


@Database(entities = [PomodoroRecord::class, AppOpenDay::class, UserStats::class], version = 3)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pomodoroDao(): PomodoroDao
    abstract fun appOpenDayDao(): AppOpenDayDao
    abstract fun userStatsDao(): UserStatsDao
}

class Converters {
    @TypeConverter
    fun fromEpochDay(value: Long?): LocalDate? = value?.let { LocalDate.ofEpochDay(it) }

    @TypeConverter
    fun localDateToEpochDay(date: LocalDate?): Long? = date?.toEpochDay()
}

object DatabaseProvider {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "pomodoro_db"
            ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
        }
    }
}