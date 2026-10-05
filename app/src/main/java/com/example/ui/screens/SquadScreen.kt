package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.model.Player
import com.example.ui.components.PlayerCard
import com.example.ui.components.TacticalPitchView
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricGold
import com.example.ui.theme.GoldGlow
import com.example.ui.theme.PitchDark
import com.example.ui.theme.PitchGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.CareerUiState

enum class SquadTab {
    STARTING_XI,
    ALL_PLAYERS,
    DEVELOPMENT_STATS,
    TACTICS_OFFSIDE
}

@Composable
fun SquadScreen(
    uiState: CareerUiState,
    onToggleStarting: (String) -> Unit,
    onTrainPlayer: (String, String) -> Unit,
    onUpdateTactics: (String, Int, Boolean, Int) -> Unit
) {
    var currentSubTab by remember { mutableStateOf(SquadTab.STARTING_XI) }
    var selectedPositionFilter by remember { mutableStateOf("الكل") }
    var selectedPlayerForDevId by remember {
        mutableStateOf(uiState.squadPlayers.firstOrNull()?.id ?: "")
    }

    // Keep selected player synced
    val selectedPlayer = uiState.squadPlayers.find { it.id == selectedPlayerForDevId }
        ?: uiState.squadPlayers.firstOrNull()

    val startingXI = uiState.squadPlayers.filter { it.isStartingXI }
    val bench = uiState.squadPlayers.filter { !it.isStartingXI }
    val currentClub = uiState.myClub ?: uiState.allClubs.find { it.id == uiState.profile?.chosenClubId }

    val defensiveLine = uiState.profile?.defensiveLineHeight ?: 70
    val offsideTrap = uiState.profile?.offsideTrapEnabled ?: true
    val currentTactic = uiState.profile?.currentTactic ?: "Gegenpressing"
    val pressing = uiState.profile?.pressingIntensity ?: 75

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSurface)
            .padding(14.dp)
            .testTag("screen_squad"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Current Club Overview Header Card
        item {
            ClubOverviewHeader(
                club = currentClub,
                profile = uiState.profile,
                squadCount = uiState.squadPlayers.size,
                startingCount = startingXI.size,
                avgRating = if (startingXI.isNotEmpty()) startingXI.map { it.overallRating }.average().toInt() else 89,
                totalValue = uiState.squadPlayers.sumOf { it.marketValue }
            )
        }

        // 2. Navigation Tabs (Starting XI, All Players, Development Stats, Tactics)
        item {
            SquadSegmentedTabs(
                selectedTab = currentSubTab,
                onTabSelect = { currentSubTab = it }
            )
        }

        // 3. Tab Contents
        when (currentSubTab) {
            SquadTab.STARTING_XI -> {
                // Pitch View with Interactive Line
                item {
                    TacticalPitchView(
                        startingXI = startingXI,
                        defensiveLineHeight = defensiveLine,
                        offsideTrapEnabled = offsideTrap,
                        onDefensiveLineChange = { newLine ->
                            onUpdateTactics(currentTactic, newLine, offsideTrap, pressing)
                        },
                        onOffsideTrapToggle = { newTrap ->
                            onUpdateTactics(currentTactic, defensiveLine, newTrap, pressing)
                        }
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "التشكيلة الأساسية (${startingXI.size} / 11)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PitchGreen
                        )
                        Text(
                            text = "متوسط التقييم: ${if (startingXI.isNotEmpty()) startingXI.map { it.overallRating }.average().toInt() else 0} OVR",
                            fontSize = 12.sp,
                            color = ElectricGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                items(startingXI) { player ->
                    PlayerCard(
                        player = player,
                        onToggleStartingClick = { onToggleStarting(player.id) },
                        onTrainClick = {
                            selectedPlayerForDevId = player.id
                            currentSubTab = SquadTab.DEVELOPMENT_STATS
                        }
                    )
                }
            }

            SquadTab.ALL_PLAYERS -> {
                // Position Filter Bar
                item {
                    PositionFilterChips(
                        selected = selectedPositionFilter,
                        onSelect = { selectedPositionFilter = it }
                    )
                }

                val filteredPlayers = uiState.squadPlayers.filter { player ->
                    when (selectedPositionFilter) {
                        "هجوم" -> player.position in listOf("ST", "LW", "RW", "CF")
                        "وسط" -> player.position in listOf("CAM", "CM", "CDM", "LM", "RM")
                        "دفاع" -> player.position in listOf("CB", "LB", "RB", "LWB", "RWB")
                        "حراسة" -> player.position == "GK"
                        else -> true
                    }
                }

                item {
                    Text(
                        text = "إجمالي اللاعبين (${filteredPlayers.size})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                }

                items(filteredPlayers) { player ->
                    PlayerCard(
                        player = player,
                        onToggleStartingClick = { onToggleStarting(player.id) },
                        onTrainClick = {
                            selectedPlayerForDevId = player.id
                            currentSubTab = SquadTab.DEVELOPMENT_STATS
                        }
                    )
                }
            }

            SquadTab.DEVELOPMENT_STATS -> {
                // Player Selection Carousel
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "اختر لاعباً لعرض إحصائيات التطور والتدريب:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(uiState.squadPlayers) { p ->
                                val isSelected = p.id == selectedPlayer?.id
                                PlayerAvatarSelector(
                                    player = p,
                                    isSelected = isSelected,
                                    onClick = { selectedPlayerForDevId = p.id }
                                )
                            }
                        }
                    }
                }

                // Individual Player Development Statistics Card
                if (selectedPlayer != null) {
                    item {
                        PlayerDevelopmentDashboard(
                            player = selectedPlayer,
                            onExecuteDrill = { drillType ->
                                onTrainPlayer(selectedPlayer.id, drillType)
                            }
                        )
                    }
                }
            }

            SquadTab.TACTICS_OFFSIDE -> {
                item {
                    TacticsAndOffsideTrapControl(
                        currentTactic = currentTactic,
                        defensiveLine = defensiveLine,
                        offsideTrap = offsideTrap,
                        pressing = pressing,
                        onUpdateTactics = onUpdateTactics
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// --- 1. Current Club Overview Header ---
@Composable
private fun ClubOverviewHeader(
    club: Club?,
    profile: com.example.model.CareerProfile?,
    squadCount: Int,
    startingCount: Int,
    avgRating: Int,
    totalValue: Long
) {
    val context = LocalContext.current
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Club Crest
                    val clubLogoModel = com.example.util.AppImageResolver.resolveClubLogo(club?.logoUrl ?: "", club?.id ?: "", club?.name ?: "")
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0B101B))
                            .border(1.5.dp, Color(0xFFF5A623), CircleShape)
                            .padding(4.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(clubLogoModel)
                                .crossfade(true)
                                .build(),
                            contentDescription = club?.name,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = club?.name ?: profile?.chosenClubName ?: "Real Madrid",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFFF5A623), modifier = Modifier.size(16.dp))
                        }
                        Text(
                            text = "${club?.league ?: "La Liga"} • ${club?.stadiumName ?: "Santiago Bernabéu"}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Team Rating Badge
                Box(
                    modifier = Modifier
                        .background(Color(0xFFF5A623), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "$avgRating", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.Black)
                        Text(text = "OVR فريقي", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Squad Summary Stats Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0B101B), RoundedCornerShape(12.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "التشكيلة", fontSize = 10.sp, color = TextSecondary)
                    Text(text = "$startingCount / 11", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PitchGreen)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "إجمالي اللاعبين", fontSize = 10.sp, color = TextSecondary)
                    Text(text = "$squadCount", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "القيمة السوقية", fontSize = 10.sp, color = TextSecondary)
                    Text(text = "€${totalValue / 1_000_000}M", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color(0xFFFCD34D))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "تناغم الفريق", fontSize = 10.sp, color = TextSecondary)
                    Text(text = "96%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                }
            }
        }
    }
}

// --- 2. Segmented Navigation Tabs ---
@Composable
private fun SquadSegmentedTabs(
    selectedTab: SquadTab,
    onTabSelect: (SquadTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF171B27), RoundedCornerShape(12.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        SquadTabButton(
            title = "التشكيلة والملعب",
            isSelected = selectedTab == SquadTab.STARTING_XI,
            modifier = Modifier.weight(1f),
            onClick = { onTabSelect(SquadTab.STARTING_XI) }
        )
        SquadTabButton(
            title = "كل اللاعبين",
            isSelected = selectedTab == SquadTab.ALL_PLAYERS,
            modifier = Modifier.weight(1f),
            onClick = { onTabSelect(SquadTab.ALL_PLAYERS) }
        )
        SquadTabButton(
            title = "إحصائيات التطور",
            isSelected = selectedTab == SquadTab.DEVELOPMENT_STATS,
            modifier = Modifier.weight(1f),
            onClick = { onTabSelect(SquadTab.DEVELOPMENT_STATS) }
        )
        SquadTabButton(
            title = "مصيدة التسلل",
            isSelected = selectedTab == SquadTab.TACTICS_OFFSIDE,
            modifier = Modifier.weight(1f),
            onClick = { onTabSelect(SquadTab.TACTICS_OFFSIDE) }
        )
    }
}

@Composable
private fun SquadTabButton(
    title: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                if (isSelected) Color(0xFFF5A623) else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
            color = if (isSelected) Color.Black else TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// --- Position Filter Chips ---
@Composable
private fun PositionFilterChips(
    selected: String,
    onSelect: (String) -> Unit
) {
    val filters = listOf("الكل", "هجوم", "وسط", "دفاع", "حراسة")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { filter ->
            val isSelected = selected == filter
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (isSelected) Color(0xFFF5A623) else Color(0xFF131D2E),
                        RoundedCornerShape(10.dp)
                    )
                    .border(1.dp, if (isSelected) Color(0xFFF5A623) else Color(0xFF22334A), RoundedCornerShape(10.dp))
                    .clickable { onSelect(filter) }
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = filter,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.Black else TextPrimary
                )
            }
        }
    }
}

// --- Player Avatar Selector Item ---
@Composable
private fun PlayerAvatarSelector(
    player: Player,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            val playerPhotoModel = com.example.util.AppImageResolver.resolvePlayerImage(player.photoUrl, player.id, player.name)
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0F172A))
                    .border(
                        if (isSelected) 2.5.dp else 1.dp,
                        if (isSelected) Color(0xFFF5A623) else Color(0xFF334155),
                        CircleShape
                    )
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
            }

            // OVR Rating Badge
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(if (isSelected) Color(0xFFF5A623) else Color(0xFF1E293B), CircleShape)
                    .border(1.dp, Color(0xFF0B101B), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${player.overallRating}",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isSelected) Color.Black else Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = player.name.split(" ").lastOrNull() ?: player.name,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color(0xFFFCD34D) else TextSecondary,
            maxLines = 1
        )
    }
}

// --- 3. Individual Player Development Statistics Dashboard ---
@Composable
private fun PlayerDevelopmentDashboard(
    player: Player,
    onExecuteDrill: (String) -> Unit
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131D2E)),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFF5A623).copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Player Profile Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val playerPhotoModel = com.example.util.AppImageResolver.resolvePlayerImage(player.photoUrl, player.id, player.name)
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0B101B))
                        .border(2.dp, Color(0xFFF5A623), RoundedCornerShape(14.dp))
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
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = player.name, fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = player.nationalityFlag, fontSize = 16.sp)
                    }
                    Text(
                        text = "${player.position} | العمر: ${player.age} سنة | القيمة: €${player.marketValue / 1_000_000}M",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFF5A623).copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "التقييم: ${player.overallRating}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFCD34D)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF10B981).copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "الإمكانات: ${player.potentialRating}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Training Progress & Growth Potential Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0B101B), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "مستوى التطور نحو النقطة القادمة", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "${player.trainingXp} / ${player.trainingTargetXp} XP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFCD34D))
                }
                Spacer(modifier = Modifier.height(6.dp))
                val progress = if (player.trainingTargetXp > 0) (player.trainingXp.toFloat() / player.trainingTargetXp.toFloat()).coerceIn(0f, 1f) else 0f
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFFF5A623),
                    trackColor = Color(0xFF22334A)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "هامش التطور المتبقي للوصول للقمة: +${player.potentialRating - player.overallRating} نقاط",
                    fontSize = 10.sp,
                    color = Color(0xFF10B981)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Specialized Offside Awareness Metric (Key Highlight)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🚩", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "وعي التسلل وتوقيت الانطلاق", fontSize = 13.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                            Text(text = "التزامن مع خط الدفاع الأخير لتفادي الراية", fontSize = 10.sp, color = TextSecondary)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .background(if (player.offsideAwareness >= 85) Color(0xFF10B981) else Color(0xFFF5A623), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${player.offsideAwareness} / 99",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 6-Core Stats Progress Meters
            Text(text = "الإحصائيات الفردية الأساسية:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            StatMeterRow("السرعة (Pace)", player.pace, Color(0xFF10B981))
            StatMeterRow("التسديد (Shooting)", player.shooting, Color(0xFFFCD34D))
            StatMeterRow("التمرير (Passing)", player.passing, Color(0xFF3B82F6))
            StatMeterRow("المراوغة (Dribbling)", player.dribbling, Color(0xFF8B5CF6))
            StatMeterRow("الدفاع (Defending)", player.defending, Color(0xFF06B6D4))
            StatMeterRow("البدني (Physical)", player.physical, Color(0xFFF97316))

            Spacer(modifier = Modifier.height(16.dp))

            // Actionable Individual Training Drills
            Text(text = "برامج التدريب الفردي المتاحة:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))

            DrillItem(
                title = "تدريب كسر التسلل السريع",
                desc = "+2 وعي تسلل، +1 سرعة، +25 XP",
                icon = "🚩",
                onClick = { onExecuteDrill("OFFSIDE_AVOIDANCE") }
            )
            Spacer(modifier = Modifier.height(6.dp))
            DrillItem(
                title = "تدريب إنهاء الهجمات السريري",
                desc = "+1 تسديد، +25 XP",
                icon = "🎯",
                onClick = { onExecuteDrill("FINISHING") }
            )
            Spacer(modifier = Modifier.height(6.dp))
            DrillItem(
                title = "تدريب التمرير والرؤية والتيكي تاكا",
                desc = "+1 تمرير، +1 وعي تسلل، +25 XP",
                icon = "🪄",
                onClick = { onExecuteDrill("TIKI_TAKA_PASSING") }
            )
            Spacer(modifier = Modifier.height(6.dp))
            DrillItem(
                title = "تدريب تناغم مصيدة التسلل الدفاعية",
                desc = "+1 دفاع، +2 وعي تسلل، +25 XP",
                icon = "🛡️",
                onClick = { onExecuteDrill("DEFENSIVE_LINE_TRAP") }
            )
        }
    }
}

@Composable
private fun StatMeterRow(name: String, value: Int, color: Color) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = name, fontSize = 11.sp, color = TextSecondary)
            Text(text = "$value", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { (value / 99f).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = Color(0xFF22334A)
        )
    }
}

@Composable
private fun DrillItem(
    title: String,
    desc: String,
    icon: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E293B), RoundedCornerShape(10.dp))
            .border(1.dp, Color(0xFF22334A), RoundedCornerShape(10.dp))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(text = desc, fontSize = 9.sp, color = Color(0xFF10B981))
            }
        }
        Button(
            onClick = onClick,
            modifier = Modifier.height(32.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5A623))
        ) {
            Text(text = "تدريب", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.Black)
        }
    }
}

// --- 4. Tactics & Offside Trap Settings Tab ---
@Composable
private fun TacticsAndOffsideTrapControl(
    currentTactic: String,
    defensiveLine: Int,
    offsideTrap: Boolean,
    pressing: Int,
    onUpdateTactics: (String, Int, Boolean, Int) -> Unit
) {
    val tacticsList = listOf(
        "Gegenpressing" to "ضغط عالي ومصيدة متقدمة",
        "Tiki-Taka" to "استحواذ وتمرير قصير",
        "Counter-Attack" to "دفاع متأخر وهجمات مرتدة",
        "Catenaccio" to "تكتل حديدي دفاعي"
    )

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Tactic Selector
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131D2E)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22334A))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(text = "الفلسفة التكتيكية العامة:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(tacticsList) { (tactic, desc) ->
                        val isSelected = currentTactic == tactic
                        Box(
                            modifier = Modifier
                                .background(if (isSelected) Color(0xFFF5A623) else Color(0xFF1E293B), RoundedCornerShape(10.dp))
                                .border(1.dp, if (isSelected) Color(0xFFF5A623) else Color(0xFF334155), RoundedCornerShape(10.dp))
                                .clickable { onUpdateTactics(tactic, defensiveLine, offsideTrap, pressing) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Column {
                                Text(
                                    text = tactic,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else TextPrimary
                                )
                                Text(
                                    text = desc,
                                    fontSize = 9.sp,
                                    color = if (isSelected) Color.Black.copy(alpha = 0.8f) else TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Defensive Line Depth Control
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131D2E)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22334A))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "عمق خط الدفاع الأخير:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "$defensiveLine%", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color(0xFFFCD34D))
                }
                Spacer(modifier = Modifier.height(6.dp))
                Slider(
                    value = defensiveLine.toFloat(),
                    onValueChange = { onUpdateTactics(currentTactic, it.toInt(), offsideTrap, pressing) },
                    valueRange = 20f..90f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFF5A623),
                        activeTrackColor = Color(0xFFF5A623),
                        inactiveTrackColor = Color(0xFF22334A)
                    )
                )
                Text(
                    text = if (defensiveLine > 70) "ضغط عالي متقدم (يزيد فاعلية مصيدة التسلل ويزيد احتمالية المرتدات)"
                    else if (defensiveLine < 40) "دفاع متكتل متأخر (تقليل المساحات وندرة حالات التسلل)"
                    else "خط دفاع متوازن",
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            }
        }

        // Offside Trap Switch
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131D2E)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22334A))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "مفتاح مصيدة التسلل (Offside Trap):", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(
                        text = if (offsideTrap) "مفعلة: يتحرك المدافعون بتزامن لصيد المهاجمين" else "معطلة: تراجع فردي ومراقبة لصيقة",
                        fontSize = 10.sp,
                        color = if (offsideTrap) PitchGreen else TextSecondary
                    )
                }
                Switch(
                    checked = offsideTrap,
                    onCheckedChange = { onUpdateTactics(currentTactic, defensiveLine, it, pressing) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = Color(0xFFF5A623)
                    )
                )
            }
        }
    }
}
