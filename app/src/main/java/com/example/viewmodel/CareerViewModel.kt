package com.example.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.AuthManager
import com.example.data.CareerRepository
import com.example.gemini.GeminiService
import com.example.model.CareerProfile
import com.example.model.Club
import com.example.model.DailyChallenge
import com.example.model.MatchRecord
import com.example.model.NotificationAlert
import com.example.model.Player
import com.example.util.NotificationHelper
import com.example.util.SoundManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class ChatMessage(
    val sender: String, // "MANAGER" or "AI_ANALYST"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class LiveMatchState(
    val match: MatchRecord,
    val currentMinute: Int = 0,
    val homeScore: Int = 0,
    val awayScore: Int = 0,
    val currentEventText: String = "انطلاق المباراة!",
    val isOffsideCallActive: Boolean = false,
    val offsideBannerText: String = "",
    val eventsList: List<String> = emptyList(),
    val isComplete: Boolean = false
)

enum class NavigationTab {
    HOME,
    SQUAD,
    TRANSFERS,
    TRAINING,
    AI_ANALYST,
    CHALLENGES,
    NOTIFICATIONS
}

data class CareerUiState(
    val profile: CareerProfile? = null,
    val myClub: Club? = null,
    val squadPlayers: List<Player> = emptyList(),
    val transferMarketPlayers: List<Player> = emptyList(),
    val allClubs: List<Club> = emptyList(),
    val allCoaches: List<com.example.model.Coach> = emptyList(),
    val newsList: List<com.example.model.NewsItem> = com.example.data.InitialData.newsList,
    val friendsList: List<com.example.model.FriendItem> = com.example.data.InitialData.friends,
    val leaderboardList: List<com.example.model.LeaderboardUser> = com.example.data.InitialData.leaderboard,
    val gameModesList: List<com.example.model.GameModeItem> = com.example.data.InitialData.gameModes,
    val activeGameModeFilter: String = "الكل",
    val allMatches: List<MatchRecord> = emptyList(),
    val dailyChallenges: List<DailyChallenge> = emptyList(),
    val notifications: List<NotificationAlert> = emptyList(),
    val currentTab: NavigationTab = NavigationTab.HOME,
    val liveMatchState: LiveMatchState? = null,
    val isSimulating: Boolean = false,
    val isAnalyzingWithGemini: Boolean = false,
    val activeOffsideReport: String? = null,
    val chatMessages: List<ChatMessage> = listOf(
        ChatMessage("AI_ANALYST", "مرحباً بك يا كابتن! أنا محلل الأوفسايد والتكتيك الذكي المدعوم بـ Gemini AI. يمكنك سؤالي عن أي تفصيل تكتيكي، أو مراجعة مصيدة التسلل وخطط مواجهة الخصوم.")
    ),
    val isChatLoading: Boolean = false,
    val isSyncingCloud: Boolean = false,
    val cloudSyncMessage: String? = null
)

class CareerViewModel(
    private val repository: CareerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CareerUiState())
    val uiState: StateFlow<CareerUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureInitialized()
        }

        viewModelScope.launch {
            repository.careerProfile.collect { profile ->
                _uiState.update { it.copy(profile = profile) }
                if (profile != null) {
                    loadClubData(profile.chosenClubId)
                }
            }
        }

        viewModelScope.launch {
            repository.allClubs.collect { clubs ->
                _uiState.update { it.copy(allClubs = clubs) }
            }
        }

        viewModelScope.launch {
            repository.allCoaches.collect { coaches ->
                _uiState.update { it.copy(allCoaches = coaches) }
            }
        }

        viewModelScope.launch {
            repository.allMatches.collect { matches ->
                _uiState.update { it.copy(allMatches = matches) }
            }
        }

        viewModelScope.launch {
            repository.dailyChallenges.collect { challenges ->
                _uiState.update { it.copy(dailyChallenges = challenges) }
            }
        }

        viewModelScope.launch {
            repository.notifications.collect { notifs ->
                _uiState.update { it.copy(notifications = notifs) }
            }
        }
    }

    fun setGameModeFilter(filter: String) {
        SoundManager.playClick()
        _uiState.update { it.copy(activeGameModeFilter = filter) }
    }

    fun addCoins(amount: Long) {
        val current = _uiState.value.profile ?: return
        viewModelScope.launch {
            SoundManager.playGoalCheer()
            repository.updateProfile(current.copy(totalCoins = current.totalCoins + amount))
        }
    }

    private fun loadClubData(clubId: String) {
        viewModelScope.launch {
            repository.getClubPlayers(clubId).collect { players ->
                _uiState.update { it.copy(squadPlayers = players) }
            }
        }
        viewModelScope.launch {
            repository.getTransferMarket(clubId).collect { market ->
                _uiState.update { it.copy(transferMarketPlayers = market) }
            }
        }
        viewModelScope.launch {
            repository.allClubs.collect { clubs ->
                val myClub = clubs.find { it.id == clubId }
                _uiState.update { it.copy(myClub = myClub) }
            }
        }
    }

    fun selectTab(tab: NavigationTab) {
        SoundManager.playClick()
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun updateTactics(tactic: String, defensiveLine: Int, offsideTrap: Boolean, pressing: Int) {
        SoundManager.playClick()
        val currentProfile = _uiState.value.profile ?: return
        viewModelScope.launch {
            val updated = currentProfile.copy(
                currentTactic = tactic,
                defensiveLineHeight = defensiveLine,
                offsideTrapEnabled = offsideTrap,
                pressingIntensity = pressing
            )
            repository.updateProfile(updated)
            repository.addNotification(
                NotificationAlert(
                    id = "tac_${System.currentTimeMillis()}",
                    title = "تحديث التكتيك ومصيدة التسلل 📋",
                    message = "تم تفعيل خطة $tactic مع ضبط عمق الدفاع ($defensiveLine%) وحالة مصيدة التسلل (${if (offsideTrap) "مفعلة" else "معطلة"}).",
                    type = "LINEUP",
                    iconEmoji = "📐"
                )
            )
        }
    }

    fun toggleStartingXI(playerId: String) {
        SoundManager.playClick()
        val player = _uiState.value.squadPlayers.find { it.id == playerId } ?: return
        viewModelScope.launch {
            repository.updatePlayer(player.copy(isStartingXI = !player.isStartingXI))
        }
    }

    fun trainPlayer(playerId: String, drillType: String) {
        viewModelScope.launch {
            SoundManager.playClick()
            val result = repository.trainPlayer(playerId, drillType)
            result.onSuccess { updatedPlayer ->
                SoundManager.playGoalCheer()
                repository.addNotification(
                    NotificationAlert(
                        id = "train_${System.currentTimeMillis()}",
                        title = "تطور ملحوظ للاعب! 📈",
                        message = "أكمل ${updatedPlayer.name} تدريب '$drillType' بنجاح، وارتفع تقييم التسلل إلى ${updatedPlayer.offsideAwareness}!",
                        type = "CHALLENGE",
                        iconEmoji = "⚡"
                    )
                )
            }
        }
    }

    fun buyPlayer(player: Player, transferFee: Long, offeredWage: Long) {
        val profile = _uiState.value.profile ?: return
        viewModelScope.launch {
            val success = repository.buyPlayer(player, transferFee, offeredWage, profile)
            if (success) {
                SoundManager.playGoalCheer()
            }
        }
    }

    fun sellPlayer(player: Player) {
        val profile = _uiState.value.profile ?: return
        viewModelScope.launch {
            repository.sellPlayer(player, profile)
            SoundManager.playClick()
        }
    }

    fun claimDailyChallenge(challenge: DailyChallenge) {
        val profile = _uiState.value.profile ?: return
        viewModelScope.launch {
            SoundManager.playGoalCheer()
            repository.claimChallengeReward(challenge, profile)
        }
    }

    fun simulateMatch(match: MatchRecord, context: Context) {
        if (_uiState.value.isSimulating) return
        SoundManager.playWhistle()

        _uiState.update {
            it.copy(
                isSimulating = true,
                liveMatchState = LiveMatchState(match = match)
            )
        }

        viewModelScope.launch {
            var currentMin = 0
            var homeGoals = 0
            var awayGoals = 0
            var offsidesHome = 0
            var offsidesAway = 0
            val events = mutableListOf<String>()
            val profile = _uiState.value.profile
            val offsideTrap = profile?.offsideTrapEnabled ?: true

            // Send Lineup Notification before kickoff
            NotificationHelper.showNotification(
                context,
                "تأكيد تشكيلة المباراة: ${match.homeClubName} vs ${match.awayClubName}",
                "المدرب يدخل بخطة ${match.homeFormation} وخط دفاع متقدم. صافرة البداية الآن!"
            )

            while (currentMin < 90) {
                delay(600)
                currentMin += 15
                var eventText = "د $currentMin: ضغط مستمر وصراع في وسط الملعب"
                var isOffside = false
                var offsideText = ""

                // Chance of offside call
                if (Random.nextInt(100) < 35) {
                    isOffside = true
                    if (Random.nextBoolean()) {
                        offsidesAway++
                        offsideText = if (offsideTrap) {
                            "🚩 كشف تسلل ناجح! مصيدة التسلل تصطاد مهاجم ${match.awayClubName} بذكاء."
                        } else {
                            "🚩 راية تسلل ضد ${match.awayClubName}!"
                        }
                        SoundManager.playOffsideBuzzer()
                    } else {
                        offsidesHome++
                        offsideText = "🚩 تسلل على مهاجمنا! تم رفع الراية بسبب التسرع في الانطلاق."
                        SoundManager.playOffsideBuzzer()
                    }
                    eventText = "د $currentMin: $offsideText"
                    events.add(eventText)
                } else if (Random.nextInt(100) < 30) {
                    // Goal scored
                    if (Random.nextInt(100) < 65) {
                        homeGoals++
                        eventText = "⚽ جوووول! هدف لصالح ${match.homeClubName} بعد هجمة سريعة متقنة!"
                        SoundManager.playGoalCheer()
                    } else {
                        awayGoals++
                        eventText = "⚽ هدف لصالح ${match.awayClubName} من هجمة مباغتة!"
                    }
                    events.add("د $currentMin: $eventText")
                }

                _uiState.update {
                    it.copy(
                        liveMatchState = it.liveMatchState?.copy(
                            currentMinute = currentMin,
                            homeScore = homeGoals,
                            awayScore = awayGoals,
                            currentEventText = eventText,
                            isOffsideCallActive = isOffside,
                            offsideBannerText = offsideText,
                            eventsList = events.toList()
                        )
                    )
                }
            }

            SoundManager.playWhistle()
            val finishedMatch = match.copy(
                homeScore = homeGoals,
                awayScore = awayGoals,
                isFinished = true,
                offsidesHome = offsidesHome,
                offsidesAway = offsidesAway,
                possessionHome = Random.nextInt(52, 64),
                shotsHome = homeGoals * 3 + Random.nextInt(4, 9),
                shotsAway = awayGoals * 2 + Random.nextInt(3, 7)
            )

            repository.updateMatch(finishedMatch)

            // System Notification for Full-Time Result
            NotificationHelper.showNotification(
                context,
                "نهاية المباراة: ${finishedMatch.homeClubName} $homeGoals - $awayGoals ${finishedMatch.awayClubName}",
                "صافرة النهاية! انتهى اللقاء بفوز ${if (homeGoals > awayGoals) finishedMatch.homeClubName else if (awayGoals > homeGoals) finishedMatch.awayClubName else "التعادل"}. محلل الأوفسايد الذكي أعد لك التقرير."
            )

            // Trigger Gemini AI Tactical Analysis for the match
            generateSmartOffsideAnalysis(finishedMatch)

            _uiState.update {
                it.copy(
                    isSimulating = false,
                    liveMatchState = it.liveMatchState?.copy(
                        isComplete = true,
                        currentEventText = "صافرة النهاية! النتيجة: $homeGoals - $awayGoals"
                    )
                )
            }
        }
    }

    fun generateSmartOffsideAnalysis(match: MatchRecord) {
        val profile = _uiState.value.profile ?: return
        _uiState.update { it.copy(isAnalyzingWithGemini = true) }

        viewModelScope.launch {
            val analysis = GeminiService.analyzeMatchOffsideAndTactics(
                match = match,
                defensiveLineHeight = profile.defensiveLineHeight,
                offsideTrapActive = profile.offsideTrapEnabled,
                managerTactic = profile.currentTactic
            )

            val updatedMatch = match.copy(smartOffsideAnalysis = analysis)
            repository.updateMatch(updatedMatch)

            _uiState.update {
                it.copy(
                    isAnalyzingWithGemini = false,
                    activeOffsideReport = analysis
                )
            }

            repository.addNotification(
                NotificationAlert(
                    id = "ana_${System.currentTimeMillis()}",
                    title = "تقرير محلل الأوفسايد الذكي (Gemini AI) 🧠",
                    message = "تم تحليل أداء مصيدة التسلل والعمق الدفاعي لمباراة ${match.homeClubName} vs ${match.awayClubName}. اضغط للاطلاع على نصائح المدرب.",
                    type = "OFFSIDE_ANALYSIS",
                    iconEmoji = "📊"
                )
            )
        }
    }

    fun sendTacticalQuestion(question: String) {
        if (question.isBlank()) return
        SoundManager.playClick()
        val userMsg = ChatMessage("MANAGER", question)
        val clubName = _uiState.value.profile?.chosenClubName ?: "فريقك"
        val currentTactic = _uiState.value.profile?.currentTactic ?: "Gegenpressing"

        _uiState.update {
            it.copy(
                chatMessages = it.chatMessages + userMsg,
                isChatLoading = true
            )
        }

        viewModelScope.launch {
            val answer = GeminiService.chatWithTacticalAnalyst(
                userQuestion = question,
                clubName = clubName,
                currentFormation = "4-3-3",
                currentTactic = currentTactic
            )

            _uiState.update {
                it.copy(
                    chatMessages = it.chatMessages + ChatMessage("AI_ANALYST", answer),
                    isChatLoading = false
                )
            }
        }
    }

    fun syncCareerWithCloud(context: Context) {
        _uiState.update { it.copy(isSyncingCloud = true, cloudSyncMessage = "جاري الاتصال بحساب Google والمزامنة السحابية...") }
        viewModelScope.launch {
            delay(1200)
            val authResult = AuthManager.performGoogleSignIn(context)
            authResult.onSuccess { session ->
                val currentProfile = _uiState.value.profile
                if (currentProfile != null) {
                    val updated = currentProfile.copy(
                        userEmail = session.email,
                        isCloudSynced = true,
                        lastSyncTimestamp = System.currentTimeMillis()
                    )
                    repository.updateProfile(updated)
                }

                SoundManager.playGoalCheer()
                _uiState.update {
                    it.copy(
                        isSyncingCloud = false,
                        cloudSyncMessage = "تمت المزامنة بنجاح مع حساب Google (${session.email}) ☁️✅"
                    )
                }

                repository.addNotification(
                    NotificationAlert(
                        id = "sync_${System.currentTimeMillis()}",
                        title = "مزامنة سحابية مؤكدة ☁️",
                        message = "تم حفظ جميع بيانات طور المسيرة وتحديثات الانتقالات على خوادم السحابة لحساب ${session.email}.",
                        type = "CHALLENGE",
                        iconEmoji = "💾"
                    )
                )
            }
        }
    }

    fun dismissOffsideReport() {
        _uiState.update { it.copy(activeOffsideReport = null) }
    }

    fun dismissCloudSyncModal() {
        _uiState.update { it.copy(cloudSyncMessage = null) }
    }

    fun markNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }
}
