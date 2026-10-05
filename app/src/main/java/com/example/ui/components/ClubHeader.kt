package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.model.CareerProfile
import com.example.model.Club
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.ElectricGold
import com.example.ui.theme.PitchGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.AppImageResolver

@Composable
fun ClubHeader(
    profile: CareerProfile?,
    club: Club?,
    onSyncClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("club_header_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            // Background Stadium Graphic
            Image(
                painter = painterResource(id = R.drawable.career_banner_1791217224747),
                contentDescription = "Stadium Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient Overlay for readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.35f),
                                Color(0xFF080E17).copy(alpha = 0.95f)
                            )
                        )
                    )
            )

            // Content Overlay
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Row: Club name, Season/Week & Cloud Sync Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val clubLogoModel = AppImageResolver.resolveClubLogo(club?.logoUrl ?: "", club?.id ?: "", club?.name ?: "")
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                .border(1.5.dp, ElectricGold, CircleShape)
                                .padding(3.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(clubLogoModel)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = club?.name,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = club?.name ?: "Real Madrid",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            Text(
                                text = "الموسم ${profile?.seasonYear ?: 2026} • الأسبوع ${profile?.currentWeek ?: 1}",
                                fontSize = 11.sp,
                                color = PitchGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Cloud Sync Button
                    Row(
                        modifier = Modifier
                            .background(
                                if (profile?.isCloudSynced == true) Color(0xFF064E3B) else Color(0xFF1E293B),
                                RoundedCornerShape(12.dp)
                            )
                            .border(1.dp, PitchGreen.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                            .clickable { onSyncClick() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("cloud_sync_button"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (profile?.isCloudSynced == true) Icons.Default.CloudDone else Icons.Default.CloudSync,
                            contentDescription = "Cloud Sync",
                            tint = PitchGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (profile?.isCloudSynced == true) "Google Cloud" else "مزامنة سحابية",
                            fontSize = 11.sp,
                            color = PitchGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Bottom Row: Manager Avatar, Name, and Budgets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Manager portrait
                        Image(
                            painter = painterResource(id = R.drawable.manager_avatar_1791217321226),
                            contentDescription = "Manager Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .border(2.dp, ElectricGold, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = profile?.managerName ?: "Amin Abdullah",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = ElectricGold,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "سمعة المدرب: ${profile?.managerReputation ?: 88}%",
                                    fontSize = 11.sp,
                                    color = ElectricGold,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Budgets pill
                    Column(horizontalAlignment = Alignment.End) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = ElectricGold, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "€${(profile?.transferBudget ?: 180_000_000L) / 1_000_000}M",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = ElectricGold
                            )
                        }
                        Text(
                            text = "🪙 ${(profile?.totalCoins ?: 50_000L)} كوينز",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
