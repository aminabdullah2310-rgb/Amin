package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Player
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import com.example.ui.theme.ElectricGold
import com.example.ui.theme.GoldGlow
import com.example.ui.theme.PitchGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PlayerCard(
    player: Player,
    modifier: Modifier = Modifier,
    onTrainClick: (() -> Unit)? = null,
    onToggleStartingClick: (() -> Unit)? = null,
    onActionClick: (() -> Unit)? = null,
    actionButtonText: String? = null
) {
    val cardBorderBrush = when (player.cardRarity) {
        "ICON" -> Brush.verticalGradient(listOf(Color(0xFFFFD700), Color(0xFFB8860B), Color(0xFFFFE082)))
        "FUTURE_STAR" -> Brush.verticalGradient(listOf(Color(0xFF00E5FF), Color(0xFF7C4DFF), Color(0xFFFF4081)))
        else -> Brush.verticalGradient(listOf(Color(0xFFE2C974), Color(0xFF9E8130), Color(0xFFF9E8A2)))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.5.dp, cardBorderBrush, RoundedCornerShape(16.dp))
            .testTag("player_card_${player.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(
                            DarkSurfaceElevated.copy(alpha = 0.95f),
                            DarkSurfaceCard
                        )
                    )
                )
                .padding(14.dp)
        ) {
            // Header Row: Rating, Position, Nationality, Starting status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Rating Badge
                    Box(
                        modifier = Modifier
                            .background(cardBorderBrush, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${player.overallRating}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Position Tag
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF1E293B), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = player.position,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PitchGreen
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = player.nationalityFlag, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = player.clubName,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Starting XI badge
                if (player.isStartingXI) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color(0xFF064E3B), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Starting XI",
                            tint = PitchGreen,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "أساسي", fontSize = 11.sp, color = PitchGreen, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Text(
                        text = "احتياط",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier
                            .background(Color(0xFF334155), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Player Avatar/Photo & Name Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val playerPhotoModel = com.example.util.AppImageResolver.resolvePlayerImage(player.photoUrl, player.id, player.name)
                val clubLogoModel = com.example.util.AppImageResolver.resolveClubLogo(player.clubLogoUrl, player.clubId, player.clubName)

                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.5.dp, cardBorderBrush, RoundedCornerShape(12.dp))
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
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.BottomEnd)
                            .background(Color.Black.copy(alpha = 0.85f), CircleShape)
                            .padding(2.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(clubLogoModel)
                                .crossfade(true)
                                .build(),
                            contentDescription = player.clubName,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = player.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "العمر: ${player.age} سنة | الإمكانات: ${player.potentialRating}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                // Offside Awareness Badge
                Box(
                    modifier = Modifier
                        .background(
                            if (player.offsideAwareness >= 85) Color(0xFF14532D) else Color(0xFF451A03),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🚩", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "تسلل: ${player.offsideAwareness}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (player.offsideAwareness >= 85) PitchGreen else ElectricGold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 6 Primary FC / PES Stats Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A), RoundedCornerShape(10.dp))
                    .padding(vertical = 8.dp, horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatBadge("سرعة", "${player.pace}")
                StatBadge("تسديد", "${player.shooting}")
                StatBadge("تمرير", "${player.passing}")
                StatBadge("مراوغة", "${player.dribbling}")
                StatBadge("دفاع", "${player.defending}")
                StatBadge("بدني", "${player.physical}")
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Energy & Training progress bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Energy",
                        tint = PitchGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "اللياقة: ${player.energy}%", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    LinearProgressIndicator(
                        progress = { player.energy / 100f },
                        modifier = Modifier
                            .height(5.dp)
                            .weight(1f)
                            .clip(RoundedCornerShape(3.dp)),
                        color = PitchGreen,
                        trackColor = Color(0xFF1E293B)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "القيمة: €${player.marketValue / 1_000_000}M",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElectricGold
                )
            }

            // Interactive Actions Bar
            if (onTrainClick != null || onToggleStartingClick != null || onActionClick != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (onToggleStartingClick != null) {
                        OutlinedButton(
                            onClick = onToggleStartingClick,
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("toggle_starting_${player.id}"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = PitchGreen
                            )
                        ) {
                            Text(
                                text = if (player.isStartingXI) "إلى الدكة" else "إلى التشكيلة الأساسية",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    if (onTrainClick != null) {
                        OutlinedButton(
                            onClick = onTrainClick,
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("train_player_${player.id}"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = ElectricGold
                            )
                        ) {
                            Icon(Icons.Default.FitnessCenter, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "تطوير", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (onActionClick != null && actionButtonText != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        androidx.compose.material3.Button(
                            onClick = onActionClick,
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("action_player_${player.id}"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PitchGreen)
                        ) {
                            Text(text = actionButtonText, fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBadge(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(text = label, fontSize = 10.sp, color = TextSecondary)
    }
}
