package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.model.CareerProfile
import com.example.model.Club
import com.example.model.Coach
import com.example.model.DailyChallenge
import com.example.model.MatchRecord
import com.example.model.NotificationAlert
import com.example.model.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        CareerProfile::class,
        Club::class,
        Coach::class,
        Player::class,
        MatchRecord::class,
        DailyChallenge::class,
        NotificationAlert::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun careerDao(): CareerDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "offside_pro_career.db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.careerDao())
                    }
                }
            }

            override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                super.onDestructiveMigration(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.careerDao())
                    }
                }
            }

            suspend fun populateInitialData(dao: CareerDao) {
                dao.insertProfile(InitialData.defaultProfile)
                dao.insertClubs(InitialData.clubs)
                dao.insertCoaches(InitialData.coaches)
                dao.insertPlayers(InitialData.players)
                dao.insertMatches(InitialData.matches)
                dao.insertChallenges(InitialData.dailyChallenges)
                for (notification in InitialData.notifications) {
                    dao.insertNotification(notification)
                }
            }
        }
    }
}
