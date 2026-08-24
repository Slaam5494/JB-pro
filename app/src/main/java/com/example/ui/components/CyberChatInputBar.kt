package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

val DEFAULT_CYBER_PROMPTS = listOf(
    "⚡ Fabrication status",
    "◈ Explain 9D FX",
    "⚡ Calibrate sensors",
    "◈ Storage diagnostics",
    "⚡ Neural Code Gen",
    "◈ Quantum Matrix",
    "⚡ AR Tape Guide"
)

@Composable
fun CyberChatInputBar(
    inputText: String,
    onValueChange: (String) -> Unit,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Type cyber query...",
    quickPrompts: List<String> = DEFAULT_CYBER_PROMPTS,
    onQuickPromptSelected: ((String) -> Unit)? = null,
    enabled: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "borderGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("cyber_chat_input_bar")
    ) {
        // Quick Action Chips Bar (Situated right above input bar)
        if (quickPrompts.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
                    .testTag("quick_action_chips_row"),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(quickPrompts) { prompt ->
                    val cleanPrompt = prompt.replace("⚡ ", "").replace("◈ ", "").trim()
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(DarkCardSurface.copy(alpha = 0.9f))
                            .border(
                                width = 1.dp,
                                color = CyanPrimary.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(color = CyanPrimary)
                            ) {
                                if (onQuickPromptSelected != null) {
                                    onQuickPromptSelected(cleanPrompt)
                                } else {
                                    onSendMessage(cleanPrompt)
                                }
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = prompt,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium,
                            fontSize = 8.5.sp,
                            color = CyanPrimary,
                            letterSpacing = 0.3.sp
                        )
                    }
                }
            }
        }

        // Unified Glowing Input Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .border(
                    width = 1.2.dp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            CyanPrimary.copy(alpha = glowAlpha),
                            PurplePrimary.copy(alpha = glowAlpha * 0.8f),
                            CyanPrimary.copy(alpha = glowAlpha)
                        )
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                .background(Color(0xFF040A1A).copy(alpha = 0.95f))
                .padding(horizontal = 6.dp, vertical = 5.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Text Area with Monospace placeholder
                OutlinedTextField(
                    value = inputText,
                    onValueChange = onValueChange,
                    enabled = enabled,
                    placeholder = {
                        Text(
                            text = placeholder,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = TextSecondary.copy(alpha = 0.7f),
                            letterSpacing = 0.3.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.5.sp,
                        color = TextPrimary
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = CyanPrimary
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (inputText.isNotBlank()) {
                                onSendMessage(inputText.trim())
                            }
                        }
                    )
                )

                // Distinct Cyan / Neon Send Button with Dark Arrow Icon
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(8.dp),
                            ambientColor = CyanPrimary,
                            spotColor = CyanGlow
                        )
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    CyanPrimary,
                                    Color(0xFF00B4D8)
                                )
                            )
                        )
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable(
                            enabled = enabled && inputText.isNotBlank(),
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = Color.Black)
                        ) {
                            if (inputText.isNotBlank()) {
                                onSendMessage(inputText.trim())
                            }
                        }
                        .testTag("chat_send_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Cyber Query",
                        tint = Color(0xFF020713),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
