package com.example.model

data class NewsItem(
    val id: String,
    val title: String,
    val summary: String,
    val category: String,
    val timeAgo: String,
    val tag: String,
    val location: String,
    val imageUrl: String
)

data class FriendItem(
    val id: String,
    val name: String,
    val initial: String,
    val status: String, // ONLINE, IN_MATCH, OFFLINE
    val statusText: String
)

data class LeaderboardUser(
    val rank: Int,
    val name: String,
    val badge: String,
    val subtext: String,
    val xp: Int
)

data class GameModeItem(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val category: String,
    val iconName: String,
    val colorHex: Long
)
