package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.InitialData
import com.example.model.Club
import com.example.model.Coach
import com.example.model.FriendItem
import com.example.model.GameModeItem
import com.example.model.LeaderboardUser
import com.example.model.MatchRecord
import com.example.model.NewsItem
import com.example.model.Player
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricGold
import com.example.ui.theme.GoldGlow
import com.example.ui.theme.PitchGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.CareerUiState
import com.example.viewmodel.NavigationTab
import com.example.util.AppImageResolver

@Composable
fun HomeScreen(
    uiState: CareerUiState,
    onNavigateTab: (NavigationTab) -> Unit,
    onStartMatchSimulation: (MatchRecord) -> Unit,
    onSyncCloud: () -> Unit,
    onAddCoinsClick: () -> Unit = {}
) {
    val nextMatch = uiState.allMatches.firstOrNull { !it.isFinished } ?: uiState.allMatches.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSurface)
            .padding(horizontal = 14.dp)
            .testTag("screen_home"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 0. Top Bar HUD (User, Level, XP, Coins, Notifications)
        item {
            Spacer(modifier = Modifier.height(4.dp))
            TopHeaderHud(
                profile = uiState.profile,
                onSyncClick = onSyncCloud,
                onAddCoins = onAddCoinsClick,
                onNotificationsClick = { onNavigateTab(NavigationTab.NOTIFICATIONS) }
            )
        }

        // 1. Hero Promo Banner: Messi x Ronaldo - Two Legends • One Passion
        item {
            HeroPromoBanner(
                onPlayNowClick = {
                    if (nextMatch != null) {
                        onStartMatchSimulation(nextMatch)
                    } else {
                        onNavigateTab(NavigationTab.SQUAD)
                    }
                }
            )
        }

        // 2. Official Elite Clubs (Horizontal strip with authentic crests)
        item {
            OfficialClubsStrip(
                clubs = uiState.allClubs,
                onClubClick = { onNavigateTab(NavigationTab.SQUAD) }
            )
        }

        // 3. عوالم OFFSIDE PRO (The 7 Worlds)
        item {
            OffsideWorldsSection(
                onWorldClick = { worldIndex ->
                    when (worldIndex) {
                        0 -> onNavigateTab(NavigationTab.CHALLENGES) // المعرفة
                        1 -> onNavigateTab(NavigationTab.SQUAD) // الساحة
                        2 -> onNavigateTab(NavigationTab.SQUAD) // التشكيلة
                        3 -> onNavigateTab(NavigationTab.TRAINING) // المسيرة
                        4 -> onNavigateTab(NavigationTab.AI_ANALYST) // عالم الكرة
                        5 -> onNavigateTab(NavigationTab.TRANSFERS) // النادي
                        else -> onNavigateTab(NavigationTab.NOTIFICATIONS) // المجتمع
                    }
                }
            )
        }

        // 4. طور المسيرة الاحترافية (FC Career Section)
        item {
            CareerModeHubSection(
                uiState = uiState,
                onOpenManagerCareer = { onNavigateTab(NavigationTab.SQUAD) },
                onOpenPlayerCareer = { onNavigateTab(NavigationTab.TRAINING) },
                onReviewTransferOffer = { onNavigateTab(NavigationTab.TRANSFERS) }
            )
        }

        // 5. نجوم الأسبوع وبطاقات اللاعبين (Featured Player Cards)
        item {
            FeaturedPlayerCardsSection(
                players = uiState.squadPlayers.take(6),
                onPlayerClick = { onNavigateTab(NavigationTab.SQUAD) }
            )
        }

        // 6. المدربون العالميون والتكتيكات (World Coaches & Tactics)
        item {
            WorldCoachesSection(
                coaches = uiState.allCoaches.ifEmpty { InitialData.coaches },
                onCoachClick = { onNavigateTab(NavigationTab.AI_ANALYST) }
            )
        }

        // 7. جميع أوضاع اللعب (All 29 Game Modes + Filter Pills)
        item {
            GameModesSection(
                modes = uiState.gameModesList,
                selectedFilter = uiState.activeGameModeFilter,
                onFilterSelected = { /* Handled in VM */ },
                onModeClick = { mode ->
                    if (nextMatch != null) onStartMatchSimulation(nextMatch) else onNavigateTab(NavigationTab.SQUAD)
                }
            )
        }

        // 8. أحدث الأخبار (Latest Football News with real photos)
        item {
            LatestNewsSection(newsList = uiState.newsList)
        }

        // 9. أصدقاؤك (Friends Online Hub)
        item {
            FriendsOnlineSection(
                friends = uiState.friendsList,
                onOpenChat = { onNavigateTab(NavigationTab.AI_ANALYST) }
            )
        }

        // 10. الترتيب العالمي (Global Leaderboard)
        item {
            GlobalLeaderboardSection(
                leaderboard = uiState.leaderboardList,
                currentUserXp = uiState.profile?.coachXp ?: 4820
            )
        }

        // 11. Creator Badge & Footer Delight
        item {
            FooterDelight()
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// --- Top HUD Bar ---
@Composable
private fun TopHeaderHud(
    profile: com.example.model.CareerProfile?,
    onSyncClick: () -> Unit,
    onAddCoins: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B101B)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22334A))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: User Profile + Level Ring
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(contentAlignment = Alignment.BottomStart) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.sweepGradient(
                                    listOf(Color(0xFFF5A623), Color(0xFF3B82F6), Color(0xFF8B5CF6), Color(0xFFF5A623))
                                )
                            )
                            .padding(2.5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF131D2E), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "A",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFCD34D)
                            )
                        }
                    }
                    // Level Badge
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(Color(0xFFF5A623), CircleShape)
                            .border(1.dp, Color(0xFF0B101B), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${profile?.managerLevel ?: 38}",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = profile?.managerName ?: "Amin_Abdullah",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFF10B981), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${profile?.coachXp ?: 4820} / ${profile?.nextLevelXp ?: 6000} XP",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Right: Coins Chip & Notifications & Cloud Sync
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Coins Pill
                Row(
                    modifier = Modifier
                        .background(Color(0xFF131D2E), RoundedCornerShape(20.dp))
                        .border(1.dp, Color(0xFF22334A), RoundedCornerShape(20.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "AM Coins",
                        tint = Color(0xFFF5A623),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${profile?.totalCoins ?: 12450}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFCD34D)
                    )
                    Text(text = " AM", fontSize = 9.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(Color(0xFFF5A623).copy(alpha = 0.2f), CircleShape)
                            .clickable { onAddCoins() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Coins", tint = Color(0xFFF5A623), modifier = Modifier.size(12.dp))
                    }
                }

                // Notifications
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFF131D2E), RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0xFF22334A), RoundedCornerShape(10.dp))
                        .clickable { onNotificationsClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = "Alerts", tint = TextPrimary, modifier = Modifier.size(18.dp))
                }

                // Cloud Sync Icon
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFF5A623).copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0xFFF5A623).copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .clickable { onSyncClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "☁️", fontSize = 16.sp)
                }
            }
        }
    }
}

// --- 1. Hero Promo Banner (Messi x Ronaldo) ---
@Composable
private fun HeroPromoBanner(onPlayNowClick: () -> Unit) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_promo_banner"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131D2E)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF5A623).copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(310.dp)
        ) {
            // Split Background with Messi on right and Ronaldo on left
            Row(modifier = Modifier.fillMaxSize()) {
                // Messi (Right side in RTL)
                Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(InitialData.PHOTO_MESSI)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Lionel Messi",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color(0xFF0B101B).copy(alpha = 0.85f), Color(0xFF0B101B))
                                )
                            )
                    )
                    // Messi Badge
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.TopEnd)
                            .background(Color(0xFF0B101B).copy(alpha = 0.85f), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFFF5A623).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = "MESSI 94", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFCD34D))
                    }
                }

                // Ronaldo (Left side in RTL)
                Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(InitialData.PHOTO_RONALDO)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Cristiano Ronaldo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color(0xFF0B101B).copy(alpha = 0.85f), Color(0xFF0B101B))
                                )
                            )
                    )
                    // Ronaldo Badge
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.TopStart)
                            .background(Color(0xFF0B101B).copy(alpha = 0.85f), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFFF5A623).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = "RONALDO 93", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFCD34D))
                    }
                }
            }

            // Foreground Text and CTA
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF0B101B).copy(alpha = 0.85f), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFF5A623).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(text = "AM CLUB • صدام الأساطير", fontSize = 10.sp, color = Color(0xFFFCD34D), fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .background(Color(0xFF0B101B).copy(alpha = 0.85f), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFF5A623).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(text = "TWO LEGENDS • ONE PASSION", fontSize = 9.sp, color = Color(0xFFF5A623), fontWeight = FontWeight.Medium)
                    }
                }

                // Bottom Content & CTA
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "كرة القدم أكثر من مجرد لعبة",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "تحدَّ أصدقاءك في أكثر من 25 وضعًا كرويًا",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFCD34D)
                    )
                    Text(
                        text = "اجمع البطاقات، ابنِ تشكيلتك الذهبية ونافس في الساحة العالمية",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onPlayNowClick,
                        modifier = Modifier
                            .height(44.dp)
                            .padding(horizontal = 24.dp)
                            .testTag("play_now_hero_button"),
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF5A623)
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Text(text = "العب الآن", color = Color(0xFF0B101B), fontWeight = FontWeight.Black, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF0B101B), modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

// --- 2. Official Elite Clubs Horizontal Strip ---
@Composable
private fun OfficialClubsStrip(
    clubs: List<Club>,
    onClubClick: (Club) -> Unit
) {
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFFF5A623), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(text = "أندية النخبة الرسمية", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "الشعارات والبطاقات المعتمدة", fontSize = 11.sp, color = TextSecondary)
                }
            }
            Text(text = "${clubs.size} أندية كبرى", fontSize = 11.sp, color = Color(0xFFFCD34D), fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(clubs) { club ->
                Row(
                    modifier = Modifier
                        .background(Color(0xFF131D2E), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFF22334A), RoundedCornerShape(12.dp))
                        .clickable { onClubClick(club) }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val clubLogoModel = AppImageResolver.resolveClubLogo(club.logoUrl, club.id, club.name)
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0B101B))
                            .border(1.dp, Color(0xFFF5A623).copy(alpha = 0.5f), CircleShape)
                            .padding(2.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(clubLogoModel)
                                .crossfade(true)
                                .build(),
                            contentDescription = club.name,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = club.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            }
        }
    }
}

// --- 3. عوالم OFFSIDE PRO (The 7 Worlds) ---
@Composable
private fun OffsideWorldsSection(onWorldClick: (Int) -> Unit) {
    val context = LocalContext.current
    val worlds = listOf(
        Triple("المعرفة", "اختبر معلوماتك وواجه التحديات", InitialData.WORLD_KNOWLEDGE),
        Triple("الساحة", "تنافس . اربح . وتصدر الترتيب", InitialData.WORLD_ARENA),
        Triple("التشكيلة", "ابنِ فريقك . افتح البطاقات", InitialData.WORLD_SQUAD),
        Triple("المسيرة الاحترافية", "طور المدرب واللاعب الواقعي", InitialData.WORLD_CAREER),
        Triple("عالم الكرة", "أخبار وإحصائيات وعقود", InitialData.WORLD_GLOBAL),
        Triple("النادي", "أدر ناديك وحقق الألقاب", InitialData.WORLD_CLUB)
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "🌍 عوالم OFFSIDE PRO", fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                Text(text = "7 عوالم .. تجربة لا تنتهي", fontSize = 11.sp, color = TextSecondary)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "عرض الكل", fontSize = 11.sp, color = Color(0xFFF5A623), fontWeight = FontWeight.Bold)
                Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = Color(0xFFF5A623), modifier = Modifier.size(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2-Column Grid for Worlds
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            for (i in 0 until worlds.size step 2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WorldCard(
                        title = worlds[i].first,
                        desc = worlds[i].second,
                        imgUrl = worlds[i].third,
                        modifier = Modifier.weight(1f),
                        onClick = { onWorldClick(i) }
                    )
                    if (i + 1 < worlds.size) {
                        WorldCard(
                            title = worlds[i + 1].first,
                            desc = worlds[i + 1].second,
                            imgUrl = worlds[i + 1].third,
                            modifier = Modifier.weight(1f),
                            onClick = { onWorldClick(i + 1) }
                        )
                    }
                }
            }

            // 7. Full width Community & Clans World
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onWorldClick(6) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131D2E)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(Color(0xFF8B5CF6).copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "👥", fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "المجتمع والكلانات", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF10B981).copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(text = "مباشر", fontSize = 9.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(text = "أصدقاء . كلانات . شات وتحديات جماعية", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                    Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = TextSecondary)
                }
            }
        }
    }
}

@Composable
private fun WorldCard(
    title: String,
    desc: String,
    imgUrl: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    Card(
        modifier = modifier
            .height(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131D2E)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22334A))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(85.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imgUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(listOf(Color.Transparent, Color(0xFF131D2E)))
                        )
                )
            }
            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)) {
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1)
                Text(text = desc, fontSize = 10.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

// --- 4. طور المسيرة الاحترافية (FC Career Section) ---
@Composable
private fun CareerModeHubSection(
    uiState: CareerUiState,
    onOpenManagerCareer: () -> Unit,
    onOpenPlayerCareer: () -> Unit,
    onReviewTransferOffer: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131D2E)),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFF5A623).copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFF5A623).copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⚽", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "طور المسيرة الاحترافية", fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFF5A623), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(text = "FC CAREER", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.Black)
                            }
                        }
                        Text(text = "إدارة الأندية أو احتراف كلاعب صاعد بنظام واقعي", fontSize = 11.sp, color = TextSecondary)
                    }
                }
                Text(text = "موسم 2026", fontSize = 11.sp, color = Color(0xFFFCD34D), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dual Career Tracks: Manager vs Player
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Manager Career Track
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF5A623).copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "👔", fontSize = 20.sp)
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFF5A623).copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(text = "MANAGER", fontSize = 9.sp, color = Color(0xFFFCD34D), fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "مسيرة مدرب", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(text = "إدارة الميزانيات، التكتيك والمؤتمرات الصحفية.", fontSize = 10.sp, color = TextSecondary, maxLines = 2)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onOpenManagerCareer,
                            modifier = Modifier.fillMaxWidth().height(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5A623))
                        ) {
                            Text(text = "متابعة الإدارة", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Player Career Track
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B82F6).copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "⚡", fontSize = 20.sp)
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF3B82F6).copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(text = "PLAYER", fontSize = 9.sp, color = Color(0xFFADC6FF), fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "مسيرة لاعب", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(text = "اصنع نجمك الصاعد، طوّر المهارات والأهداف.", fontSize = 10.sp, color = TextSecondary, maxLines = 2)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onOpenPlayerCareer,
                            modifier = Modifier.fillMaxWidth().height(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                        ) {
                            Text(text = "بدء مسيرة نجم", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Operations Room (Budget, Spirit, Next Match)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0B101B), RoundedCornerShape(12.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "ميزانية الانتقالات", fontSize = 10.sp, color = TextSecondary)
                    Text(text = "€185,000,000", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color(0xFFFCD34D))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "روح الفريق والجاهزية", fontSize = 10.sp, color = TextSecondary)
                    Text(text = "94% ممتاز", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color(0xFF10B981))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "المباراة القادمة", fontSize = 10.sp, color = TextSecondary)
                    Text(text = "ضد مان سيتي", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color(0xFFF5A623))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Active Transfer Offer Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
                    .clickable { onReviewTransferOffer() }
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🤝", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = "عرض رسمي مقدم للاعب (جناح أيمن)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(text = "المفاوضات جارية • رسوم متفق عليها €82M", fontSize = 10.sp, color = TextSecondary)
                    }
                }
                Box(
                    modifier = Modifier
                        .background(Color(0xFFF5A623), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "مراجعة العرض", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.Black)
                }
            }
        }
    }
}

// --- 5. نجوم الأسبوع وبطاقات اللاعبين ---
@Composable
private fun FeaturedPlayerCardsSection(
    players: List<Player>,
    onPlayerClick: (Player) -> Unit
) {
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Stars, contentDescription = null, tint = Color(0xFFF5A623), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(text = "نجوم الأسبوع وبطاقات اللاعبين", fontSize = 17.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                    Text(text = "بطاقات خاصة وإحصائيات FIFA التراكمية", fontSize = 11.sp, color = Color(0xFFFCD34D))
                }
            }
            Text(text = "المزيد ➔", fontSize = 11.sp, color = Color(0xFFF5A623), fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2-Column Grid for Cards
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            val list = players.ifEmpty { InitialData.players.take(6) }
            for (i in 0 until list.size step 2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MiniPlayerCard(player = list[i], modifier = Modifier.weight(1f), onClick = { onPlayerClick(list[i]) })
                    if (i + 1 < list.size) {
                        MiniPlayerCard(player = list[i + 1], modifier = Modifier.weight(1f), onClick = { onPlayerClick(list[i + 1]) })
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniPlayerCard(
    player: Player,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    Card(
        modifier = modifier
            .height(230.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131D2E)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF5A623).copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            val playerPhotoModel = AppImageResolver.resolvePlayerImage(player.photoUrl, player.id, player.name)
            val clubLogoModel = AppImageResolver.resolveClubLogo(player.clubLogoUrl, player.clubId, player.clubName)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(playerPhotoModel)
                        .crossfade(true)
                        .build(),
                    contentDescription = player.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(listOf(Color.Transparent, Color(0xFF131D2E)))
                        )
                )

                // Rating Box (Top End)
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.TopEnd)
                        .background(Color(0xFF0B101B).copy(alpha = 0.85f), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFFF5A623).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${player.overallRating}", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color(0xFFFCD34D))
                        Text(text = "OVR", fontSize = 8.sp, color = Color(0xFFF5A623), fontWeight = FontWeight.Bold)
                    }
                }

                // Club Logo (Top Start)
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.TopStart)
                        .size(28.dp)
                        .background(Color(0xFF0B101B).copy(alpha = 0.85f), CircleShape)
                        .border(1.dp, Color(0xFFF5A623).copy(alpha = 0.5f), CircleShape)
                        .padding(3.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(clubLogoModel)
                            .crossfade(true)
                            .build(),
                        contentDescription = player.clubName,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = player.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1)
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFF5A623).copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(text = player.position, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF5A623))
                    }
                }

                Text(text = "${player.clubName} • ${player.nationality}", fontSize = 10.sp, color = TextSecondary, maxLines = 1)

                Spacer(modifier = Modifier.height(6.dp))

                // Stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    MiniStat("سرعة", "${player.pace}", Color(0xFF10B981))
                    MiniStat("تسديد", "${player.shooting}", Color(0xFFFCD34D))
                    MiniStat("مراوغة", "${player.dribbling}", Color(0xFF3B82F6))
                }
            }
        }
    }
}

@Composable
private fun MiniStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 8.sp, color = TextSecondary)
        Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

// --- 6. المدربون العالميون والتكتيكات ---
@Composable
private fun WorldCoachesSection(
    coaches: List<Coach>,
    onCoachClick: (Coach) -> Unit
) {
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "👔 المدربون العالميون والتكتيكات", fontSize = 17.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                Text(text = "خطط تكتيكية ومكافآت التناغم لجميع البطاقات", fontSize = 11.sp, color = TextSecondary)
            }
            Text(text = "${coaches.size} مدربين أساطير", fontSize = 11.sp, color = Color(0xFFF5A623), fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            for (i in 0 until coaches.size step 2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CoachTile(coach = coaches[i], modifier = Modifier.weight(1f), onClick = { onCoachClick(coaches[i]) })
                    if (i + 1 < coaches.size) {
                        CoachTile(coach = coaches[i + 1], modifier = Modifier.weight(1f), onClick = { onCoachClick(coaches[i + 1]) })
                    }
                }
            }
        }
    }
}

@Composable
private fun CoachTile(
    coach: Coach,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    Card(
        modifier = modifier
            .height(130.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131D2E)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22334A))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val coachPhotoModel = AppImageResolver.resolveCoachImage(coach.photoUrl, coach.id, coach.name)
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, Color(0xFFF5A623), CircleShape)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(coachPhotoModel)
                            .crossfade(true)
                            .build(),
                        contentDescription = coach.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = coach.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = coach.tacticalPhilosophy, fontSize = 10.sp, color = Color(0xFFF5A623), fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = coach.tacticBonus,
                fontSize = 10.sp,
                color = TextSecondary,
                lineHeight = 14.sp
            )
        }
    }
}

// --- 7. جميع أوضاع اللعب (All Game Modes + Filter Pills) ---
@Composable
private fun GameModesSection(
    modes: List<GameModeItem>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    onModeClick: (GameModeItem) -> Unit
) {
    val filters = listOf("الكل", "فردي", "ثنائي", "متعدد اللاعبين")

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "🎮 جميع أوضاع اللعب", fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                Text(text = "29 تجربة تنتظرك", fontSize = 11.sp, color = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Filter Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF171B27), RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            filters.forEach { filter ->
                val isSelected = selectedFilter == filter
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (isSelected) Color(0xFFF5A623) else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onFilterSelected(filter) }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = filter,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                        color = if (isSelected) Color.Black else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2-Column Grid for Modes
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            val filteredModes = modes.filter { selectedFilter == "الكل" || it.category == selectedFilter }
            for (i in 0 until filteredModes.size step 2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GameModeTile(mode = filteredModes[i], modifier = Modifier.weight(1f), onClick = { onModeClick(filteredModes[i]) })
                    if (i + 1 < filteredModes.size) {
                        GameModeTile(mode = filteredModes[i + 1], modifier = Modifier.weight(1f), onClick = { onModeClick(filteredModes[i + 1]) })
                    }
                }
            }
        }
    }
}

@Composable
private fun GameModeTile(
    mode: GameModeItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(86.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131D2E)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22334A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = mode.nameAr, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = mode.nameEn, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(mode.colorHex))
        }
    }
}

// --- 8. أحدث الأخبار ---
@Composable
private fun LatestNewsSection(newsList: List<NewsItem>) {
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "📰", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "أحدث الأخبار", fontSize = 17.sp, fontWeight = FontWeight.Black, color = TextPrimary)
            }
            Text(text = "عرض الكل", fontSize = 11.sp, color = Color(0xFFF5A623), fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            newsList.forEach { news ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131D2E)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22334A))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0B101B))
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(news.imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = news.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .background(Color.Black.copy(alpha = 0.8f))
                            ) {
                                Text(
                                    text = news.tag,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFCD34D),
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = news.category, fontSize = 9.sp, color = Color(0xFFF5A623), fontWeight = FontWeight.Bold)
                                Text(text = news.timeAgo, fontSize = 9.sp, color = TextSecondary)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = news.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1)
                            Text(text = news.summary, fontSize = 10.sp, color = TextSecondary, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
        }
    }
}

// --- 9. أصدقاؤك (Friends Online Strip) ---
@Composable
private fun FriendsOnlineSection(
    friends: List<FriendItem>,
    onOpenChat: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "👤", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "أصدقاؤك", fontSize = 17.sp, fontWeight = FontWeight.Black, color = TextPrimary)
            }
            Text(text = "عرض الكل", fontSize = 11.sp, color = Color(0xFFF5A623), fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(friends) { friend ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF252A36)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = friend.initial, fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFFFCD34D))
                        }
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    if (friend.status == "ONLINE") Color(0xFF10B981) else Color(0xFFF5A623),
                                    CircleShape
                                )
                                .border(1.5.dp, Color(0xFF0B101B), CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = friend.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = friend.statusText, fontSize = 9.sp, color = if (friend.status == "ONLINE") Color(0xFF10B981) else Color(0xFFF5A623))
                }
            }

            // Quick Add button
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onOpenChat() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF131D2E))
                            .border(1.dp, Color(0xFF22334A), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = TextPrimary)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "إضافة", fontSize = 11.sp, color = TextSecondary)
                }
            }
        }
    }
}

// --- 10. الترتيب العالمي (Global Leaderboard) ---
@Composable
private fun GlobalLeaderboardSection(
    leaderboard: List<LeaderboardUser>,
    currentUserXp: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131D2E)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22334A))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "🏆 الترتيب العالمي", fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                Text(text = "المتصدرون هذا الأسبوع", fontSize = 11.sp, color = Color(0xFFFCD34D))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                leaderboard.take(5).forEach { user ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E293B), RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "${user.rank}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFCD34D), modifier = Modifier.width(20.dp))
                            Text(text = user.badge, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(text = user.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(text = user.subtext, fontSize = 9.sp, color = TextSecondary)
                            }
                        }
                        Text(text = "${user.xp} XP", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color(0xFFFCD34D))
                    }
                }

                // Current User persistent row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF303541), RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0xFFF5A623), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "–", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF5A623), modifier = Modifier.width(20.dp))
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color(0xFFF5A623), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "A", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.Black)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "Amin_Abdullah (أنت)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFCD34D))
                            Text(text = "جاهز للصعود! العب لتتقدم", fontSize = 9.sp, color = TextSecondary)
                        }
                    }
                    Text(text = "$currentUserXp XP", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color(0xFFF5A623))
                }
            }
        }
    }
}

// --- 11. Footer Delight ---
@Composable
private fun FooterDelight() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Designed by ", fontSize = 11.sp, color = TextSecondary)
            Text(text = "Amin Abdullah", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFCD34D))
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = "OFFSIDE PRO • تجربة كرة قدم لا مثيل لها", fontSize = 10.sp, color = TextSecondary.copy(alpha = 0.7f))
    }
}
