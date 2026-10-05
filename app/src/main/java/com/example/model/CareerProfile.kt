package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "career_profile")
data class CareerProfile(
    @PrimaryKey val id: Int = 1,
    val managerName: String = "Amin Abdullah",
    val managerNationality: String = "Saudi Arabia",
    val chosenClubId: String = "real_madrid",
    val chosenClubName: String = "Real Madrid",
    val seasonYear: Int = 2026,
    val currentWeek: Int = 1,
    val transferBudget: Long = 185_000_000L,
    val weeklyWageBudget: Long = 4_200_000L,
    val managerReputation: Int = 89,
    val currentTactic: String = "Gegenpressing",
    val defensiveLineHeight: Int = 72,
    val offsideTrapEnabled: Boolean = true,
    val pressingIntensity: Int = 80,
    val userEmail: String = "aminabdullah2310@gmail.com",
    val isCloudSynced: Boolean = true,
    val lastSyncTimestamp: Long = System.currentTimeMillis(),
    val totalCoins: Long = 12_450L,
    val totalGems: Int = 450,
    val coachXp: Int = 4_820,
    val nextLevelXp: Int = 6_000,
    val managerLevel: Int = 38
)
