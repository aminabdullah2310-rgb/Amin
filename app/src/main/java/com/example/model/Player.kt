package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class Player(
    @PrimaryKey val id: String,
    val name: String,
    val clubId: String,
    val clubName: String,
    val position: String, // ST, LW, RW, CAM, CM, CDM, CB, LB, RB, GK
    val age: Int,
    val overallRating: Int,
    val potentialRating: Int,
    val pace: Int,
    val shooting: Int,
    val passing: Int,
    val dribbling: Int,
    val defending: Int,
    val physical: Int,
    val offsideAwareness: Int = 75,
    val marketValue: Long, // in Euros
    val weeklyWage: Long,
    val morale: Int = 90,
    val energy: Int = 100,
    val trainingXp: Int = 0,
    val trainingTargetXp: Int = 100,
    val isStartingXI: Boolean = true,
    val nationality: String,
    val photoUrl: String = "",
    val clubLogoUrl: String = "",
    val nationalityFlag: String = "🌍",
    val cardRarity: String = "GOLD_RARE" // GOLD_RARE, ICON, FUTURE_STAR, TOTW
)
