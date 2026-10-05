package com.example.data

import com.example.model.CareerProfile
import com.example.model.Club
import com.example.model.Coach
import com.example.model.DailyChallenge
import com.example.model.FriendItem
import com.example.model.GameModeItem
import com.example.model.LeaderboardUser
import com.example.model.MatchRecord
import com.example.model.NewsItem
import com.example.model.NotificationAlert
import com.example.model.Player

object InitialData {

    val defaultProfile = CareerProfile(
        id = 1,
        managerName = "Amin Abdullah",
        managerNationality = "Saudi Arabia",
        chosenClubId = "real_madrid",
        chosenClubName = "Real Madrid",
        seasonYear = 2026,
        currentWeek = 1,
        transferBudget = 185_000_000L,
        weeklyWageBudget = 4_200_000L,
        managerReputation = 89,
        currentTactic = "Gegenpressing",
        defensiveLineHeight = 72,
        offsideTrapEnabled = true,
        pressingIntensity = 80,
        userEmail = "aminabdullah2310@gmail.com",
        isCloudSynced = true,
        totalCoins = 12_450L,
        totalGems = 450,
        coachXp = 4_820,
        nextLevelXp = 6_000,
        managerLevel = 38
    )

    // Authentic Club Crests from Design Spec
    const val CREST_REAL_MADRID = "https://lh3.googleusercontent.com/aida/AEtjO1UQ8VHPn4y6ygRxKrJFA_BEhzF8GSL9q7mVM8xKwzpIJg0mHgX7HhrNHBwpnCvcNhfgFm2IhQsEI2pxiwtrPzzeBj6pL188bvOropTXQNQ27yj3azYpNNd1rwqR-NE_r64Rs6Wutavyas3yRkK4oAXaC8-Ov7Yx-92zhFh2G0mGQCt4LMnEPgKyT7zOsD2bQIXs7lMgrS3bi_Ri1xjr_p-BJ-_IG7OGj21hw_p8UiCz7jK5TAPnzf4M_8LI"
    const val CREST_BARCELONA = "https://lh3.googleusercontent.com/aida/AEtjO1X8kewNZcTymMQVIFmMXF0vV8pUZNsc3I7NtgASNg29-6DCw66-11Anjl_yGr6olkInI2ej1nzTg3gNS8AKRPlN4lVkbWwFjc-Kkf6ORYiiY3UoWMQr48P8hrUuEGRzBOBRZN4YkCP1f2HBfVh8TId-BggZD03Vi75aTYOs8yRSmRCzOMU4hSrb1S4Ndukt7arl5m5-nQrB9v2NIe-yFiDEVCe9ClONpza8bx2GN4WxYPi-zXSg9Lp8L-A1"
    const val CREST_MAN_CITY = "https://lh3.googleusercontent.com/aida/AEtjO1XKbtkI8ZBy2TU7X_DAaSOWPs2q-JuUwt5kWnuUDaUt-YXIyeID_0LH8pw88de_8Q52-C7Dt1EXoxbZVptBDtqSVtbDALtz8nOg1UcfPZcuBYHs0dJac-mZaYeEYTRzK6BEYkvJjMTx4gbcQI9HjmHMxl2jYkf8p0JHl1i4_BzpLx3NeyJMY5UZdxf7zzC8TPY-SnwR8kBlqhgRloKq4e9enWk0R8wuBDCt21Qps6BuSJBug8o7yiWJFhiz"
    const val CREST_ARSENAL = "https://lh3.googleusercontent.com/aida/AEtjO1Vk9FcDKfAFjSH3WlCthopauJWrmRPBSt6sezClnaAz4dPFcLtnSVK02mpU3lKvTWa1tkvYQEcviVu5pS-fgCQdiAdn_ADjlwbb7gAE5wcmulBDsek2csMSdFGbb0QeukRBXd4q1a60LsL05k58U4VLqB8AZL-VVk5oLUAc3ID4Of3vjf4n4fcd06-j4ORCEK5tQXoPtVx_O5sYbThoCA8byNEG1d618_gbV7DvTlf_PCBzPMVizYTv7VAg"
    const val CREST_LIVERPOOL = "https://lh3.googleusercontent.com/aida-public/AB6AXuCg3nvKIvaznmGIvGwgRs0cqdv646S5hhbXs4EVForGdctfxbY6UzrWKp7zilHqt0EGy4Asa6tUgICHisD180NBKDqbcx_sOPJN6OMTtH7o182psyUaPIBzyPxGsGXTFke01Ipsgd0bAQ6Vfvz_rKDmyUD1NECTOJ72M2yVTU_gkTHhjwWwQyzM3KRoT3Y8cjT1uZN25xd3Zho0-B2zTxGCmQ1d2UI40PU3CzOCT3ge7IQCfhZ7WFko8Q"
    const val CREST_BAYERN = "https://lh3.googleusercontent.com/aida-public/AB6AXuBbx_E5ksjvC3eXMgCVs2cOJRMUKzZCc4OHMj6hsGMGk4cCCVLQQhP9OYChBMHifjNh-qCq3zlxQb6Qt7gUPJAzJj4NuO4NislbGdXV47wEy0mA9HOxsROhn8uUWs-xJKg2dhcba20--_eXhu_CZD7dgm0ZRgWjD2X52cYYaZXfGsjVjLHxipvFdIXfM4ayUDEFMQ_zu6Kl03X5X_5ozluheFsjJMbZ1lH4qXlKIzw-lbZy7TniRGQnGg"

    // Authentic Player Photos from Design Spec
    const val PHOTO_MESSI = "https://lh3.googleusercontent.com/aida-public/AB6AXuA-MsNnzDlFg8O2Q2w1CC1c9UFgHZZFyzwiSjZ3KH7FqqW_TM4w1EjbbVJNQZI7eMU1gPwmnL98CerB4zLMx-pTBJ4aBk4WWNugX8KWJbb_O_SaU9g4wh_B_tGmuLkA3MxC6j15wE8c82xYZR43NbqBDYonzHcvmXOZeH676WngdjGfGHRYqIAMLtzyReoIaX6LbtwxrdYDKe6IGAFdR0DYMMpRIdgXTAtLA8ropF8YOVfd0ueCf-hG5g"
    const val PHOTO_RONALDO = "https://lh3.googleusercontent.com/aida-public/AB6AXuDkvIWRpNSsLMcOtdSfWMlEVjkkuxO93BOrG0EMmHOSFAaOXS85W33ZXME1r3DHjEgJ0mTlLBg47DbrAMW8GArJ5lZ2FakH4z4v27X3bh0s_IuH9NuJISdeHbTqY7k2emyEg3EJ2aZ98PIpdPfQ3zKm2QCx1U67Wmu8SwE071zS9SZmdSFbgKbU_ifmnyisHEZStusOxFlxN36aNx0EO5Ud6hJ2jDa-jNsfz8NKPBfdn7VG6DZ3Txm12A"
    const val PHOTO_MBAPPE = "https://lh3.googleusercontent.com/aida/AEtjO1XVA5uGiWgXbVWZWHpF7jVw420kGHXMVHGQ2qOApCwNS-v3BWCqkZXCjfUsDWWBdi6rhAQDwmHSQdP3ygQIlZKbDfTZh3t3aj19poVf_o-rluAhv0QseD1rAWQ7RkbJr6qeGgPnNKV4hKfywa_Uf-dmA0Z7H_PG5Y94jXNqaFd-8tJ8bOzNe83W1aOKN8iEdqQ2UONgmTTz1GnP9ILGMoqON5QPR10v-b9BqScdYmcEq-OWWq-h1r35_eGr"
    const val PHOTO_HAALAND = "https://lh3.googleusercontent.com/aida/AEtjO1USQ2kJkiiv_iWpJWaegneAaiKJIvGyO0BxaD_dcL0lNU4umbREtcjv6e2TAa1s4t291RDHPOFuhUJ_PR4lGjCQ31z7tnVL55ZtTE0AXFUM7h9VoTK4Yc5v_WlO4frJbK8JWuZVamrI_VZfusjy6sLBLt5kUKVoYffkRq03J18kKG1Pkx93ZfxqHP8h9cEJ1fTXmGshL8d5hlTJHF7OJqw7qA8fIZ3jzTjPUlFJbn1ycorrib47Cn75gVA"
    const val PHOTO_VINICIUS = "https://lh3.googleusercontent.com/aida-public/AB6AXuC99oyp_3rgoRWMZ5MzXJyQveYBkiu_ncia3gN9eDPKfZyFLNyFcVKdSpgDWCaB4i2zZBCgQ6of1xEZTw0lM9Fewcnjzb1cPniDW0H5SJJpuu-zN_UcJoegajqVzrHRu6gzUTdLzGv-vdOvga0e_SnUj2mq646pbqg1ndAB_DOnpKrRIJp-COiMF5aYtDQ-3kG1JE4_fHCGYuwA9-WZP4EmpaOGhRuHnt6CijTJbWcZ2eJxc0uEHtMOqQ"
    const val PHOTO_SALAH = "https://lh3.googleusercontent.com/aida-public/AB6AXuAZxzf-Fepf51QVAD_kLn5_SA4K2wm3XLB3BPKepE8gpqlJj2lrZT4TywrWOBhpkOzqdy_OTMTCAL6NCbGBRxnNJ0Eu_lnn-WULQNovVjPHjS3ydq79umuQpvY_EL1tu08FdSP1POFy2aOzK9ee2R2fQK738x6k_lzntMqyWtwdvyIPgranuSdO5kU_oYVJuzSCoykQrK17rrxjUfHBYLbQOM94OM9g3PVE0xtDMWjYPLkDY04hZT7xvw"

    // Authentic Coach Photos from Design Spec
    const val PHOTO_ANCELOTTI = "https://lh3.googleusercontent.com/aida/AEtjO1X-trxYI8-szUEiMwA9DxBFN-76GFzuX6wZSEcd-XRvPUenr26G7X8nfKzTf9hCQTzoJyUoTtKVjYxDPT_3VA8aqBL5PiYbnu_UsxDZMOLcJ5Y0cD97UevzX6XA-D4hbLP0wHZUVJGr4SZGVyTASr6TnLT2Kl8vtetlHw9NhultFjVP7T2G5L5_q7ac-VpQGnErGubMR-bWGCT2rxoFyQWXbtxkPIfPTUZEZ_Kig_hV63j-qEOa970bYnqv"
    const val PHOTO_GUARDIOLA = "https://lh3.googleusercontent.com/aida/AEtjO1XRlqFZFnj6_d3tRfxP63-Nx4yGdlbXiiYTIIyY2M1W_6TAqPZTAupoEOeEvhCEzOTo2BhsWGw2SuZMqtQuvGlX0aF1OAnLnmvMwKujzcp-ddLqrWzVUnPS-g1_9_8cwwLM3HWZvSeHybER27rjmzq_jqRLDjJX3PuhJ0oOwkcxDd6vtw3pKYUx4P9A73OC-EUswfKY_K8Y4_X5GDMO-YC4LmG4kLSI7CPagvzn7ABcfUmErQW1DgQfiTs5"
    const val PHOTO_KLOPP = "https://lh3.googleusercontent.com/aida-public/AB6AXuCp6GUFjeeM2CtcYhEl5NY7hK9xTv5iHdpVsne6NLn5G-gLb2QUVf5SlDfUVMjRK-_JilwsUgoI5fvfKrFbwgP1RliVKmfSMmSGIRZlJFSmKT5eK9A9bQmLw9Ed-DuiVhBxHCOiiTPoftt7TstfH4Ie6cYqqMu1Zd8eC6T-jSfZSQYYuqbECFgczJYqy9fLlUbKmHbefkDOscvXJtEpDliCXn_9yJ8MKEHwomKOgDQc0cufacE5YBNnNA"
    const val PHOTO_MOURINHO = "https://lh3.googleusercontent.com/aida-public/AB6AXuBOBGvvQGUdTWjoqTQQNC4QW1vBqBRmC1a7sSMLpEeH6u47Jsma_VEiUMKBSS_yxOF4KvBfHvsu2btDId2tIaJGUzv6uM86qAy3nt0GA8ZGOBdCHrdgmvVQcUaNOa80JiutZoOI-a6Yc4OAMEA6AbWjaqgua0YbFbzoH5eOse2NG4na4pGKIz2EWAqotQJJTnKJLewVtnhH2tR58pKn6MuvLLm9_1N8JJbNFsqb_3jhmSrdlDO0358YCw"
    const val PHOTO_FLICK = "res:coach_flick"
    const val PHOTO_BELLINGHAM = "res:player_bellingham"
    const val PHOTO_VALVERDE = "res:player_valverde"
    const val PHOTO_RUDIGER = "res:player_rudiger"
    const val PHOTO_COURTOIS = "res:player_courtois"
    const val PHOTO_YAMAL = "res:player_yamal"

    // 7 Worlds Graphics
    const val WORLD_KNOWLEDGE = "https://lh3.googleusercontent.com/aida-public/AB6AXuBJOiW3OLibAKep_c6FvHOzfPLLGGPZSNWc_T2uCfr_jNUyHIxxWfWq2z4rJ2RcZ2I1DvVY1Y_5XGU0pV7kV2Pi62-ErepXQwrAwPLPUWUmFqDPR57YeoQJoHAPoq_9zNwq5HsiJCOwFGzSazyHFmlVn51nOArSuutl8YmdMx6DscPOxD_XESgvnQz5yDeZ7GebSIep535tph7ugLHp2krZuD__PrHND3BObeSUEm_5OV5FkfYFSJfBcA"
    const val WORLD_ARENA = "https://lh3.googleusercontent.com/aida-public/AB6AXuA4z0GRnMxs4L7WXZLFVncMbNPkSuBAw_d6Mwmf8Gejmy4mFb-AubFo7ZXIOkUEznyOUZkpaSSn2wWrUEQ_5M2_PQCI7Ievm1UT6MyYyEZnC-rqqsEjJd2EGUX2eqCaO_IlNfuWRSvRpYFUaR9sQVGzvJyBIqW34sJ5HOjkumqOT3_gVsq2MacAxUc_EJIxI8BxJmBOFTId8pwgRuNZdBkrBLz7W7ZhFnKLtjh7MNY9jqCAfeJXZSV8FA"
    const val WORLD_SQUAD = "https://lh3.googleusercontent.com/aida-public/AB6AXuD6hC3GKWxaeE6vEWxGr988Mpm92xt8zfmd6ILGpYTxOCL3qhThCk4zPnFLgU3aaPku6lGml6me7D0WeQibb5D32Pu9DoqA2VV5sUNw30VeoFbWXzWKGC6rk0IXKIW94MBiiPn1nk_LvSTXwcGwcoBMels_0oiv_9ajePQFFNAX3tATyplVCCS0Gw_MSBXnjDEYDZ0oUXmR-0ZT7l3UWUZ3SUGr5jXqyF-IgOQSVDU9KrnuZDL3RVGzxw"
    const val WORLD_CAREER = "https://lh3.googleusercontent.com/aida-public/AB6AXuAsjByXLWB3lwjQVyz8XndwZC_442UBSvghNbxEqEKH1Y5Pc58jFRXl-9Sl8f14ds0GiPBY9FaBr9g2JueZW5aDGNwRcsRK1G-r1RxnUOSWWOh_n3Uu6LHMLaBt0kxKJ6l9z1T23gwj5nzDacKG6NieQubcXyjiSZwwlGSdX4L00_eikAWVzBrg3kohG72iGg-eWVvEIy-Crm7LTcb3vG7Ymr3ssVUSah5qzO_iWGw1dFDe3JIaAjo1AQ"
    const val WORLD_GLOBAL = "https://lh3.googleusercontent.com/aida-public/AB6AXuAQ7ZNKeo6gST9u7zukZCGnqc6gj2bo31ZzlNGZR2wmO0znMA4R2s18rikaVL2HAZcNUmDgAT5-KkSjXb3OobOdaUvHjzteeNo4frJT8E3K8-e44zawvT2jctofKqh_yLYzK993v_8c7eJioxDDKyVLM2PfRlpu9_Qu3gzcNxGmeiS1oSP9OiU8JFadJtWE-NB0xYG_yvUcn8mbUKIIQAAqREDslI45tG_gNOVq2Ws21xIso3v-oVk3rA"
    const val WORLD_CLUB = "https://lh3.googleusercontent.com/aida-public/AB6AXuBHr4mrPidyFoj2rlKHf3F94_j3i5szjyDmOiOQdoC9s325G3AB8GTqbU-88HdP8iqFWO1FNRxeYwOwvfWllozOn0vs8WtPd6tRr9D00tIIrtNvADJSA22-G0QIwW5I2hBeN7B6UdNlmXNTbtdPCZrXx7-UJQ8Ga8kXvZbfSojGxFJ9v71MKIUgMKnxcURwsFYAMOQE140LwAvTniSVQKmwRO44xQT4usvRsx8qb3MF6_6G97Zgfu8_6g"

    val clubs = listOf(
        Club("real_madrid", "Real Madrid", "RMA", "La Liga", "#FFFFFF", "#FEBE10", "Santiago Bernabéu", 220_000_000L, 95, "Carlo Ancelotti", "4-3-3", "👑", CREST_REAL_MADRID, 4, 1, 0, 13, 5, 12, 3),
        Club("barcelona", "FC Barcelona", "BAR", "La Liga", "#004D98", "#A50044", "Spotify Camp Nou", 130_000_000L, 93, "Hansi Flick", "4-2-3-1", "🔴🔵", CREST_BARCELONA, 3, 2, 0, 11, 5, 10, 4),
        Club("man_city", "Manchester City", "MCI", "Premier League", "#6CABDD", "#1C2C5B", "Etihad Stadium", 240_000_000L, 96, "Pep Guardiola", "4-3-3", "🦁", CREST_MAN_CITY, 4, 0, 1, 12, 5, 14, 5),
        Club("arsenal", "Arsenal FC", "ARS", "Premier League", "#EF0107", "#063672", "Emirates Stadium", 160_000_000L, 92, "Mikel Arteta", "4-3-3", "🔴⚪", CREST_ARSENAL, 3, 1, 1, 10, 5, 9, 4),
        Club("liverpool", "Liverpool FC", "LIV", "Premier League", "#C8102E", "#00B2A9", "Anfield", 175_000_000L, 93, "Arne Slot", "4-3-3", "🔴", CREST_LIVERPOOL, 3, 1, 1, 10, 5, 11, 6),
        Club("bayern", "Bayern Munich", "BAY", "Bundesliga", "#DC052D", "#0066B2", "Allianz Arena", 190_000_000L, 94, "Vincent Kompany", "4-2-3-1", "⭐", CREST_BAYERN, 3, 0, 2, 9, 5, 13, 7)
    )

    val coaches = listOf(
        Coach(
            id = "c_ancelotti",
            name = "كارلو أنشيلوتي",
            clubId = "real_madrid",
            clubName = "Real Madrid",
            nationality = "إيطاليا",
            tacticalPhilosophy = "خطة 4-3-3 المرنة",
            tacticTitle = "الهجوم المضاد والتحولات",
            tacticBonus = "مكافأة هجومية +15% في الشوط الثاني",
            offsideTrapMastery = 92,
            pressIntensity = 75,
            defensiveLineDepth = 70,
            reputation = 96,
            photoUrl = PHOTO_ANCELOTTI,
            clubLogoUrl = CREST_REAL_MADRID
        ),
        Coach(
            id = "c_pep",
            name = "بيب جوارديولا",
            clubId = "man_city",
            clubName = "Manchester City",
            nationality = "إسبانيا",
            tacticalPhilosophy = "استحواذ وضغط عالي 3-2-4-1",
            tacticTitle = "التمرير المستمر وتطويق الخصم",
            tacticBonus = "تحكم التمريرات ودقة التوزيع +20%",
            offsideTrapMastery = 95,
            pressIntensity = 90,
            defensiveLineDepth = 85,
            reputation = 97,
            photoUrl = PHOTO_GUARDIOLA,
            clubLogoUrl = CREST_MAN_CITY
        ),
        Coach(
            id = "c_flick",
            name = "هانسي فليك",
            clubId = "barcelona",
            clubName = "FC Barcelona",
            nationality = "ألمانيا",
            tacticalPhilosophy = "الضغط العالي ومصيدة الأوفسايد 4-2-3-1",
            tacticTitle = "مصيدة التسلل الصارمة والخنق الهجومي",
            tacticBonus = "كشف التسلل والسرعة الجماعية +25%",
            offsideTrapMastery = 98,
            pressIntensity = 94,
            defensiveLineDepth = 88,
            reputation = 95,
            photoUrl = PHOTO_FLICK,
            clubLogoUrl = CREST_BARCELONA
        ),
        Coach(
            id = "c_klopp",
            name = "يورغن كلوب",
            clubId = "liverpool",
            clubName = "Liverpool FC",
            nationality = "ألمانيا",
            tacticalPhilosophy = "Gegenpressing 4-3-3",
            tacticTitle = "الضغط العكسي الخانق",
            tacticBonus = "سرعة استخلاص الكرة واللياقة +18%",
            offsideTrapMastery = 90,
            pressIntensity = 95,
            defensiveLineDepth = 80,
            reputation = 95,
            photoUrl = PHOTO_KLOPP,
            clubLogoUrl = CREST_LIVERPOOL
        ),
        Coach(
            id = "c_arteta",
            name = "ميكل أرتيتا",
            clubId = "arsenal",
            clubName = "Arsenal FC",
            nationality = "إسبانيا",
            tacticalPhilosophy = "التحكم التكتيكي والكرات الثابتة 4-3-3",
            tacticTitle = "الصلابة الدفاعية والتحولات الذكية",
            tacticBonus = "تنظيم الخط الخلفي والكرات الثابتة +20%",
            offsideTrapMastery = 91,
            pressIntensity = 85,
            defensiveLineDepth = 75,
            reputation = 92,
            photoUrl = "",
            clubLogoUrl = CREST_ARSENAL
        ),
        Coach(
            id = "c_mourinho",
            name = "جوزيه مورينيو",
            clubId = "fenerbahce",
            clubName = "Club Legend",
            nationality = "البرتغال",
            tacticalPhilosophy = "انضباط دفاعي 4-2-3-1",
            tacticTitle = "الجدار الحديدي والارتداد",
            tacticBonus = "صلابة الدفاع والهجمات المرتدة +22%",
            offsideTrapMastery = 96,
            pressIntensity = 65,
            defensiveLineDepth = 40,
            reputation = 94,
            photoUrl = PHOTO_MOURINHO,
            clubLogoUrl = ""
        )
    )

    val players = listOf(
        // Superstars with Authentic Images
        Player("p_messi", "ليونيل ميسي", "barcelona", "FC Barcelona", "RW", 37, 94, 94, 90, 94, 95, 96, 38, 72, 94, 75_000_000L, 350_000L, 95, 92, 0, 100, true, "الأرجنتين", PHOTO_MESSI, CREST_BARCELONA, "🇦🇷", "ICON"),
        Player("p_ronaldo", "كريستيانو رونالدو", "real_madrid", "Real Madrid", "ST", 39, 93, 93, 88, 94, 82, 89, 36, 90, 92, 60_000_000L, 360_000L, 96, 94, 0, 100, true, "البرتغال", PHOTO_RONALDO, CREST_REAL_MADRID, "🇵🇹", "ICON"),
        Player("p1", "كيليان مبابي", "real_madrid", "Real Madrid", "ST", 26, 93, 95, 97, 92, 82, 93, 36, 78, 88, 185_000_000L, 450_000L, 95, 96, 20, 100, true, "فرنسا", PHOTO_MBAPPE, CREST_REAL_MADRID, "🇫🇷", "ICON"),
        Player("p2", "فينيسيوس جونيور", "real_madrid", "Real Madrid", "LW", 25, 91, 94, 96, 85, 84, 92, 32, 70, 89, 165_000_000L, 380_000L, 94, 94, 30, 100, true, "البرازيل", PHOTO_VINICIUS, CREST_REAL_MADRID, "🇧🇷", "GOLD_RARE"),
        Player("p3", "جود بيلينجهام", "real_madrid", "Real Madrid", "CAM", 22, 90, 95, 80, 86, 87, 88, 78, 83, 90, 175_000_000L, 350_000L, 98, 92, 45, 100, true, "إنجلترا", PHOTO_BELLINGHAM, CREST_REAL_MADRID, "🏴󠁧󠁢󠁥󠁮󠁧󠁿", "FUTURE_STAR"),
        Player("p5", "فيديريكو فالفيردي", "real_madrid", "Real Madrid", "CM", 27, 88, 90, 88, 83, 85, 84, 81, 84, 85, 120_000_000L, 260_000L, 96, 98, 40, 100, true, "الأوروغواي", PHOTO_VALVERDE, CREST_REAL_MADRID, "🇺🇾", "GOLD_RARE"),
        Player("p8", "أنطونيو روديغر", "real_madrid", "Real Madrid", "CB", 32, 88, 88, 82, 55, 71, 68, 86, 86, 88, 45_000_000L, 210_000L, 95, 93, 15, 100, true, "ألمانيا", PHOTO_RUDIGER, CREST_REAL_MADRID, "🇩🇪", "GOLD_RARE"),
        Player("p11", "تيبو كورتوا", "real_madrid", "Real Madrid", "GK", 33, 89, 89, 48, 25, 75, 50, 89, 87, 85, 40_000_000L, 240_000L, 96, 99, 10, 100, true, "بلجيكا", PHOTO_COURTOIS, CREST_REAL_MADRID, "🇧🇪", "GOLD_RARE"),
        Player("p15", "إيرلينغ هالاند", "man_city", "Manchester City", "ST", 25, 92, 95, 89, 95, 72, 82, 45, 91, 84, 195_000_000L, 420_000L, 95, 96, 0, 100, true, "النرويج", PHOTO_HAALAND, CREST_MAN_CITY, "🇳🇴", "ICON"),
        Player("p22", "محمد صلاح", "liverpool", "Liverpool FC", "RW", 33, 91, 91, 92, 89, 84, 90, 45, 76, 88, 70_000_000L, 350_000L, 95, 90, 0, 100, true, "مصر", PHOTO_SALAH, CREST_LIVERPOOL, "🇪🇬", "ICON"),
        Player("p_yamal", "لامين يامال", "barcelona", "FC Barcelona", "RW", 18, 88, 96, 93, 84, 86, 92, 35, 68, 92, 160_000_000L, 180_000L, 95, 94, 10, 100, true, "إسبانيا", PHOTO_YAMAL, CREST_BARCELONA, "🇪🇸", "FUTURE_STAR")
    )

    val newsList = listOf(
        NewsItem(
            id = "n_1",
            title = "ريال مدريد ومانشستر سيتي في صدام ناري مرتقب",
            summary = "التشكيلات الرسمية وخطة الهجوم المضاد بقيادة مبابي لمواجهة خط وسط السيتي بقيادة دي بروين ورودري.",
            category = "دوري الأبطال | قمة البرنابيو",
            timeAgo = "منذ 15 دقيقة",
            tag = "UCL",
            location = "سانتياغو برنابيو",
            imageUrl = PHOTO_MBAPPE
        ),
        NewsItem(
            id = "n_2",
            title = "ليفربول يكثف المفاوضات لتمديد عقد محمد صلاح براتب قياسي",
            summary = "إدارة الريدز تقترب من التوصل لاتفاق نهائي لإبقاء قائد الهجوم في أنفيلد لموسمين إضافيين.",
            category = "عاجل | صفقات وتجديد عقود",
            timeAgo = "منذ 38 دقيقة",
            tag = "LFC",
            location = "أنفيلد رود",
            imageUrl = PHOTO_SALAH
        ),
        NewsItem(
            id = "n_3",
            title = "هالاند يكسر حاجز الـ 75 هدفاً في أسرع حصيلة بتاريخ البريميرليغ",
            summary = "المهاجم النرويجي يواصل كتابة التاريخ التهديفي بعد ثنائية مذهلة بمعدل 0.98 هدف لكل مباراة.",
            category = "إحصائيات رسمية | رقم قياسي",
            timeAgo = "منذ ساعتين",
            tag = "75 GOALS",
            location = "ملعب الاتحاد",
            imageUrl = PHOTO_HAALAND
        ),
        NewsItem(
            id = "n_4",
            title = "ميسي ورونالدو يواصلان كتابة التاريخ عبر القارات",
            summary = "سجل تهديفي يتخطى حاجز الـ 850 هدفاً وملاحم مستمرة في الدوري الأمريكي ودوري روشن للمحترفين.",
            category = "الجوائز العالمية والأرقام الخالدة",
            timeAgo = "منذ 3 ساعات",
            tag = "GOAT",
            location = "فيفا الرسمي",
            imageUrl = PHOTO_MESSI
        )
    )

    val friends = listOf(
        FriendItem("f1", "Youssef", "Y", "ONLINE", "متصل"),
        FriendItem("f2", "Omar", "O", "ONLINE", "متصل"),
        FriendItem("f3", "Ahmed", "A", "IN_MATCH", "في مباراة"),
        FriendItem("f4", "Mostafa", "M", "ONLINE", "متصل")
    )

    val leaderboard = listOf(
        LeaderboardUser(1, "The King", "🥇", "المتصدر الأسبوعي", 12450),
        LeaderboardUser(2, "Galaxy FC", "🥈", "المركز الثاني", 11230),
        LeaderboardUser(3, "Legends", "🥉", "المركز الثالث", 10980),
        LeaderboardUser(4, "Offside United", "4", "المركز الرابع", 9760),
        LeaderboardUser(5, "Real Dream", "5", "المركز الخامس", 8420)
    )

    val gameModes = listOf(
        GameModeItem("fire_xi", "التشكيلة النارية", "FIRE XI", "فردي", "local_fire_department", 0xFFEF4444),
        GameModeItem("blitz_30", "سباق الـ30 ثانية", "BLITZ 30", "فردي", "timer", 0xFFF5A623),
        GameModeItem("overdrive_70", "تحدي الـ70 ثانية", "OVERDRIVE 70", "فردي", "pace", 0xFF3B82F6),
        GameModeItem("daily_xi", "تحدي اليوم", "DAILY XI", "فردي", "event_available", 0xFF10B981),
        GameModeItem("football_code", "شفرة الكرة", "FOOTBALL CODE", "ثنائي", "sentiment_very_satisfied", 0xFFFCD34D),
        GameModeItem("value_battle", "معركة القيمة", "VALUE BATTLE", "ثنائي", "monetization_on", 0xFFF5A623),
        GameModeItem("mystery_xi", "اللاعب الغامض", "MYSTERY XI", "متعدد اللاعبين", "person_search", 0xFF8B5CF6),
        GameModeItem("number_clash", "صدام الأرقام", "NUMBER CLASH", "متعدد اللاعبين", "pin", 0xFF3B82F6),
        GameModeItem("link_xi", "سلسلة النجوم", "LINK XI", "فردي", "link", 0xFF94A3B8),
        GameModeItem("name_rush", "سباق الأسماء", "NAME RUSH", "فردي", "bolt", 0xFFF5A623),
        GameModeItem("fact_or_foul", "حقيقة أم خطأ", "FACT OR FOUL", "فردي", "check_box", 0xFF10B981),
        GameModeItem("no_go_words", "ممنوع النطق", "NO-GO WORDS", "ثنائي", "block", 0xFFEF4444),
        GameModeItem("the_spy", "الجاسوس", "THE SPY", "متعدد اللاعبين", "visibility", 0xFF64748B),
        GameModeItem("hidden_star", "النجم الخفي", "HIDDEN STAR", "فردي", "star", 0xFFFCD34D),
        GameModeItem("legends_market", "سوق الأساطير", "LEGENDS MARKET", "ثنائي", "gavel", 0xFFF5A623),
        GameModeItem("secret_draft", "الاختيار السري", "SECRET DRAFT", "ثنائي", "shield", 0xFF3B82F6),
        GameModeItem("the_deal", "الصفقة", "THE DEAL", "فردي", "handshake", 0xFFF5A623),
        GameModeItem("player_market", "سوق اللاعبين", "PLAYER MARKET", "فردي", "trending_up", 0xFF10B981)
    )

    val matches = listOf(
        MatchRecord(
            id = "m1",
            weekNumber = 1,
            homeClubId = "real_madrid",
            homeClubName = "Real Madrid",
            awayClubId = "barcelona",
            awayClubName = "FC Barcelona",
            homeScore = 3,
            awayScore = 1,
            isFinished = true,
            possessionHome = 58,
            shotsHome = 14,
            shotsAway = 8,
            offsidesHome = 2,
            offsidesAway = 4,
            foulsHome = 7,
            foulsAway = 11,
            homeFormation = "4-3-3",
            awayFormation = "4-2-3-1",
            homeTacticalStyle = "Gegenpressing",
            awayTacticalStyle = "High Line Tiki-Taka",
            offsideTrapActive = true,
            smartOffsideAnalysis = "تحليل الأوفسايد الذكي: نجح خط الدفاع في نصب مصيدة التسلل 4 مرات ضد برشلونة بفضل التزامن الممتاز بين روديغر وميليتاو. ومع ذلك، هناك ثغرة في الدقيقة 73 عندما تأخر الظهير في الصعود، مما كاد يكلف هدفاً. التوصية: زيادة تدريبات التوافق الدفاعي للحفاظ على خط متساوٍ ومفاجأة الخصم بالضغط المتوسط.",
            tacticalScoreRating = 91,
            matchEventsJson = "[\"12' هدف: مبابي بتسديدة صاروخية\", \"34' تسلل على ليفاندوفسكي\", \"51' هدف: فينيسيوس جونيور\", \"68' هدف: ميسي يقلص الفارق\", \"84' هدف: بيلينجهام ينهي اللقاء\"]",
            matchDateText = "الأسبوع 1 - كلاسيكو الدوري"
        ),
        MatchRecord(
            id = "m2",
            weekNumber = 2,
            homeClubId = "real_madrid",
            homeClubName = "Real Madrid",
            awayClubId = "man_city",
            awayClubName = "Manchester City",
            homeScore = 0,
            awayScore = 0,
            isFinished = false,
            homeFormation = "4-3-3",
            awayFormation = "4-3-3",
            homeTacticalStyle = "Gegenpressing",
            awayTacticalStyle = "Positional Play",
            offsideTrapActive = true,
            matchDateText = "الأسبوع 2 - قمة دوري أبطال أوروبا القادمة"
        )
    )

    val dailyChallenges = listOf(
        DailyChallenge(
            id = "c1",
            title = "Master the Offside Trap",
            titleAr = "إتقان مصيدة التسلل",
            description = "Force the opponent into 3 or more offsides in a single match.",
            descriptionAr = "أوقع الخصم في التسلل 3 مرات على الأقل في مباراة واحدة.",
            currentProgress = 4,
            targetGoal = 3,
            rewardCoins = 15_000L,
            rewardXp = 350,
            isCompleted = true,
            isClaimed = false,
            categoryIcon = "🚩"
        ),
        DailyChallenge(
            id = "c2",
            title = "Zero Offsides In Attack",
            titleAr = "هجوم بلا تسلل",
            description = "Finish a match with 0 or 1 offside caught against your strikers.",
            descriptionAr = "أنهِ المباراة بتسلل واحد أو أقل لتطبيق التوقيت المثالي للجري السريع.",
            currentProgress = 1,
            targetGoal = 1,
            rewardCoins = 12_000L,
            rewardXp = 250,
            isCompleted = false,
            isClaimed = false,
            categoryIcon = "⚡"
        )
    )

    val notifications = listOf(
        NotificationAlert(
            id = "n1",
            title = "صافرة النهاية: فوز ملحمي لريال مدريد!",
            message = "ريال مدريد 3 - 1 برشلونة. تحليل الأوفسايد الذكي جاهز للاطلاع في غرفة المدرب.",
            type = "MATCH_RESULT",
            iconEmoji = "🏆"
        ),
        NotificationAlert(
            id = "n2",
            title = "إعلان التشكيلة الرسمية للمباراة القادمة",
            message = "تم تأكيد التشكيلة الرسمية (4-3-3) ضد مانشستر سيتي مع تفعيل مصيدة التسلل الصارمة.",
            type = "LINEUP",
            iconEmoji = "📋"
        )
    )
}
