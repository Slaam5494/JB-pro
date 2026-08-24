package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AdBannerSlot
import com.example.ui.components.CyberSubHeader
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkCardSecondary
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.VideoGenerationState

@Composable
fun VideoGenScreen(
    videoState: VideoGenerationState,
    onStartGeneration: (String) -> Unit,
    onSetStyle: (String) -> Unit,
    onSetAspectRatio: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var promptInput by remember { mutableStateOf("9D Volumetric Cyberpunk Portal with Glowing Neon Mist") }
    var isPlayingPreview by remember { mutableStateOf(true) }

    val styles = listOf(
        "9D Hologram FX",
        "Neural Warp",
        "Cyber Synthwave",
        "Quantum Particle 60FPS",
        "Blade Runner 2.39:1"
    )

    val aspectRatios = listOf("16:9", "9:16", "1:1", "21:9")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        CyberSubHeader(title = "VIDEO GENERATION STUDIO", onBack = onBack)

        AdBannerSlot(label = "--- ADVERTISEMENT ---", placeholderText = "[ TOP AD BANNER ]", heightDp = 18)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Preview / Video Canvas Viewport
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, CyanPrimary.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .background(Color(0xFF020713)),
                contentAlignment = Alignment.Center
            ) {
                // Animated Cyber Video Canvas
                CyberVideoCanvas(
                    isGenerating = videoState.isGenerating,
                    isPlaying = isPlayingPreview
                )

                // Overlay Controls & Badges
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(DarkSurface.copy(alpha = 0.85f), RoundedCornerShape(3.dp))
                            .border(1.dp, CyanPrimary, RoundedCornerShape(3.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = videoState.selectedStyle,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 7.sp,
                            color = CyanPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(DarkSurface.copy(alpha = 0.85f), RoundedCornerShape(3.dp))
                            .border(1.dp, PurplePrimary, RoundedCornerShape(3.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = videoState.selectedAspectRatio,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 7.sp,
                            color = PurplePrimary
                        )
                    }
                }

                // Play / Pause Toggle Center Icon
                if (!videoState.isGenerating) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(DarkSurface.copy(alpha = 0.7f))
                            .border(1.dp, CyanPrimary, CircleShape)
                            .clickable { isPlayingPreview = !isPlayingPreview },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = CyanPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Generating Progress Overlay
                if (videoState.isGenerating) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(DarkSurface.copy(alpha = 0.85f))
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = videoState.statusText,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            color = CyanPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { videoState.progress },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = CyanPrimary,
                            trackColor = DarkCardSecondary
                        )
                    }
                }
            }

            // Aspect Ratio Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RATIO:",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.sp,
                    color = TextSecondary
                )
                aspectRatios.forEach { ratio ->
                    val isSel = videoState.selectedAspectRatio == ratio
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isSel) CyanPrimary.copy(alpha = 0.25f) else DarkCardSurface)
                            .border(1.dp, if (isSel) CyanPrimary else CyanPrimary.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                            .clickable { onSetAspectRatio(ratio) }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                            .testTag("ratio_$ratio")
                    ) {
                        Text(
                            text = ratio,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 7.5.sp,
                            color = if (isSel) CyanPrimary else TextSecondary
                        )
                    }
                }
            }

            // FX Style Selector
            Column {
                Text(
                    text = "9D FX STYLE PRESETS:",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(styles) { style ->
                        val isSel = videoState.selectedStyle == style
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isSel) PurplePrimary.copy(alpha = 0.25f) else DarkCardSurface)
                                .border(1.dp, if (isSel) PurplePrimary else PurplePrimary.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                            .clickable { onSetStyle(style) }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                            .testTag("style_${style.lowercase().replace(' ', '_')}")
                        ) {
                            Text(
                                text = style,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 7.5.sp,
                                color = if (isSel) PurplePrimary else TextSecondary
                            )
                        }
                    }
                }
            }

            // Prompt Input Field
            OutlinedTextField(
                value = promptInput,
                onValueChange = { promptInput = it },
                label = { Text("VIDEO SYNTHESIS PROMPT", fontFamily = FontFamily.Monospace, fontSize = 8.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("video_prompt_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanPrimary,
                    unfocusedBorderColor = CyanPrimary.copy(alpha = 0.4f),
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = DarkCardSurface.copy(alpha = 0.8f),
                    unfocusedContainerColor = DarkCardSurface.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(6.dp),
                maxLines = 3
            )

            // Buttons: Generate & Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = { onStartGeneration(promptInput) },
                    enabled = !videoState.isGenerating,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("btn_synthesize_video"),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Synthesize",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SYNTHESIZE 9D VIDEO",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 8.5.sp,
                        color = Color.Black
                    )
                }

                Button(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Check out Lumina 9D AI Video Generator: ${videoState.generatedVideoTitle ?: promptInput}"
                            )
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Share Lumina AI Video")
                        context.startActivity(shareIntent)
                    },
                    modifier = Modifier
                        .height(38.dp)
                        .testTag("btn_share_video"),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkCardSurface),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.6f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = CyanPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "SHARE",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.sp,
                        color = CyanPrimary
                    )
                }
            }
        }

        AdBannerSlot(label = "--- ADVERTISEMENT ---", placeholderText = "[ BOTTOM AD BANNER ]", heightDp = 18)
    }
}

@Composable
fun CyberVideoCanvas(
    isGenerating: Boolean,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "videoFx")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Dark background
        drawRect(Color(0xFF030A18))

        // Volumetric mist & energy waves
        val cx = w / 2f
        val cy = h / 2f

        // Draw multiple glowing harmonic rings
        val ringCount = 5
        for (i in 1..ringCount) {
            val radius = (w * 0.12f * i + (if (isPlaying) kotlin.math.sin(Math.toRadians((phase + i * 40).toDouble())).toFloat() * 10f else 0f))
            drawCircle(
                color = if (i % 2 == 0) CyanPrimary.copy(alpha = 0.25f) else PurplePrimary.copy(alpha = 0.25f),
                radius = radius,
                center = Offset(cx, cy),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f)
            )
        }

        // Scanlines
        var y = 0f
        while (y < h) {
            drawLine(
                color = Color.Black.copy(alpha = 0.35f),
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = 1.5f
            )
            y += 4f
        }
    }
}
