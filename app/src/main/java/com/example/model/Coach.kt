package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coaches")
data class Coach(
    @PrimaryKey val id: String,
    val name: String,
    val clubId: String,
    val clubName: String,
    val nationality: String,
    val tacticalPhilosophy: String,
    val tacticTitle: String,
    val tacticBonus: String,
    val offsideTrapMastery: Int,
    val pressIntensity: Int,
    val defensiveLineDepth: Int,
    val reputation: Int,
    val photoUrl: String = "",
    val clubLogoUrl: String = "",
    val avatarEmoji: String = "👔"
)
