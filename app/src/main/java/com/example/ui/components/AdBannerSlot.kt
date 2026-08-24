package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AdBannerSlot(
    modifier: Modifier = Modifier,
    label: String = "--- [ SYSTEM MONETIZATION & AD SLOT RESERVED ] ---",
    placeholderText: String = "[ AD BANNER PLACEHOLDER ]",
    heightDp: Int = 26
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 2.dp)
            .border(
                1.dp,
                CyanPrimary.copy(alpha = 0.35f),
                RoundedCornerShape(6.dp)
            )
            .background(Color(0xFF020713).copy(alpha = 0.8f), RoundedCornerShape(6.dp))
            .padding(3.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontFamily = FontFamily.Monospace,
            fontSize = 7.sp,
            color = TextSecondary,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(bottom = 2.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(heightDp.dp)
                .background(DarkCardSurface.copy(alpha = 0.9f), RoundedCornerShape(4.dp))
                .border(1.dp, PurplePrimary.copy(alpha = 0.35f), RoundedCornerShape(4.dp))
                .testTag("ad_banner_container"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = placeholderText,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 8.sp,
                letterSpacing = 1.sp,
                color = PurplePrimary
            )
        }
    }
}
