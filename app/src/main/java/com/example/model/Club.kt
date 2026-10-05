package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clubs")
data class Club(
    @PrimaryKey val id: String,
    val name: String,
    val shortName: String,
    val league: String,
    val primaryColorHex: String,
    val secondaryColorHex: String,
    val stadiumName: String,
    val budget: Long,
    val prestigeRating: Int,
    val managerName: String,
    val formation: String = "4-3-3",
    val logoEmoji: String = "⚽",
    val logoUrl: String = "",
    val wins: Int = 0,
    val draws: Int = 0,
    val losses: Int = 0,
    val points: Int = 0,
    val played: Int = 0,
    val goalsFor: Int = 0,
    val goalsAgainst: Int = 0
)
