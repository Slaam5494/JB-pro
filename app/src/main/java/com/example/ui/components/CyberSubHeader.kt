package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.PurplePrimary

@Composable
fun CyberSubHeader(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp)
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
            .background(DarkCardSurface.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(CyanPrimary.copy(alpha = 0.12f))
                .border(1.dp, CyanPrimary, RoundedCornerShape(6.dp))
                .clickable { onBack() }
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .testTag("back_button"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back to dashboard",
                tint = CyanPrimary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "BACK",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                color = CyanPrimary,
                letterSpacing = 1.sp
            )
        }

        Text(
            text = title,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            fontSize = 11.sp,
            letterSpacing = 1.2.sp,
            color = CyanPrimary,
            modifier = Modifier.testTag("page_header_title")
        )
    }
}
