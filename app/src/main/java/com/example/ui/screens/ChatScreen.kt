package com.example.ui.screens

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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.ui.components.AdBannerSlot
import com.example.ui.components.CyberChatInputBar
import com.example.ui.components.CyberSubHeader
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DarkCardSecondary
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AiChatMode(val displayName: String, val badge: String) {
    LUMINA_NEURAL("LUMINA 9D AI", "ONLINE"),
    GEMINI_MATRIX("GEMINI FLASH AI", "READY"),
    AR_FABRICATION("AR FABRICATION AI", "CONNECTED")
}

@Composable
fun ChatScreen(
    messages: List<ChatMessage>,
    onSendMessage: (String) -> Unit,
    onClearChat: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    var activeMode by remember { mutableStateOf(AiChatMode.LUMINA_NEURAL) }
    val listState = rememberLazyListState()

    val quickPrompts = remember(activeMode) {
        when (activeMode) {
            AiChatMode.LUMINA_NEURAL -> listOf(
                "⚡ Fabrication status",
                "◈ Explain 9D FX",
                "⚡ Calibrate sensors",
                "◈ Storage diagnostics",
                "⚡ Quantum Matrix"
            )
            AiChatMode.GEMINI_MATRIX -> listOf(
                "⚡ Gemini reasoning",
                "◈ Multimodal analysis",
                "⚡ Generate Python shader",
                "◈ Analyze measurement log",
                "⚡ Synthesize prompt"
            )
            AiChatMode.AR_FABRICATION -> listOf(
                "⚡ AR Tape precision",
                "◈ 360° Spirit Level tolerance",
                "⚡ AI object counting guide",
                "◈ Save measurement export",
                "⚡ Calibrate distance"
            )
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Header
        CyberSubHeader(title = "AI CHAT ROOM", onBack = onBack)

        // FIXED TOP AD BANNER
        AdBannerSlot(
            label = "--- ADVERTISEMENT ---",
            placeholderText = "[ TOP AD BANNER ]",
            heightDp = 18
        )

        // AI Engine Mode Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AiChatMode.entries.forEach { mode ->
                val isSelected = activeMode == mode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (isSelected) PurplePrimary.copy(alpha = 0.25f) else DarkCardSurface
                        )
                        .border(
                            1.dp,
                            if (isSelected) CyanPrimary else PurplePrimary.copy(alpha = 0.35f),
                            RoundedCornerShape(6.dp)
                        )
                        .clickable { activeMode = mode }
                        .padding(vertical = 4.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = mode.displayName,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 7.5.sp,
                        color = if (isSelected) CyanPrimary else TextSecondary,
                        maxLines = 1
                    )
                }
            }
        }

        // Main Chat Box Container
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 3.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, PurplePrimary.copy(alpha = 0.45f), RoundedCornerShape(8.dp))
                .background(Color(0xFF020713).copy(alpha = 0.92f))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header bar in chat
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(EmeraldAccent)
                    )
                    Text(
                        text = "${activeMode.displayName} (${activeMode.badge} • ROOM DB)",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 7.5.sp,
                        color = PurplePrimary
                    )
                }

                IconButton(
                    onClick = onClearChat,
                    modifier = Modifier
                        .size(24.dp)
                        .testTag("btn_clear_chat")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear Chat",
                        tint = DangerRed.copy(alpha = 0.85f),
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            // Scrollable Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 2.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { msg ->
                    ChatMessageBubble(message = msg)
                }
            }

            // Reusable Unified Cyber Chat Input Bar
            CyberChatInputBar(
                inputText = inputText,
                onValueChange = { inputText = it },
                onSendMessage = { query ->
                    onSendMessage(query)
                    inputText = ""
                },
                placeholder = "Type cyber query...",
                quickPrompts = quickPrompts,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // FIXED BOTTOM AD BANNER
        AdBannerSlot(
            label = "--- ADVERTISEMENT ---",
            placeholderText = "[ BOTTOM AD BANNER ]",
            heightDp = 18
        )
    }
}

@Composable
fun ChatMessageBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    val isUser = message.isUser
    val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp))

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
            modifier = Modifier.fillMaxWidth(0.88f)
        ) {
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 8.dp,
                            topEnd = 8.dp,
                            bottomStart = if (isUser) 8.dp else 2.dp,
                            bottomEnd = if (isUser) 2.dp else 8.dp
                        )
                    )
                    .background(
                        if (isUser) CyanPrimary.copy(alpha = 0.16f) else PurplePrimary.copy(alpha = 0.16f)
                    )
                    .border(
                        1.dp,
                        if (isUser) CyanPrimary.copy(alpha = 0.8f) else PurplePrimary.copy(alpha = 0.8f),
                        RoundedCornerShape(
                            topStart = 8.dp,
                            topEnd = 8.dp,
                            bottomStart = if (isUser) 8.dp else 2.dp,
                            bottomEnd = if (isUser) 2.dp else 8.dp
                        )
                    )
                    .padding(horizontal = 9.dp, vertical = 7.dp)
            ) {
                Text(
                    text = message.text,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.5.sp,
                    color = if (isUser) TextPrimary else TextCyan,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$timeStr • ${if (isUser) "YOU" else "LUMINA NEURAL"}",
                fontFamily = FontFamily.Monospace,
                fontSize = 6.5.sp,
                color = TextSecondary
            )
        }
    }
}
