package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.CareerRepository
import com.example.ui.components.OffsideAnalyzerDialog
import com.example.ui.screens.DailyChallengesScreen
import com.example.ui.screens.GoogleSyncDialog
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MatchScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.SquadScreen
import com.example.ui.screens.TacticsAiScreen
import com.example.ui.screens.TransferMarketScreen
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.ElectricGold
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PitchDark
import com.example.ui.theme.PitchGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.NotificationHelper
import com.example.viewmodel.CareerViewModel
import com.example.viewmodel.NavigationTab

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Create Android notification channel for match and lineup alerts
        NotificationHelper.createNotificationChannel(this)

        setContent {
            MyApplicationTheme {
                MainAppContainer()
            }
        }
    }
}

@Composable
fun MainAppContainer() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val database = remember { AppDatabase.getDatabase(context, scope) }
    val repository = remember { CareerRepository(database.careerDao()) }
    val viewModel: CareerViewModel = viewModel { CareerViewModel(repository) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showSyncDialog by remember { mutableStateOf(false) }

    // Request Android 13+ Notification Permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("main_scaffold"),
        containerColor = PitchDark,
        bottomBar = {
            BottomNavBar(
                currentTab = uiState.currentTab,
                unreadNotificationsCount = uiState.notifications.count { !it.isRead },
                onTabSelected = { viewModel.selectTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.currentTab) {
                NavigationTab.HOME -> HomeScreen(
                    uiState = uiState,
                    onNavigateTab = { viewModel.selectTab(it) },
                    onStartMatchSimulation = { match ->
                        viewModel.selectTab(NavigationTab.SQUAD)
                        viewModel.simulateMatch(match, context)
                    },
                    onSyncCloud = { showSyncDialog = true },
                    onAddCoinsClick = { viewModel.addCoins(500) }
                )
                NavigationTab.SQUAD -> SquadScreen(
                    uiState = uiState,
                    onToggleStarting = { viewModel.toggleStartingXI(it) },
                    onTrainPlayer = { playerId, drill ->
                        viewModel.trainPlayer(playerId, drill)
                    },
                    onUpdateTactics = { tactic, defLine, offsideTrap, press ->
                        viewModel.updateTactics(tactic, defLine, offsideTrap, press)
                    }
                )
                NavigationTab.TRANSFERS -> TransferMarketScreen(
                    uiState = uiState,
                    onBuyPlayer = { player, fee, wage -> viewModel.buyPlayer(player, fee, wage) },
                    onSellPlayer = { player -> viewModel.sellPlayer(player) }
                )
                NavigationTab.TRAINING -> com.example.ui.screens.TrainingScreen(
                    uiState = uiState,
                    onTrainPlayer = { playerId, drill -> viewModel.trainPlayer(playerId, drill) }
                )
                NavigationTab.AI_ANALYST -> TacticsAiScreen(
                    uiState = uiState,
                    onSendMessage = { viewModel.sendTacticalQuestion(it) }
                )
                NavigationTab.CHALLENGES -> DailyChallengesScreen(
                    uiState = uiState,
                    onClaimChallenge = { viewModel.claimDailyChallenge(it) }
                )
                NavigationTab.NOTIFICATIONS -> NotificationsScreen(
                    uiState = uiState,
                    onMarkAllRead = { viewModel.markNotificationsRead() }
                )
            }

            // Gemini AI Smart Offside Analysis Modal Dialog
            if (uiState.activeOffsideReport != null || uiState.isAnalyzingWithGemini) {
                OffsideAnalyzerDialog(
                    reportText = uiState.activeOffsideReport,
                    isLoading = uiState.isAnalyzingWithGemini,
                    onDismiss = { viewModel.dismissOffsideReport() },
                    onApplyDrillRecommendation = {
                        viewModel.selectTab(NavigationTab.TRAINING)
                    }
                )
            }

            // Google Cloud Sync Modal Dialog
            if (showSyncDialog) {
                GoogleSyncDialog(
                    profile = uiState.profile,
                    isSyncing = uiState.isSyncingCloud,
                    syncMessage = uiState.cloudSyncMessage,
                    onDismiss = {
                        showSyncDialog = false
                        viewModel.dismissCloudSyncModal()
                    },
                    onTriggerSync = {
                        viewModel.syncCareerWithCloud(context)
                    }
                )
            }
        }
    }
}

data class NavItem(
    val tab: NavigationTab,
    val label: String,
    val icon: ImageVector
)

@Composable
fun BottomNavBar(
    currentTab: NavigationTab,
    unreadNotificationsCount: Int,
    onTabSelected: (NavigationTab) -> Unit
) {
    val items = listOf(
        NavItem(NavigationTab.HOME, "الرئيسية", Icons.Default.Home),
        NavItem(NavigationTab.SQUAD, "التشكيلة", Icons.Default.SportsSoccer),
        NavItem(NavigationTab.AI_ANALYST, "محلل الذكاء", Icons.Default.AutoAwesome),
        NavItem(NavigationTab.TRANSFERS, "انتقالات", Icons.Default.ShoppingBag),
        NavItem(NavigationTab.CHALLENGES, "تحديات", Icons.Default.EmojiEvents),
        NavItem(NavigationTab.NOTIFICATIONS, "تنبيهات", Icons.Default.Notifications)
    )

    NavigationBar(
        containerColor = DarkSurface,
        modifier = Modifier
            .height(68.dp)
            .testTag("bottom_nav_bar")
    ) {
        items.forEach { item ->
            val isSelected = currentTab == item.tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(item.tab) },
                icon = {
                    if (item.tab == NavigationTab.NOTIFICATIONS && unreadNotificationsCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = PitchGreen) {
                                    Text(text = "$unreadNotificationsCount", color = Color.Black)
                                }
                            }
                        ) {
                            Icon(item.icon, contentDescription = item.label, modifier = Modifier.size(22.dp))
                        }
                    } else {
                        Icon(
                            item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(22.dp),
                            tint = if (isSelected) PitchGreen else TextSecondary
                        )
                    }
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) PitchGreen else TextSecondary
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xFF0F172A)
                ),
                modifier = Modifier.testTag("nav_${item.tab.name.lowercase()}")
            )
        }
    }
}
