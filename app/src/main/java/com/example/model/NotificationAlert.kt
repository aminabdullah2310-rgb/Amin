package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationAlert(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val type: String, // MATCH_RESULT, LINEUP, TRANSFER, OFFSIDE_ANALYSIS, CHALLENGE
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val iconEmoji: String = "🔔"
)
