package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkCardSecondary
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AppScreen

data class NavItem(
    val screen: AppScreen,
    val label: String,
    val icon: ImageVector
)

@Composable
fun BottomModuleNavBar(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val navItems = listOf(
        NavItem(AppScreen.VIDEO_GEN, "VIDEO", Icons.Default.Movie),
        NavItem(AppScreen.IMAGE_GEN, "IMAGE", Icons.Default.Palette),
        NavItem(AppScreen.FABRICATION, "FABRIC", Icons.Default.DesignServices),
        NavItem(AppScreen.CHAT, "AI CHAT", Icons.Default.Chat),
        NavItem(AppScreen.PROMPT_STUDIO, "PROMPT", Icons.Default.AutoAwesome),
        NavItem(AppScreen.SETTINGS, "SETTINGS", Icons.Default.Settings)
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .border(
                1.dp,
                Brush.horizontalGradient(
                    listOf(
                        CyanPrimary.copy(alpha = 0.5f),
                        PurplePrimary.copy(alpha = 0.4f),
                        CyanPrimary.copy(alpha = 0.5f)
                    )
                ),
                RoundedCornerShape(8.dp)
            )
            .background(
                Brush.verticalGradient(
                    listOf(
                        DarkCardSurface.copy(alpha = 0.95f),
                        Color(0xFF020713).copy(alpha = 0.98f)
                    )
                ),
                RoundedCornerShape(8.dp)
            )
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        navItems.forEach { item ->
            val isSelected = currentScreen == item.screen
            val activeColor = when (item.screen) {
                AppScreen.CHAT -> PurplePrimary
                AppScreen.PROMPT_STUDIO -> EmeraldAccent
                else -> CyanPrimary
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (isSelected) activeColor.copy(alpha = 0.22f) else DarkCardSecondary.copy(alpha = 0.5f)
                    )
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) activeColor else CyanPrimary.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .clickable { onNavigate(item.screen) }
                    .padding(vertical = 3.dp)
                    .testTag("nav_btn_${item.label.lowercase()}"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (isSelected) activeColor else TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.label,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                        fontSize = 7.sp,
                        color = if (isSelected) TextPrimary else TextSecondary,
                        letterSpacing = 0.3.sp
                    )
                }
            }
        }
    }
}
