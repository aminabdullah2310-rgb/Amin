package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.CareerProfile
import com.example.model.Club
import com.example.model.Coach
import com.example.model.DailyChallenge
import com.example.model.MatchRecord
import com.example.model.NotificationAlert
import com.example.model.Player
import kotlinx.coroutines.flow.Flow

@Dao
interface CareerDao {

    // --- Profile ---
    @Query("SELECT * FROM career_profile WHERE id = 1 LIMIT 1")
    fun getCareerProfile(): Flow<CareerProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: CareerProfile)

    @Update
    suspend fun updateProfile(profile: CareerProfile)

    // --- Clubs ---
    @Query("SELECT * FROM clubs ORDER BY points DESC, prestigeRating DESC")
    fun getAllClubs(): Flow<List<Club>>

    @Query("SELECT * FROM clubs WHERE id = :clubId LIMIT 1")
    fun getClubById(clubId: String): Flow<Club?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClubs(clubs: List<Club>)

    @Update
    suspend fun updateClub(club: Club)

    // --- Coaches ---
    @Query("SELECT * FROM coaches")
    fun getAllCoaches(): Flow<List<Coach>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoaches(coaches: List<Coach>)

    // --- Players ---
    @Query("SELECT * FROM players ORDER BY overallRating DESC")
    fun getAllPlayers(): Flow<List<Player>>

    @Query("SELECT * FROM players WHERE clubId = :clubId ORDER BY isStartingXI DESC, overallRating DESC")
    fun getPlayersByClub(clubId: String): Flow<List<Player>>

    @Query("SELECT * FROM players WHERE clubId != :myClubId ORDER BY overallRating DESC")
    fun getTransferMarketPlayers(myClubId: String): Flow<List<Player>>

    @Query("SELECT * FROM players WHERE id = :playerId LIMIT 1")
    suspend fun getPlayerByIdDirect(playerId: String): Player?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayers(players: List<Player>)

    @Update
    suspend fun updatePlayer(player: Player)

    // --- Matches ---
    @Query("SELECT * FROM matches ORDER BY weekNumber ASC")
    fun getAllMatches(): Flow<List<MatchRecord>>

    @Query("SELECT * FROM matches WHERE isFinished = 0 ORDER BY weekNumber ASC")
    fun getUpcomingMatches(): Flow<List<MatchRecord>>

    @Query("SELECT * FROM matches WHERE isFinished = 1 ORDER BY weekNumber DESC")
    fun getFinishedMatches(): Flow<List<MatchRecord>>

    @Query("SELECT * FROM matches WHERE id = :matchId LIMIT 1")
    suspend fun getMatchById(matchId: String): MatchRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatches(matches: List<MatchRecord>)

    @Update
    suspend fun updateMatch(match: MatchRecord)

    // --- Challenges ---
    @Query("SELECT * FROM daily_challenges")
    fun getAllChallenges(): Flow<List<DailyChallenge>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenges(challenges: List<DailyChallenge>)

    @Update
    suspend fun updateChallenge(challenge: DailyChallenge)

    // --- Notifications ---
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationAlert>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationAlert)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsAsRead()
}
