package com.example.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.Player
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.ElectricGold
import com.example.ui.theme.PitchGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.AppImageResolver

@Composable
fun TacticalPitchView(
    startingXI: List<Player>,
    defensiveLineHeight: Int,
    offsideTrapEnabled: Boolean,
    onDefensiveLineChange: (Int) -> Unit,
    onOffsideTrapToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("tactical_pitch_view"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "محاكاة خط التسلل والتشكيلة ⚽",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "تحكم في عمق الدفاع ومصيدة التسلل الذكية",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (offsideTrapEnabled) "مصيدة التسلل ON" else "OFF",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (offsideTrapEnabled) PitchGreen else TextSecondary
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Switch(
                        checked = offsideTrapEnabled,
                        onCheckedChange = onOffsideTrapToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = PitchGreen,
                            uncheckedThumbColor = Color.LightGray,
                            uncheckedTrackColor = Color.DarkGray
                        ),
                        modifier = Modifier.testTag("toggle_offside_trap")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // The Graphic Tactical Pitch
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(Color(0xFF143A1E), RoundedCornerShape(12.dp))
                    .border(2.dp, Color(0xFF22C55E).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            ) {
                // Pitch Lines and Grass Stripes Canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Alternating grass stripes
                    val stripeCount = 6
                    val stripeHeight = h / stripeCount
                    for (i in 0 until stripeCount) {
                        if (i % 2 == 0) {
                            drawRect(
                                color = Color(0xFF164724),
                                topLeft = Offset(0f, i * stripeHeight),
                                size = Size(w, stripeHeight)
                            )
                        }
                    }

                    // Outer border
                    drawRect(
                        color = Color.White.copy(alpha = 0.5f),
                        topLeft = Offset(12f, 12f),
                        size = Size(w - 24f, h - 24f),
                        style = Stroke(width = 3f)
                    )

                    // Halfway line
                    drawLine(
                        color = Color.White.copy(alpha = 0.5f),
                        start = Offset(12f, h / 2f),
                        end = Offset(w - 12f, h / 2f),
                        strokeWidth = 3f
                    )

                    // Center circle
                    drawCircle(
                        color = Color.White.copy(alpha = 0.5f),
                        radius = 42f,
                        center = Offset(w / 2f, h / 2f),
                        style = Stroke(width = 3f)
                    )

                    // Goal Areas (Top and Bottom)
                    drawRect(
                        color = Color.White.copy(alpha = 0.4f),
                        topLeft = Offset(w * 0.25f, 12f),
                        size = Size(w * 0.5f, h * 0.16f),
                        style = Stroke(width = 2.5f)
                    )
                    drawRect(
                        color = Color.White.copy(alpha = 0.4f),
                        topLeft = Offset(w * 0.25f, h - 12f - h * 0.16f),
                        size = Size(w * 0.5f, h * 0.16f),
                        style = Stroke(width = 2.5f)
                    )

                    // DYNAMIC DEFENSIVE OFFSIDE TRAP LINE
                    // Line height maps from bottom (own goal) towards middle/high line
                    val lineY = h - (h * (0.20f + (defensiveLineHeight / 100f) * 0.35f))
                    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 12f), 0f)

                    // Highlight Offside Zone
                    drawRect(
                        color = if (offsideTrapEnabled) Color(0xFFEF4444).copy(alpha = 0.18f) else Color.Transparent,
                        topLeft = Offset(12f, 12f),
                        size = Size(w - 24f, lineY - 12f)
                    )

                    // The Offside Line
                    drawLine(
                        color = if (offsideTrapEnabled) Color(0xFFFF3366) else Color(0xFFFFD700),
                        start = Offset(12f, lineY),
                        end = Offset(w - 12f, lineY),
                        strokeWidth = 4f,
                        pathEffect = if (offsideTrapEnabled) dashEffect else null
                    )
                }

                // Player Tokens on pitch
                val forwards = startingXI.filter { it.position in listOf("ST", "LW", "RW", "CF") }.take(3)
                val midfielders = startingXI.filter { it.position in listOf("CAM", "CM", "CDM") }.take(3)
                val defenders = startingXI.filter { it.position in listOf("CB", "LB", "RB") }.take(4)
                val goalkeeper = startingXI.firstOrNull { it.position == "GK" }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 14.dp, horizontal = 16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Forwards Row (Top)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        forwards.forEach { player ->
                            PitchPlayerToken(player, isAttacker = true)
                        }
                    }

                    // Midfielders Row (Center)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        midfielders.forEach { player ->
                            PitchPlayerToken(player, isAttacker = false)
                        }
                    }

                    // Defenders Row (Near Offside line)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        defenders.forEach { player ->
                            PitchPlayerToken(player, isAttacker = false, isDefender = true)
                        }
                    }

                    // Goalkeeper (Bottom)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (goalkeeper != null) {
                            PitchPlayerToken(goalkeeper, isAttacker = false)
                        }
                    }
                }

                // Offside Line Badge Indicator overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                        .background(Color(0xFF0F172A).copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFFFF3366), RoundedCornerShape(8.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "🚩 خط التسلل: $defensiveLineHeight%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF3366)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Defensive Line Height Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "عمق الخط الدفاعي (Defensive Line):",
                    fontSize = 13.sp,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = when {
                        defensiveLineHeight >= 75 -> "خط دفاع متقدم جداً (ضغط عالي)"
                        defensiveLineHeight >= 50 -> "خط دفاع متوازن"
                        else -> "دفاع منخفض (Drop Deep)"
                    },
                    fontSize = 12.sp,
                    color = PitchGreen,
                    fontWeight = FontWeight.Bold
                )
            }

            Slider(
                value = defensiveLineHeight.toFloat(),
                onValueChange = { onDefensiveLineChange(it.toInt()) },
                valueRange = 20f..85f,
                colors = SliderDefaults.colors(
                    thumbColor = PitchGreen,
                    activeTrackColor = PitchGreen,
                    inactiveTrackColor = Color(0xFF334155)
                ),
                modifier = Modifier.testTag("slider_defensive_line")
            )
        }
    }
}

@Composable
private fun PitchPlayerToken(player: Player, isAttacker: Boolean = false, isDefender: Boolean = false) {
    val photoModel = AppImageResolver.resolvePlayerImage(player.photoUrl, player.id, player.name)
    val ringColor = when {
        isDefender -> Color(0xFF3B82F6)
        isAttacker -> Color(0xFFEF4444)
        else -> Color(0xFF10B981)
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0F172A))
                    .border(2.dp, ringColor, CircleShape)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(photoModel)
                        .crossfade(true)
                        .build(),
                    contentDescription = player.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .background(ringColor, CircleShape)
                    .border(0.5.dp, Color.Black, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${player.overallRating}",
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }
        Text(
            text = player.name.split(" ").lastOrNull() ?: player.name,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            maxLines = 1
        )
    }
}
