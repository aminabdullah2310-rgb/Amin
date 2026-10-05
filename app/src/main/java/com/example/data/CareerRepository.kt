package com.example.data

import com.example.model.CareerProfile
import com.example.model.Club
import com.example.model.Coach
import com.example.model.DailyChallenge
import com.example.model.MatchRecord
import com.example.model.NotificationAlert
import com.example.model.Player
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class CareerRepository(private val careerDao: CareerDao) {

    val careerProfile: Flow<CareerProfile?> = careerDao.getCareerProfile()
    val allClubs: Flow<List<Club>> = careerDao.getAllClubs()
    val allCoaches: Flow<List<Coach>> = careerDao.getAllCoaches()
    val allPlayers: Flow<List<Player>> = careerDao.getAllPlayers()
    val allMatches: Flow<List<MatchRecord>> = careerDao.getAllMatches()
    val upcomingMatches: Flow<List<MatchRecord>> = careerDao.getUpcomingMatches()
    val finishedMatches: Flow<List<MatchRecord>> = careerDao.getFinishedMatches()
    val dailyChallenges: Flow<List<DailyChallenge>> = careerDao.getAllChallenges()
    val notifications: Flow<List<NotificationAlert>> = careerDao.getAllNotifications()

    fun getClubPlayers(clubId: String): Flow<List<Player>> = careerDao.getPlayersByClub(clubId)

    fun getTransferMarket(myClubId: String): Flow<List<Player>> = careerDao.getTransferMarketPlayers(myClubId)

    suspend fun ensureInitialized() {
        val profile = careerDao.getCareerProfile().firstOrNull()
        if (profile == null) {
            careerDao.insertProfile(InitialData.defaultProfile)
            careerDao.insertClubs(InitialData.clubs)
            careerDao.insertCoaches(InitialData.coaches)
            careerDao.insertPlayers(InitialData.players)
            careerDao.insertMatches(InitialData.matches)
            careerDao.insertChallenges(InitialData.dailyChallenges)
            for (notif in InitialData.notifications) {
                careerDao.insertNotification(notif)
            }
        } else {
            // Re-sync clubs & coaches to ensure authentic crests & portraits are always up-to-date
            careerDao.insertClubs(InitialData.clubs)
            careerDao.insertCoaches(InitialData.coaches)
            for (seedPlayer in InitialData.players) {
                val existing = careerDao.getPlayerByIdDirect(seedPlayer.id)
                if (existing == null) {
                    careerDao.insertPlayers(listOf(seedPlayer))
                } else if (existing.photoUrl.isEmpty() || existing.photoUrl != seedPlayer.photoUrl) {
                    careerDao.updatePlayer(existing.copy(
                        photoUrl = seedPlayer.photoUrl,
                        clubLogoUrl = seedPlayer.clubLogoUrl
                    ))
                }
            }
        }
    }

    suspend fun updateProfile(profile: CareerProfile) {
        careerDao.updateProfile(profile)
    }

    suspend fun updatePlayer(player: Player) {
        careerDao.updatePlayer(player)
    }

    suspend fun trainPlayer(playerId: String, drillType: String): Result<Player> {
        val player = careerDao.getPlayerByIdDirect(playerId) ?: return Result.failure(Exception("Player not found"))
        val xpGain = 25
        val newXp = player.trainingXp + xpGain
        var newOverall = player.overallRating
        var newTargetXp = player.trainingTargetXp
        var newOffside = player.offsideAwareness
        var newPace = player.pace
        var newShooting = player.shooting
        var newPassing = player.passing
        var newDefending = player.defending

        when (drillType) {
            "OFFSIDE_AVOIDANCE" -> {
                newOffside = (newOffside + 2).coerceAtMost(99)
                newPace = (newPace + 1).coerceAtMost(99)
            }
            "FINISHING" -> {
                newShooting = (newShooting + 1).coerceAtMost(99)
            }
            "TIKI_TAKA_PASSING" -> {
                newPassing = (newPassing + 1).coerceAtMost(99)
                newOffside = (newOffside + 1).coerceAtMost(99)
            }
            "DEFENSIVE_LINE_TRAP" -> {
                newDefending = (newDefending + 1).coerceAtMost(99)
                newOffside = (newOffside + 2).coerceAtMost(99)
            }
        }

        if (newXp >= newTargetXp && newOverall < player.potentialRating) {
            newOverall += 1
            newTargetXp += 50
        }

        val updated = player.copy(
            overallRating = newOverall,
            offsideAwareness = newOffside,
            pace = newPace,
            shooting = newShooting,
            passing = newPassing,
            defending = newDefending,
            trainingXp = if (newXp >= newTargetXp) 0 else newXp,
            trainingTargetXp = newTargetXp,
            energy = (player.energy - 8).coerceAtLeast(30)
        )
        careerDao.updatePlayer(updated)
        return Result.success(updated)
    }

    suspend fun buyPlayer(player: Player, transferFee: Long, offeredWage: Long, profile: CareerProfile): Boolean {
        if (profile.transferBudget < transferFee) return false
        val newTransferBudget = profile.transferBudget - transferFee
        val newWageBudget = profile.weeklyWageBudget - (offeredWage - player.weeklyWage)

        val updatedPlayer = player.copy(
            clubId = profile.chosenClubId,
            clubName = profile.chosenClubName,
            weeklyWage = offeredWage,
            isStartingXI = false,
            morale = 100
        )
        careerDao.updatePlayer(updatedPlayer)
        careerDao.updateProfile(profile.copy(
            transferBudget = newTransferBudget,
            weeklyWageBudget = newWageBudget
        ))

        careerDao.insertNotification(
            NotificationAlert(
                id = "trans_${System.currentTimeMillis()}",
                title = "صفقة رسمية جديدة! ✍️",
                message = "تعاقد ${profile.chosenClubName} رسمياً مع النجم ${player.name} مقابل ${transferFee / 1_000_000} مليون يورو!",
                type = "TRANSFER",
                iconEmoji = "🤝"
            )
        )
        return true
    }

    suspend fun sellPlayer(player: Player, profile: CareerProfile): Long {
        val sellPrice = (player.marketValue * 0.95).toLong()
        val updatedProfile = profile.copy(
            transferBudget = profile.transferBudget + sellPrice,
            weeklyWageBudget = profile.weeklyWageBudget + player.weeklyWage
        )
        val transferredPlayer = player.copy(
            clubId = "bayern", // Transfer to another top club
            clubName = "Bayern Munich",
            isStartingXI = false
        )
        careerDao.updatePlayer(transferredPlayer)
        careerDao.updateProfile(updatedProfile)

        careerDao.insertNotification(
            NotificationAlert(
                id = "sell_${System.currentTimeMillis()}",
                title = "بيع لاعب بنجاح 💸",
                message = "انتقل ${player.name} إلى نادي بايرن ميونخ مقابل ${sellPrice / 1_000_000} مليون يورو وتمت إضافة المبلغ لميزانيتك.",
                type = "TRANSFER",
                iconEmoji = "💰"
            )
        )
        return sellPrice
    }

    suspend fun updateMatch(match: MatchRecord) {
        careerDao.updateMatch(match)
    }

    suspend fun claimChallengeReward(challenge: DailyChallenge, profile: CareerProfile) {
        if (!challenge.isCompleted || challenge.isClaimed) return
        careerDao.updateChallenge(challenge.copy(isClaimed = true))
        careerDao.updateProfile(profile.copy(
            totalCoins = profile.totalCoins + challenge.rewardCoins,
            coachXp = profile.coachXp + challenge.rewardXp
        ))
        careerDao.insertNotification(
            NotificationAlert(
                id = "chall_${System.currentTimeMillis()}",
                title = "مكافأة التحدي اليومي! 🎁",
                message = "استلمت ${challenge.rewardCoins} كوينز و ${challenge.rewardXp} نقطة خبرة لإنجاز: ${challenge.titleAr}",
                type = "CHALLENGE",
                iconEmoji = "🎉"
            )
        )
    }

    suspend fun addNotification(alert: NotificationAlert) {
        careerDao.insertNotification(alert)
    }

    suspend fun markAllNotificationsAsRead() {
        careerDao.markAllNotificationsAsRead()
    }
}
