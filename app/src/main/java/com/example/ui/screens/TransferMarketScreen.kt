package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.Player
import com.example.ui.components.PlayerCard
import com.example.util.AppImageResolver
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.ElectricGold
import com.example.ui.theme.PitchGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.CareerUiState

@Composable
fun TransferMarketScreen(
    uiState: CareerUiState,
    onBuyPlayer: (Player, Long, Long) -> Unit,
    onSellPlayer: (Player) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedPositionFilter by remember { mutableStateOf("ALL") }
    var selectedTab by remember { mutableStateOf(0) } // 0: Buy, 1: Sell
    var negotiatingPlayer by remember { mutableStateOf<Player?>(null) }

    val positionFilters = listOf("ALL", "ST", "RW", "LW", "CAM", "CM", "CDM", "CB", "RB", "LB", "GK")

    val filteredMarket = uiState.transferMarketPlayers.filter { player ->
        val matchesSearch = player.name.contains(searchQuery, ignoreCase = true) || player.clubName.contains(searchQuery, ignoreCase = true)
        val matchesPos = selectedPositionFilter == "ALL" || player.position == selectedPositionFilter
        matchesSearch && matchesPos
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSurface)
            .padding(16.dp)
            .testTag("screen_transfers"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "💰 سوق الانتقالات الرسمي (FC Transfer Hub)",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
            Text(
                text = "تفاوض على صفقات النجوم الكبرى وإدارة ميزانية النادي",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        // Budget Dashboard Bar
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "ميزانية الانتقالات المتاحة:", fontSize = 11.sp, color = TextSecondary)
                        Text(
                            text = "€${(uiState.profile?.transferBudget ?: 0L) / 1_000_000} مليون يورو",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = ElectricGold
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "سقف الرواتب الأسبوعي:", fontSize = 11.sp, color = TextSecondary)
                        Text(
                            text = "€${(uiState.profile?.weeklyWageBudget ?: 0L) / 1_000} ألف / أسبوع",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = PitchGreen
                        )
                    }
                }
            }
        }

        // Buy / Sell Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF0F172A),
                contentColor = PitchGreen,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = PitchGreen
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("تعاقدات جديدة (شراء)", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("بيع لاعبي الفريق", fontWeight = FontWeight.Bold) }
                )
            }
        }

        if (selectedTab == 0) {
            // Search Input
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("ابحث عن لاعب أو نادي...", color = TextSecondary) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PitchGreen) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("transfer_search_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurfaceCard,
                        unfocusedContainerColor = DarkSurfaceCard,
                        focusedBorderColor = PitchGreen,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )
            }

            // Position Filter Chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(positionFilters) { pos ->
                        val isSelected = selectedPositionFilter == pos
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isSelected) PitchGreen else Color(0xFF1E293B),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedPositionFilter = pos }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = pos,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.Black else TextPrimary
                            )
                        }
                    }
                }
            }

            // Market Players List
            items(filteredMarket) { player ->
                PlayerCard(
                    player = player,
                    onActionClick = { negotiatingPlayer = player },
                    actionButtonText = "تقديم عرض وشراء"
                )
            }
        } else {
            // Sell Your Squad Players
            item {
                Text(
                    text = "اختر لاعباً لبيعه والحصول على 95% من قيمته السوقية فوراً:",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }

            items(uiState.squadPlayers) { player ->
                PlayerCard(
                    player = player,
                    onActionClick = { onSellPlayer(player) },
                    actionButtonText = "بيع (€${(player.marketValue * 0.95 / 1_000_000).toInt()}M)"
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Negotiation Modal Dialog
    negotiatingPlayer?.let { player ->
        NegotiationDialog(
            player = player,
            userBudget = uiState.profile?.transferBudget ?: 0L,
            onDismiss = { negotiatingPlayer = null },
            onConfirmDeal = { fee, wage ->
                onBuyPlayer(player, fee, wage)
                negotiatingPlayer = null
            }
        )
    }
}

@Composable
private fun NegotiationDialog(
    player: Player,
    userBudget: Long,
    onDismiss: () -> Unit,
    onConfirmDeal: (Long, Long) -> Unit
) {
    var offerFeeMillions by remember { mutableStateOf((player.marketValue / 1_000_000).toString()) }
    var offerWageThousands by remember { mutableStateOf((player.weeklyWage / 1_000).toString()) }
    val totalFee = (offerFeeMillions.toLongOrNull() ?: 0L) * 1_000_000L
    val totalWage = (offerWageThousands.toLongOrNull() ?: 0L) * 1_000L
    val canAfford = userBudget >= totalFee

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("dialog_transfer_negotiation"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, PitchGreen)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "مفاوضات التعاقد مع ${player.name}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                val playerPhotoModel = AppImageResolver.resolvePlayerImage(player.photoUrl, player.id, player.name)
                val clubLogoModel = AppImageResolver.resolveClubLogo(player.clubLogoUrl, player.clubId, player.clubName)

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F172A))
                            .padding(2.dp)
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
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "النادي الحالي: ${player.clubName} • المركز: ${player.position}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "التقييم العام: ${player.overallRating} OVR | التسلل: ${player.offsideAwareness}",
                            fontSize = 12.sp,
                            color = PitchGreen,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "القيمة السوقية: €${player.marketValue / 1_000_000}M",
                            fontSize = 12.sp,
                            color = ElectricGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Offer Input
                Text(text = "عرض قيمة الصفقة (بالملايين €):", fontSize = 12.sp, color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = offerFeeMillions,
                    onValueChange = { offerFeeMillions = it.filter { ch -> ch.isDigit() } },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Wage Input
                Text(text = "الراتب الأسبوعي المقترح (بالآلاف €):", fontSize = 12.sp, color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = offerWageThousands,
                    onValueChange = { offerWageThousands = it.filter { ch -> ch.isDigit() } },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (!canAfford) {
                    Text(
                        text = "⚠️ ميزانيتك الحالية لا تكفي لإتمام هذه الصفقة!",
                        fontSize = 12.sp,
                        color = Color(0xFFFF3366)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = {
                        if (canAfford && totalFee > 0) {
                            onConfirmDeal(totalFee, totalWage)
                        }
                    },
                    enabled = canAfford && totalFee > 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("submit_transfer_bid_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PitchGreen)
                ) {
                    Text(text = "حسم الصفقة وتوقيع العقد ✍️", color = Color.Black, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}
