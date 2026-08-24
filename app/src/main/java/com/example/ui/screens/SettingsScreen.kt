package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.ui.components.AdBannerSlot
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

@Composable
fun SettingsScreen(
    chatCount: Int,
    measurementCount: Int,
    promptCount: Int,
    hapticEnabled: Boolean,
    onToggleHaptics: () -> Unit,
    glowEnabled: Boolean,
    onToggleGlow: () -> Unit,
    onClearAllData: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showConfirmClearDialog by remember { mutableStateOf(false) }

    val estimatedKb = ((chatCount * 128 + measurementCount * 64 + promptCount * 256) / 1024.0) + 12.4

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        CyberSubHeader(title = "APP SETTINGS", onBack = onBack)

        AdBannerSlot(label = "--- ADVERTISEMENT ---", placeholderText = "[ TOP AD BANNER ]", heightDp = 18)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Storage Diagnostics Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyanPrimary.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Storage, contentDescription = "Storage", tint = CyanPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "LOCAL ON-DEVICE STORAGE MEMORY",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 8.5.sp,
                            color = CyanPrimary
                        )
                    }

                    Text(
                        text = "Encrypted SQLite Room Database: ${"%.2f".format(estimatedKb)} KB",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Text(
                        text = "• Chat Messages: $chatCount records\n• Saved Measurements: $measurementCount records\n• Vault Prompts: $promptCount entries",
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 9.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = { showConfirmClearDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = DangerRed.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(34.dp)
                            .border(1.dp, DangerRed, RoundedCornerShape(4.dp))
                            .testTag("btn_clear_all_storage")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear", tint = DangerRed, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("CLEAR LOCAL DATA & CACHE", fontFamily = FontFamily.Monospace, fontSize = 7.5.sp, color = DangerRed)
                    }
                }
            }

            // Preferences Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, PurplePrimary.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "PREFERENCES & SENSORS",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 8.5.sp,
                        color = PurplePrimary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Vibration, contentDescription = "Haptics", tint = TextSecondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Haptic Feedback on Taps", fontSize = 10.sp, color = TextPrimary)
                        }

                        Switch(
                            checked = hapticEnabled,
                            onCheckedChange = { onToggleHaptics() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CyanPrimary,
                                checkedTrackColor = CyanPrimary.copy(alpha = 0.3f),
                                uncheckedThumbColor = TextSecondary,
                                uncheckedTrackColor = DarkSurface
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Sensors, contentDescription = "Glow", tint = TextSecondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Holographic Visual Glow", fontSize = 10.sp, color = TextPrimary)
                        }

                        Switch(
                            checked = glowEnabled,
                            onCheckedChange = { onToggleGlow() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = EmeraldAccent,
                                checkedTrackColor = EmeraldAccent.copy(alpha = 0.3f),
                                uncheckedThumbColor = TextSecondary,
                                uncheckedTrackColor = DarkSurface
                            )
                        )
                    }
                }
            }

            // System Specifications Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, EmeraldAccent.copy(alpha = 0.35f), RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = "Info", tint = EmeraldAccent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SYSTEM INFORMATION",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 8.5.sp,
                            color = EmeraldAccent
                        )
                    }

                    Text(
                        text = "Build: LUMINA 9D AI SUITE JB_V2.5®\nEngine: Local On-Device AI Engine\nHardware Sensor Interface: Direct Gravity & Accelerometer\nDatabase: SQLite Jetpack Room Persistence",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 7.5.sp,
                        color = TextSecondary,
                        lineHeight = 12.sp
                    )
                }
            }
        }

        AdBannerSlot(label = "--- ADVERTISEMENT ---", placeholderText = "[ BOTTOM AD BANNER ]", heightDp = 18)
    }

    // Confirmation Dialog
    if (showConfirmClearDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmClearDialog = false },
            containerColor = DarkSurface,
            title = {
                Text(
                    text = "CLEAR ALL STORAGE?",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = DangerRed
                )
            },
            text = {
                Text(
                    text = "This will permanently wipe all local chat history and saved fabrication measurements from your phone memory.",
                    fontSize = 10.sp,
                    color = TextPrimary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllData()
                        showConfirmClearDialog = false
                        Toast.makeText(context, "Device Memory Cleared!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text("CLEAR NOW", fontFamily = FontFamily.Monospace, fontSize = 8.sp, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmClearDialog = false }) {
                    Text("CANCEL", color = TextSecondary, fontSize = 8.sp)
                }
            }
        )
    }
}
