package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PromptItem
import com.example.ui.components.AdBannerSlot
import com.example.ui.components.CyberSubHeader
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkCardSecondary
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AppScreen

@Composable
fun PromptStudioScreen(
    promptsList: List<PromptItem>,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    onToggleFavorite: (Long, Boolean) -> Unit,
    onSaveCustomPrompt: (String, String, String, String) -> Unit,
    onLaunchInStudio: (AppScreen, String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showCreateDialog by remember { mutableStateOf(false) }

    val categories = listOf(
        "ALL",
        "9D VFX",
        "Hologram Art",
        "3D Architecture",
        "Fabrication",
        "Neural Code",
        "Cinematography"
    )

    val filteredPrompts = if (selectedCategory == "ALL") {
        promptsList
    } else {
        promptsList.filter { it.category == selectedCategory }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        CyberSubHeader(title = "PROMPT STUDIO", onBack = onBack)

        AdBannerSlot(label = "--- ADVERTISEMENT ---", placeholderText = "[ TOP AD BANNER ]", heightDp = 18)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Category Chips Row + Add Custom Prompt Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LazyRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(categories) { cat ->
                        val isSel = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isSel) EmeraldAccent.copy(alpha = 0.25f) else DarkCardSurface)
                                .border(1.dp, if (isSel) EmeraldAccent else EmeraldAccent.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                .clickable { onSelectCategory(cat) }
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                                .testTag("cat_$cat")
                        ) {
                            Text(
                                text = cat,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 7.5.sp,
                                color = if (isSel) EmeraldAccent else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(CyanPrimary)
                        .clickable { showCreateDialog = true }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                        .testTag("btn_add_custom_prompt"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.Black, modifier = Modifier.size(12.dp))
                        Text("NEW", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Black, fontSize = 7.5.sp, color = Color.Black)
                    }
                }
            }

            // Prompts List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(filteredPrompts) { item ->
                    PromptCardItem(
                        item = item,
                        onCopy = { text ->
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Lumina Prompt", text)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Prompt copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        onToggleFav = { onToggleFavorite(item.id, item.isFavorite) },
                        onUseInVideo = { onLaunchInStudio(AppScreen.VIDEO_GEN, item.promptText) },
                        onUseInImage = { onLaunchInStudio(AppScreen.IMAGE_GEN, item.promptText) }
                    )
                }
            }
        }

        AdBannerSlot(label = "--- ADVERTISEMENT ---", placeholderText = "[ BOTTOM AD BANNER ]", heightDp = 18)
    }

    // Create Prompt Dialog
    if (showCreateDialog) {
        var newTitle by remember { mutableStateOf("") }
        var newCategory by remember { mutableStateOf("9D VFX") }
        var newText by remember { mutableStateOf("") }
        var newTags by remember { mutableStateOf("VFX, Cyber, Custom") }

        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            containerColor = DarkSurface,
            title = {
                Text(
                    text = "CREATE CYBER PROMPT",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = CyanPrimary
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Title", fontSize = 8.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = CyanPrimary.copy(alpha = 0.4f),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = newText,
                        onValueChange = { newText = it },
                        label = { Text("Prompt Text", fontSize = 8.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = CyanPrimary.copy(alpha = 0.4f),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newText.isNotBlank()) {
                            onSaveCustomPrompt(newTitle, newCategory, newText, newTags)
                            showCreateDialog = false
                            Toast.makeText(context, "Prompt Saved to Local Vault!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)
                ) {
                    Text("SAVE PROMPT", fontFamily = FontFamily.Monospace, fontSize = 8.sp, color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("CANCEL", color = TextSecondary, fontSize = 8.sp)
                }
            }
        )
    }
}

@Composable
fun PromptCardItem(
    item: PromptItem,
    onCopy: (String) -> Unit,
    onToggleFav: () -> Unit,
    onUseInVideo: () -> Unit,
    onUseInImage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, CyanPrimary.copy(alpha = 0.35f), RoundedCornerShape(6.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkCardSurface.copy(alpha = 0.9f)),
        shape = RoundedCornerShape(6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Header: Category Pill + Title + Favorite Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(EmeraldAccent.copy(alpha = 0.15f), RoundedCornerShape(3.dp))
                            .border(1.dp, EmeraldAccent, RoundedCornerShape(3.dp))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = item.category,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 6.5.sp,
                            color = EmeraldAccent
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = item.title,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 9.sp,
                        color = TextPrimary
                    )
                }

                IconButton(
                    onClick = onToggleFav,
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = if (item.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (item.isFavorite) EmeraldAccent else TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Prompt text body
            Text(
                text = item.promptText,
                fontFamily = FontFamily.SansSerif,
                fontSize = 9.5.sp,
                color = TextCyan,
                lineHeight = 13.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Action Buttons Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(DarkSurface)
                        .border(1.dp, CyanPrimary.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                        .clickable { onCopy(item.promptText) }
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = CyanPrimary, modifier = Modifier.size(10.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("COPY", fontFamily = FontFamily.Monospace, fontSize = 7.sp, color = CyanPrimary)
                }

                Spacer(modifier = Modifier.width(4.dp))

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(DarkSurface)
                        .border(1.dp, PurplePrimary.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                        .clickable { onUseInVideo() }
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Movie, contentDescription = "Video", tint = PurplePrimary, modifier = Modifier.size(10.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("VIDEO", fontFamily = FontFamily.Monospace, fontSize = 7.sp, color = PurplePrimary)
                }

                Spacer(modifier = Modifier.width(4.dp))

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(DarkSurface)
                        .border(1.dp, EmeraldAccent.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                        .clickable { onUseInImage() }
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Palette, contentDescription = "Image", tint = EmeraldAccent, modifier = Modifier.size(10.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("IMAGE", fontFamily = FontFamily.Monospace, fontSize = 7.sp, color = EmeraldAccent)
                }
            }
        }
    }
}
