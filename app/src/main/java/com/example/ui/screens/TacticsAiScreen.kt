package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricGold
import com.example.ui.theme.PitchGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.CareerUiState
import com.example.viewmodel.ChatMessage

@Composable
fun TacticsAiScreen(
    uiState: CareerUiState,
    onSendMessage: (String) -> Unit
) {
    var inputQuery by remember { mutableStateOf("") }

    val quickQuestions = listOf(
        "كيف أضبط مصيدة التسلل ضد المهاجمين السريعين؟ 🚩",
        "ما هو التكتيك المثالي لكسر التكتل الدفاعي؟ 🎯",
        "كيف أوقف خطورة إيرلينغ هالاند في اللقاء القادم؟ 🛡️",
        "متى أرفع خط الدفاع إلى أكثر من 75%؟ 📐"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSurface)
            .padding(16.dp)
            .testTag("screen_tactics_ai")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.tactics_board_1791217341223),
                contentDescription = "Tactics AI",
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, PitchGreen, CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "محلل الأوفسايد والتكتيك الذكي",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ElectricGold, modifier = Modifier.size(16.dp))
                }
                Text(
                    text = "استشارات تكتيكية مخصصة لناديك عبر Gemini 3.5 Flash",
                    fontSize = 12.sp,
                    color = PitchGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Quick Consultation Chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(quickQuestions) { question ->
                Box(
                    modifier = Modifier
                        .background(Color(0xFF0F172A), RoundedCornerShape(12.dp))
                        .border(1.dp, PitchGreen.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .clickable { onSendMessage(question) }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(text = question, fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Chat Message History
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(DarkSurfaceElevated, RoundedCornerShape(16.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(uiState.chatMessages) { message ->
                ChatBubble(message = message)
            }

            if (uiState.isChatLoading) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = PitchGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "محلل الأوفسايد يراجع خطة التسلل ويكتب التحليل التكتيكي...",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Input Field
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputQuery,
                onValueChange = { inputQuery = it },
                placeholder = { Text("اكتب سؤالك التكتيكي للمحلل الذكي...", fontSize = 13.sp, color = TextSecondary) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_tactics_chat_input"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkSurfaceCard,
                    unfocusedContainerColor = DarkSurfaceCard,
                    focusedBorderColor = PitchGreen,
                    unfocusedBorderColor = Color(0xFF334155),
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (inputQuery.isNotBlank()) {
                        onSendMessage(inputQuery)
                        inputQuery = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(PitchGreen, CircleShape)
                    .testTag("send_ai_chat_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = Color.Black
                )
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val isManager = message.sender == "MANAGER"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isManager) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .background(
                    if (isManager) Color(0xFF15803D) else Color(0xFF0F172A),
                    RoundedCornerShape(
                        topStart = 14.dp,
                        topEnd = 14.dp,
                        bottomStart = if (isManager) 14.dp else 2.dp,
                        bottomEnd = if (isManager) 2.dp else 14.dp
                    )
                )
                .border(
                    1.dp,
                    if (isManager) PitchGreen else Color(0xFF22C55E).copy(alpha = 0.2f),
                    RoundedCornerShape(14.dp)
                )
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = if (isManager) "أنت (المدرب الفني) 👔" else "محلل الأوفسايد والتكتيك الذكي (Gemini) 🧠",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isManager) Color(0xFFDCFCE7) else ElectricGold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.text,
                    fontSize = 13.sp,
                    color = Color.White,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
