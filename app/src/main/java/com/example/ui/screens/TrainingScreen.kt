package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.Player
import com.example.util.AppImageResolver
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricGold
import com.example.ui.theme.PitchGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.CareerUiState

data class TrainingDrill(
    val id: String,
    val name: String,
    val icon: String,
    val description: String,
    val statBoostDescription: String
)

@Composable
fun TrainingScreen(
    uiState: CareerUiState,
    onTrainPlayer: (String, String) -> Unit
) {
    val drills = listOf(
        TrainingDrill(
            "OFFSIDE_AVOIDANCE",
            "توقيت الجري وكسر التسلل 🚩",
            "⚡",
            "تدريب مكثف على المزامنة البصرية مع حامل الكرة لتفادي راية التسلل بدقة أجزاء الثانية.",
            "+2 وعي التسلل • +1 السرعة"
        ),
        TrainingDrill(
            "FINISHING",
            "إنهاء الهجمات السريري ⚽",
            "🎯",
            "التسديد المتقن من داخل وخارج منطقة الجزاء في وضعيات 1 ضد 1 مع الحارس.",
            "+1 التسديد • +1 القوة البدنية"
        ),
        TrainingDrill(
            "TIKI_TAKA_PASSING",
            "التمرير السريع البيني 📐",
            "🔄",
            "تمريرات كاسرة للخطوط من لمسة واحدة مع فتح زوايا التمرير العمودية.",
            "+1 التمرير • +1 وعي التسلل"
        ),
        TrainingDrill(
            "DEFENSIVE_LINE_TRAP",
            "تناغم مصيدة التسلل الدفاعية 🛡️",
            "🏰",
            "تدريب خط الدفاع الرباعي على التقدم المتزامن بخطوة واحدة عند تمريرة الخصم.",
            "+1 الدفاع • +2 وعي التسلل الدفاعي"
        )
    )

    var selectedDrill by remember { mutableStateOf(drills[0]) }
    var selectedPlayer by remember { mutableStateOf<Player?>(uiState.squadPlayers.firstOrNull()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSurface)
            .padding(16.dp)
            .testTag("screen_training"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "⚡ مركز تطوير اللاعبين ومصيدة التسلل",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
            Text(
                text = "طوّر مواهب الفريق الشابة وحسّن إتقان التسلل لزيادة تقييماتهم الإجمالية",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        // Active Drills Selection
        item {
            Text(text = "1. اختر الحصة التدريبية:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PitchGreen)
        }

        items(drills) { drill ->
            val isSelected = selectedDrill.id == drill.id
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedDrill = drill }
                    .border(
                        1.5.dp,
                        if (isSelected) PitchGreen else Color.Transparent,
                        RoundedCornerShape(14.dp)
                    )
                    .testTag("drill_${drill.id}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFF064E3B).copy(alpha = 0.6f) else DarkSurfaceCard)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = drill.icon, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = drill.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(text = drill.description, fontSize = 11.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = drill.statBoostDescription, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElectricGold)
                    }
                }
            }
        }

        // Player Selection Section
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "2. اختر اللاعب المراد تطويره:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PitchGreen)
        }

        items(uiState.squadPlayers.take(8)) { player ->
            val isSelected = selectedPlayer?.id == player.id
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedPlayer = player }
                    .border(
                        1.5.dp,
                        if (isSelected) ElectricGold else Color.Transparent,
                        RoundedCornerShape(14.dp)
                    )
                    .testTag("select_player_${player.id}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFF451A03).copy(alpha = 0.6f) else DarkSurfaceCard)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val playerPhotoModel = AppImageResolver.resolvePlayerImage(player.photoUrl, player.id, player.name)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF1E293B))
                                .border(1.5.dp, if (isSelected) ElectricGold else Color(0xFF334155), RoundedCornerShape(10.dp))
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(playerPhotoModel)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = player.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "${player.name} (${player.position})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${player.overallRating}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ElectricGold
                                )
                            }
                            Text(text = "إمكانات التطوير: حتى ${player.potentialRating} • التسلل: ${player.offsideAwareness}", fontSize = 11.sp, color = TextSecondary)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "XP ${player.trainingXp}/${player.trainingTargetXp}", fontSize = 11.sp, color = PitchGreen)
                        LinearProgressIndicator(
                            progress = { (player.trainingXp.toFloat() / player.trainingTargetXp).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .width(70.dp)
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = PitchGreen,
                            trackColor = Color(0xFF334155)
                        )
                    }
                }
            }
        }

        // Execute Training Button
        item {
            selectedPlayer?.let { player ->
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { onTrainPlayer(player.id, selectedDrill.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("execute_training_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PitchGreen)
                ) {
                    Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تنفيذ الحصة التدريبية لـ ${player.name} ⚡",
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
