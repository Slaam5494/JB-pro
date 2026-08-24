package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AdBannerSlot
import com.example.ui.components.BottomModuleNavBar
import com.example.ui.components.LuminaHeader
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkCardSecondary
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AppScreen

data class ModuleCardData(
    val screen: AppScreen,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color,
    val hasAutoBadge: Boolean = false
)

@Composable
fun DashboardScreen(
    isOnline: Boolean,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val modules = listOf(
        ModuleCardData(
            screen = AppScreen.VIDEO_GEN,
            title = "VIDEO GEN",
            description = "9D FX & Neural Synth",
            icon = Icons.Default.Movie,
            accentColor = CyanPrimary
        ),
        ModuleCardData(
            screen = AppScreen.IMAGE_GEN,
            title = "IMAGE GEN",
            description = "Holo Studio & Prompts",
            icon = Icons.Default.Palette,
            accentColor = CyanPrimary
        ),
        ModuleCardData(
            screen = AppScreen.FABRICATION,
            title = "FABRICATION",
            description = "360° Level & AR Tape",
            icon = Icons.Default.DesignServices,
            accentColor = CyanPrimary
        ),
        ModuleCardData(
            screen = AppScreen.CHAT,
            title = "AI CHAT",
            description = "Gemini Neural Interface",
            icon = Icons.Default.Chat,
            accentColor = PurplePrimary
        ),
        ModuleCardData(
            screen = AppScreen.PROMPT_STUDIO,
            title = "PROMPT STUDIO",
            description = "Live Cloud & Web Prompts",
            icon = Icons.Default.AutoAwesome,
            accentColor = EmeraldAccent,
            hasAutoBadge = true
        ),
        ModuleCardData(
            screen = AppScreen.SETTINGS,
            title = "SETTINGS",
            description = "Preferences & Config",
            icon = Icons.Default.Settings,
            accentColor = CyanPrimary
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top: Header
        LuminaHeader(isOnline = isOnline)

        // Middle: Modules Grid
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "[ SYSTEM MODULES ]",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                color = TextSecondary,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
            )

            // 3x2 Grid of cards
            for (row in 0 until 3) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (col in 0 until 2) {
                        val index = row * 2 + col
                        if (index < modules.size) {
                            val module = modules[index]
                            HoloModuleCard(
                                module = module,
                                onClick = { onNavigate(module.screen) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Bottom Section: Ad Slot + 6 Button Nav Bar
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            AdBannerSlot()
            BottomModuleNavBar(
                currentScreen = AppScreen.DASHBOARD,
                onNavigate = onNavigate
            )
        }
    }
}

@Composable
fun HoloModuleCard(
    module: ModuleCardData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cardGlow")
    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "borderAlpha"
    )

    val gradientBrush = when (module.screen) {
        AppScreen.CHAT -> Brush.linearGradient(
            listOf(
                PurplePrimary.copy(alpha = 0.25f),
                DarkCardSurface.copy(alpha = 0.95f)
            )
        )
        AppScreen.PROMPT_STUDIO -> Brush.linearGradient(
            listOf(
                EmeraldAccent.copy(alpha = 0.20f),
                DarkCardSurface.copy(alpha = 0.95f)
            )
        )
        else -> Brush.linearGradient(
            listOf(
                DarkCardSecondary.copy(alpha = 0.75f),
                Color(0xFF030915).copy(alpha = 0.95f)
            )
        )
    }

    Box(
        modifier = modifier
            .height(92.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(gradientBrush)
            .border(
                width = 1.dp,
                color = module.accentColor.copy(alpha = borderAlpha),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(8.dp)
            .testTag("holo_card_${module.title.lowercase().replace(' ', '_')}"),
        contentAlignment = Alignment.TopStart
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Card Top: Icon & Live Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = module.icon,
                    contentDescription = module.title,
                    tint = module.accentColor,
                    modifier = Modifier.size(20.dp)
                )

                if (module.hasAutoBadge) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(3.dp))
                            .background(EmeraldAccent.copy(alpha = 0.15f))
                            .border(1.dp, EmeraldAccent, RoundedCornerShape(3.dp))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "AUTO",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 6.sp,
                            color = EmeraldAccent
                        )
                    }
                }
            }

            // Card Bottom: Title & Description
            Column {
                Text(
                    text = module.title,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 9.5.sp,
                    letterSpacing = 0.8.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = module.description,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 7.sp,
                    color = TextCyan.copy(alpha = 0.85f),
                    maxLines = 1
                )
            }
        }
    }
}
