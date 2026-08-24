package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.PurpleGlow
import com.example.ui.theme.PurplePrimary

@Composable
fun LuminaHeader(
    isOnline: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "logoPulse")
    val logoScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoScale"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        CyanPrimary.copy(alpha = 0.5f),
                        PurplePrimary.copy(alpha = 0.4f),
                        CyanPrimary.copy(alpha = 0.5f)
                    )
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .background(DarkCardSurface.copy(alpha = 0.85f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Logo + Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.testTag("lumina_header_logo_group")
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .border(
                        1.dp,
                        Brush.linearGradient(listOf(CyanPrimary, PurplePrimary)),
                        RoundedCornerShape(6.dp)
                    )
                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(6.dp), spotColor = CyanPrimary)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_lumina_logo),
                    contentDescription = "Lumina Logo",
                    modifier = Modifier.size(34.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "LUMINA AI",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                letterSpacing = 1.6.sp,
                color = CyanPrimary,
                modifier = Modifier.testTag("app_title_text")
            )
        }

        // Right: Status Badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = if (isOnline) CyanPrimary.copy(alpha = 0.5f) else DangerRed.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp)
                )
                .background(
                    if (isOnline) CyanPrimary.copy(alpha = 0.08f) else DangerRed.copy(alpha = 0.08f),
                    RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 8.dp, vertical = 3.dp)
                .testTag("network_status_badge")
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (isOnline) EmeraldAccent else DangerRed)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (isOnline) "JB_V2.5®(online)" else "JB_V2.5®(offline)",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 8.sp,
                letterSpacing = 0.5.sp,
                color = if (isOnline) CyanPrimary else DangerRed
            )
        }
    }
}
