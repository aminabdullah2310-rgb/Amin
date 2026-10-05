package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.MatchRecord
import com.example.util.AppImageResolver
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricGold
import com.example.ui.theme.PitchGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.CareerUiState

@Composable
fun MatchScreen(
    uiState: CareerUiState,
    onStartSimulation: (MatchRecord) -> Unit,
    onOpenAiAnalysis: (MatchRecord) -> Unit
) {
    val context = LocalContext.current
    val liveState = uiState.liveMatchState
    val nextMatch = uiState.allMatches.firstOrNull { !it.isFinished } ?: uiState.allMatches.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSurface)
            .padding(16.dp)
            .testTag("screen_match"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "🏟️ مركز المباريات والأوفسايد المباشر",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
            Text(
                text = "محاكاة واقعية للمباريات مع كشف التسلل اللحظي وتحليل الذكاء الاصطناعي",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        // Live Match Simulation Card
        if (liveState != null) {
            item {
                LiveScoreboardCard(
                    liveState = liveState,
                    isAnalyzing = uiState.isAnalyzingWithGemini,
                    onOpenAiReport = { onOpenAiAnalysis(liveState.match) }
                )
            }

            // Offside Call Alert Banner
            item {
                AnimatedVisibility(
                    visible = liveState.isOffsideCallActive,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF7F1D1D), RoundedCornerShape(12.dp))
                            .border(1.5.dp, Color(0xFFEF4444), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                            .testTag("offside_call_banner"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🚩", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = liveState.offsideBannerText.ifEmpty { "قرار تسلل! راية الحكم تعلن إيقاف الهجمة!" },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Live Match Events Ticker
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "⏱️ شريط أحداث المباراة المباشرة",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PitchGreen
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        if (liveState.eventsList.isEmpty()) {
                            Text(text = "المباراة قيد الانطلاق والتكتيك يتشكل...", fontSize = 12.sp, color = TextSecondary)
                        } else {
                            liveState.eventsList.reversed().take(6).forEach { event ->
                                Text(
                                    text = event,
                                    fontSize = 13.sp,
                                    color = if (event.contains("جوووول") || event.contains("هدف")) ElectricGold else if (event.contains("تسلل")) Color(0xFFFF6B6B) else TextPrimary,
                                    modifier = Modifier.padding(vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        } else if (nextMatch != null) {
            // Pre-Match Overview
            item {
                PreMatchSquadLineupCard(
                    match = nextMatch,
                    squad = uiState.squadPlayers.filter { it.isStartingXI },
                    onStartMatch = { onStartSimulation(nextMatch) }
                )
            }
        }

        // Match Stats Comparison
        if (liveState != null && liveState.currentMinute > 0) {
            item {
                MatchStatsCard(
                    homeTeam = liveState.match.homeClubName,
                    awayTeam = liveState.match.awayClubName,
                    possessionHome = liveState.match.possessionHome,
                    shotsHome = liveState.match.shotsHome,
                    shotsAway = liveState.match.shotsAway,
                    offsidesHome = liveState.match.offsidesHome,
                    offsidesAway = liveState.match.offsidesAway
                )
            }
        }

        // Gemini AI Smart Offside Analysis Card
        if (liveState?.isComplete == true) {
            item {
                SmartOffsidePromoCard(
                    isAnalyzing = uiState.isAnalyzingWithGemini,
                    onOpenReport = { onOpenAiAnalysis(liveState.match) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun LiveScoreboardCard(
    liveState: com.example.viewmodel.LiveMatchState,
    isAnalyzing: Boolean,
    onOpenAiReport: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("live_scoreboard_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, PitchGreen.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Live Status Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(if (liveState.isComplete) Color.Gray else Color.Red, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (liveState.isComplete) "انتهت المباراة" else "مباشر • د ${liveState.currentMinute}'",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (liveState.isComplete) TextSecondary else Color.Red
                    )
                }

                Text(
                    text = "مصيدة التسلل: نشطة 🚩",
                    fontSize = 11.sp,
                    color = PitchGreen,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Big Scoreboard
            val homeCrest = AppImageResolver.resolveClubLogo("", liveState.match.homeClubId, liveState.match.homeClubName)
            val awayCrest = AppImageResolver.resolveClubLogo("", liveState.match.awayClubId, liveState.match.awayClubName)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home Club
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0B101B))
                            .border(1.5.dp, Color(0xFFF5A623), CircleShape)
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(homeCrest)
                                .crossfade(true)
                                .build(),
                            contentDescription = liveState.match.homeClubName,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = liveState.match.homeClubName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                // Scores
                Text(
                    text = "${liveState.homeScore}  -  ${liveState.awayScore}",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    color = ElectricGold
                )

                // Away Club
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0B101B))
                            .border(1.5.dp, Color(0xFF3B82F6), CircleShape)
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(awayCrest)
                                .crossfade(true)
                                .build(),
                            contentDescription = liveState.match.awayClubName,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = liveState.match.awayClubName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Minute Progress
            LinearProgressIndicator(
                progress = { (liveState.currentMinute / 90f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = PitchGreen,
                trackColor = Color(0xFF1E293B)
            )

            if (liveState.isComplete) {
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onOpenAiReport,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("view_ai_report_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PitchGreen)
                ) {
                    if (isAnalyzing) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Gemini يحلل مصيدة التسلل...", color = Color.Black, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "عرض تقرير محلل الأوفسايد الذكي", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun PreMatchSquadLineupCard(
    match: MatchRecord,
    squad: List<com.example.model.Player>,
    onStartMatch: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val homeCrest = AppImageResolver.resolveClubLogo("", match.homeClubId, match.homeClubName)
            val awayCrest = AppImageResolver.resolveClubLogo("", match.awayClubId, match.awayClubName)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "📋 التشكيلة المؤكدة والتكتيك المقترح",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${match.homeClubName} ضد ${match.awayClubName} • خطة ${match.homeFormation}",
                        fontSize = 12.sp,
                        color = PitchGreen
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0B101B))
                            .padding(2.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(homeCrest)
                                .crossfade(true)
                                .build(),
                            contentDescription = match.homeClubName,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "VS", fontSize = 11.sp, fontWeight = FontWeight.Black, color = ElectricGold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0B101B))
                            .padding(2.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(awayCrest)
                                .crossfade(true)
                                .build(),
                            contentDescription = match.awayClubName,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Starters List preview
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                squad.take(5).forEach { player ->
                    val playerPhoto = AppImageResolver.resolvePlayerImage(player.photoUrl, player.id, player.name)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF1E293B))
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(playerPhoto)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = player.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = player.position,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PitchGreen,
                                modifier = Modifier.width(32.dp)
                            )
                            Text(text = player.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Text(text = "🚩 تسلل: ${player.offsideAwareness}", fontSize = 11.sp, color = ElectricGold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onStartMatch,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("launch_simulation_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PitchGreen)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "انطلاق صافرة المباراة", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun MatchStatsCard(
    homeTeam: String,
    awayTeam: String,
    possessionHome: Int,
    shotsHome: Int,
    shotsAway: Int,
    offsidesHome: Int,
    offsidesAway: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "📊 إحصائيات الأداء ومصيدة التسلل",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            StatRow("الاستحواذ", "$possessionHome%", "${100 - possessionHome}%")
            StatRow("إجمالي التسديدات", "$shotsHome", "$shotsAway")
            StatRow("حالات التسلل المكتشفة 🚩", "$offsidesHome", "$offsidesAway", isHighlighted = true)
        }
    }
}

@Composable
private fun StatRow(title: String, homeVal: String, awayVal: String, isHighlighted: Boolean = false) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = homeVal, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (isHighlighted) PitchGreen else TextPrimary)
            Text(text = title, fontSize = 12.sp, color = TextSecondary)
            Text(text = awayVal, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (isHighlighted) Color(0xFFFF6B6B) else TextPrimary)
        }
        Spacer(modifier = Modifier.height(2.dp))
        LinearProgressIndicator(
            progress = { 0.5f },
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = if (isHighlighted) PitchGreen else ElectricGold,
            trackColor = Color(0xFF1E293B)
        )
    }
}

@Composable
private fun SmartOffsidePromoCard(
    isAnalyzing: Boolean,
    onOpenReport: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("smart_offside_promo_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B)),
        border = androidx.compose.foundation.BorderStroke(1.dp, PitchGreen)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "محلل الأوفسايد الذكي", fontSize = 15.sp, fontWeight = FontWeight.Black, color = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ElectricGold, modifier = Modifier.size(15.dp))
                }
                Text(
                    text = "تقرير تكتيكي فوري لضبط توقيت خط الدفاع وكشف أخطاء التسلل مع نصائح المدرب.",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }

            Button(
                onClick = onOpenReport,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricGold),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "قراءة ➔", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}
