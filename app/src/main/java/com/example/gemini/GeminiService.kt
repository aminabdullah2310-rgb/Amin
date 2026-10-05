package com.example.gemini

import com.example.BuildConfig
import com.example.model.MatchRecord
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// --- DTOs for Gemini REST API ---

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val parts: List<GeminiPart>,
    val role: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
    val text: String
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent? = null
)

interface GeminiApi {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiService {

    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val api: GeminiApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApi::class.java)
    }

    suspend fun analyzeMatchOffsideAndTactics(
        match: MatchRecord,
        defensiveLineHeight: Int,
        offsideTrapActive: Boolean,
        managerTactic: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY.trim()

        val prompt = """
            أنت "محلل الأوفسايد والتكتيك الذكي" (Smart Offside & Tactical Analyst) في طور المسيرة للعبة كرة القدم على طراز FC 25 / PES.
            قم بتحليل المباراة التالية وقدم تقريراً تكتيكياً فائق الاحترافية باللغة العربية موجهاً للمدرب مباشرة:
            
            بيانات المباراة:
            - المباراة: ${match.homeClubName} (${match.homeScore}) ضد ${match.awayClubName} (${match.awayScore})
            - الاستحواذ: ${match.homeClubName} ${match.possessionHome}% مقابل ${100 - match.possessionHome}%
            - التسديدات: ${match.shotsHome} مقابل ${match.shotsAway}
            - حالات التسلل (Offsides):
              * وقع فريق ${match.homeClubName} في التسلل: ${match.offsidesHome} مرات
              * وقع فريق ${match.awayClubName} في التسلل: ${match.offsidesAway} مرات
            - تشكيلة فريقك: ${match.homeFormation}
            - فلسفة اللعب: $managerTactic
            - ارتفاع خط الدفاع: $defensiveLineHeight%
            - تفعيل مصيدة التسلل (Offside Trap): ${if (offsideTrapActive) "مفعلة بنظام الخط الواحد" else "معطلة - التراجع السريع"}
            
            المطلوب في تقريرك:
            1. 🚩 **تشخيص مصيدة التسلل وعمق الخط الخلفي**: تحليل دقيق لمدى نجاح الدفاع في كشف مهاجمي الخصم، وهل كان صعود المدافعين متزامناً أم تسبب في انفرادات.
            2. ⚡ **توقيت انطلاقات المهاجمين (Avoid Offside)**: تقييم سرعة التمرير البيني وكسر التسلل في الهجوم.
            3. 📋 **3 نصائح تكتيكية فورية للمدرب**: توجيهات عملية لتعديل عمق الدفاع والضغط والانتشار للمباريات القادمة.
            4. 🎯 **تقييم الأداء التكتيكي العام (من 100)** مع جملة تحفيزية للمدرب.
            
            اكتب الرد بأسلوب تكتيكي مشوق وواضح وممتع مثل استوديوهات التحليل الكبرى.
        """.trimIndent()

        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            // Intelligent local analytical fallback when API key is not yet set
            return@withContext generateFallbackTacticalAnalysis(match, defensiveLineHeight, offsideTrapActive, managerTactic)
        }

        try {
            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        parts = listOf(GeminiPart(text = prompt))
                    )
                ),
                systemInstruction = GeminiContent(
                    parts = listOf(
                        GeminiPart(
                            text = "أنت خبير تكتيكي عالمي ومحلل حالات التسلل الذكي في كرة القدم الاحترافية. تقدم نصائح عملية دقيقة للمدربين لتحسين مصيدة التسلل والانتشار الهجومي."
                        )
                    )
                )
            )
            val response = api.generateContent(apiKey, request)
            val resultText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            resultText ?: generateFallbackTacticalAnalysis(match, defensiveLineHeight, offsideTrapActive, managerTactic)
        } catch (e: Exception) {
            generateFallbackTacticalAnalysis(match, defensiveLineHeight, offsideTrapActive, managerTactic)
        }
    }

    suspend fun chatWithTacticalAnalyst(
        userQuestion: String,
        clubName: String,
        currentFormation: String,
        currentTactic: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY.trim()

        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext generateFallbackChatAnswer(userQuestion)
        }

        val prompt = """
            أنت مساعد المدرب ومحلل الأوفسايد والتكتيك الذكي لنادي $clubName.
            التشكيلة المعتمدة حالياً: $currentFormation
            الأسلوب التكتيكي: $currentTactic
            
            المدرب يسألك:
            "$userQuestion"
            
            أجب المدرب بنصيحة تكتيكية مركزة وعميقة ومحفزة باللغة العربية مع خطوات تنفيذية على أرضية الملعب.
        """.trimIndent()

        try {
            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        parts = listOf(GeminiPart(text = prompt))
                    )
                )
            )
            val response = api.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: generateFallbackChatAnswer(userQuestion)
        } catch (e: Exception) {
            generateFallbackChatAnswer(userQuestion)
        }
    }

    private fun generateFallbackTacticalAnalysis(
        match: MatchRecord,
        defensiveLineHeight: Int,
        offsideTrapActive: Boolean,
        managerTactic: String
    ): String {
        val trapStatus = if (offsideTrapActive && match.offsidesAway >= 2) {
            "نجحت مصيدة التسلل في شل حركة مهاجمي ${match.awayClubName} وأوقعتهم في التسلل ${match.offsidesAway} مرات بنجاح لافت."
        } else if (match.offsidesAway == 0) {
            "لم تنجح مصيدة التسلل اليوم بالشكل المطلوب، حيث استطاع الخصم اختراق الخط الدفاعي دون الوقوع في أي راية تسلل."
        } else {
            "أظهر الخط الخلفي انضباطاً متوسطاً في تطبيق كسر التسلل، لكن ظهرت بعض الفجوات عند هجمات الخصم المرتدة."
        }

        val attackingOffside = if (match.offsidesHome >= 3) {
            "تحذير هجومي: وقع مهاجموك في التسلل ${match.offsidesHome} مرات بسبب التسرع في الانطلاق قبل لحظة التمرير الدقيقة."
        } else {
            "انضباط هجومي ممتاز: التوقيت الحركي لمهاجميك كان مثالياً لكسر التكتل دون الوقوع المتكرر في التسلل."
        }

        return """
            📊 **تقرير محلل الأوفسايد والتكتيك الذكي (Offside AI Hub)**:
            
            1. 🚩 **تشخيص مصيدة التسلل والدفاع:**
            $trapStatus
            - ارتفاع خط الدفاع (${defensiveLineHeight}%) ساهم في حصر اللعب في نصف ملعب الخصم مع السيطرة على ${match.possessionHome}% من الاستحواذ.
            
            2. ⚡ **تحليل الشق الهجومي:**
            $attackingOffside
            
            3. 📋 **توصيات المدرب للمواجهة المقبلة:**
            • ضبط التزامن بين قلبي الدفاع (Center Backs) بحيث يتقدمان ككتلة واحدة عند خروج الكرة من أرجل صناع لعب الخصم.
            • تفعيل تدريب "توقيت الجري السريع" للمهاجمين لتقليل قرارات التسلل العكسية.
            • استغلال الكرات الطولية خلف ظهيري الخصم عند تطبيقهم الضغط العالي.
            
            🎯 **التقييم التكتيكي:** 88/100
            "فريقك يمتلك هوية تكتيكية قوية وشخصية بطل! واصل التطور التكتيكي في الحصص التدريبية."
        """.trimIndent()
    }

    private fun generateFallbackChatAnswer(question: String): String {
        return when {
            question.contains("دفاع") || question.contains("أوفسايد") || question.contains("تسلل") -> {
                "نصيحة المحلل: لتطبيق مصيدة تسلل متقنة، ارفع خط الدفاع إلى أكثر من 70% وفعل خيار 'مصيدة التسلل'، مع الاعتماد على مدافعين يتمتعون بسرعة استرداد تفوق 80 (مثل روديغر أو ميليتاو)."
            }
            question.contains("هجوم") || question.contains("تسجيل") -> {
                "نصيحة المحلل: أفضل طريقة لكسر الدفاعات المتكتلة هي الاعتماد على التمرير السريع ولمسة واحدة (One-Touch) مع توجيه الأجنحة لقطع المساحة إلى الداخل وفتح ممر التمرير للمهاجم الصريح."
            }
            question.contains("هالاند") || question.contains("مهاجم") -> {
                "نصيحة المحلل: لإيقاف المهاجمين الأقوياء بدنياً، عيّن لاعب ارتكاز (CDM) للتغطية أمام قلبي الدفاع، مع استخدام المراقبة المشددة وتجنب مصيدة التسلل المتأخرة التي قد تمنحه انفراداً مباشراً."
            }
            else -> {
                "رأي المحلل التكتيكي: يعتمد النجاح في المسيرة على المرونة التكتيكية. أنصحك دائماً بمراجعة إحصائيات التسلل والاستحواذ بعد كل شوط، وتكثيف تدريبات اللاعبين الشبان لرفع طاقتهم البدنية."
            }
        }
    }
}
