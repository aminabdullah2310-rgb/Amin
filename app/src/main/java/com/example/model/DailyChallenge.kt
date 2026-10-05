package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_challenges")
data class DailyChallenge(
    @PrimaryKey val id: String,
    val title: String,
    val titleAr: String,
    val description: String,
    val descriptionAr: String,
    val currentProgress: Int,
    val targetGoal: Int,
    val rewardCoins: Long,
    val rewardXp: Int,
    val isCompleted: Boolean = false,
    val isClaimed: Boolean = false,
    val categoryIcon: String = "🎯"
)
