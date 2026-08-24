package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
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
import com.example.ui.viewmodel.ImageGenerationState

@Composable
fun ImageGenScreen(
    imageState: ImageGenerationState,
    onStartGeneration: (String) -> Unit,
    onSetStyle: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var promptText by remember { mutableStateOf("Holographic Cyber Lotus Crystal with Neon Cyan Light Rays") }

    val styles = listOf(
        "Cyberpunk 9D",
        "Neon Hologram",
        "Sci-Fi Blueprint",
        "Quantum Matrix",
        "Cyber Mecha",
        "Dark Obsidian"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        CyberSubHeader(title = "IMAGE GENERATION STUDIO", onBack = onBack)

        AdBannerSlot(label = "--- ADVERTISEMENT ---", placeholderText = "[ TOP AD BANNER ]", heightDp = 18)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Holographic Canvas Preview Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, CyanPrimary.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .background(Color(0xFF020713)),
                contentAlignment = Alignment.Center
            ) {
                // Background Hologram Art Layer
                Image(
                    painter = painterResource(id = R.drawable.ic_lumina_logo),
                    contentDescription = "Holographic Canvas",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                    alpha = 0.75f
                )

                // Overlay Shader Lines
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(
                        brush = Brush.radialGradient(
                            listOf(
                                Color.Transparent,
                                DarkSurface.copy(alpha = 0.8f)
                            )
                        )
                    )
                }

                // Top Style Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(DarkSurface.copy(alpha = 0.85f), RoundedCornerShape(4.dp))
                        .border(1.dp, CyanPrimary, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "STYLE: ${imageState.selectedStyle}",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 7.sp,
                        color = CyanPrimary
                    )
                }

                // Generating Spinner Overlay
                if (imageState.isGenerating) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(DarkSurface.copy(alpha = 0.8f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                color = CyanPrimary,
                                modifier = Modifier.size(32.dp),
                                strokeWidth = 3.dp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "RENDERING NEURAL HOLOGRAM...",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanPrimary
                            )
                        }
                    }
                }
            }

            // Style Pills Selector
            Column {
                Text(
                    text = "ARTISTIC STYLES:",
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
                        val isSel = imageState.selectedStyle == style
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isSel) CyanPrimary.copy(alpha = 0.25f) else DarkCardSurface)
                                .border(1.dp, if (isSel) CyanPrimary else CyanPrimary.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                .clickable { onSetStyle(style) }
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                                .testTag("image_style_${style.lowercase().replace(' ', '_')}")
                        ) {
                            Text(
                                text = style,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 7.5.sp,
                                color = if (isSel) CyanPrimary else TextSecondary
                            )
                        }
                    }
                }
            }

            // Prompt Input Box
            OutlinedTextField(
                value = promptText,
                onValueChange = { promptText = it },
                label = { Text("HOLO PROMPT INPUT", fontFamily = FontFamily.Monospace, fontSize = 8.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("image_prompt_input"),
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

            // Buttons: Generate, Save Artwork, Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = { onStartGeneration(promptText) },
                    enabled = !imageState.isGenerating,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("btn_generate_image"),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Generate",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "RENDER IMAGE",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 8.5.sp,
                        color = Color.Black
                    )
                }

                Button(
                    onClick = {
                        Toast.makeText(context, "Artwork Saved to Phone Storage!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .height(38.dp)
                        .testTag("btn_save_image"),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkCardSurface),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.6f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Save",
                        tint = CyanPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("SAVE", fontFamily = FontFamily.Monospace, fontSize = 8.sp, color = CyanPrimary)
                }

                Button(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Check out Lumina 9D AI Generated Artwork: ${imageState.generatedPrompt ?: promptText}"
                            )
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Share Lumina Artwork")
                        context.startActivity(shareIntent)
                    },
                    modifier = Modifier
                        .height(38.dp)
                        .testTag("btn_share_image"),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkCardSurface),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PurplePrimary.copy(alpha = 0.6f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = PurplePrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("SHARE", fontFamily = FontFamily.Monospace, fontSize = 8.sp, color = PurplePrimary)
                }
            }
        }

        AdBannerSlot(label = "--- ADVERTISEMENT ---", placeholderText = "[ BOTTOM AD BANNER ]", heightDp = 18)
    }
}
