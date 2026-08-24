package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.HoloBackground
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FabricationScreen
import com.example.ui.screens.ImageGenScreen
import com.example.ui.screens.PromptStudioScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VideoGenScreen
import com.example.ui.theme.LuminaTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.LuminaViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: LuminaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LuminaTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    LuminaMainApp(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun LuminaMainApp(
    viewModel: LuminaViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val activeFabTool by viewModel.activeFabTool.collectAsStateWithLifecycle()
    val orientationData by viewModel.orientationData.collectAsStateWithLifecycle()

    // AR Tape
    val tapeDistanceMm by viewModel.tapeDistanceMm.collectAsStateWithLifecycle()
    val tapeUnit by viewModel.currentTapeUnit.collectAsStateWithLifecycle()
    val isTapeCalibrating by viewModel.isTapeCalibrating.collectAsStateWithLifecycle()

    // AI Counter
    val countedItems by viewModel.countedItems.collectAsStateWithLifecycle()
    val counterTotal by viewModel.counterTotal.collectAsStateWithLifecycle()

    // Video & Image States
    val videoState by viewModel.videoState.collectAsStateWithLifecycle()
    val imageState by viewModel.imageState.collectAsStateWithLifecycle()

    // Prompt Category
    val selectedCategory by viewModel.selectedPromptCategory.collectAsStateWithLifecycle()

    // DB States
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val savedMeasurements by viewModel.savedMeasurements.collectAsStateWithLifecycle()
    val promptsList by viewModel.promptsList.collectAsStateWithLifecycle()

    // Settings
    val hapticEnabled by viewModel.hapticEnabled.collectAsStateWithLifecycle()
    val glowEnabled by viewModel.glowEffectsEnabled.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        // Holographic animated background
        if (glowEnabled) {
            HoloBackground()
        }

        // Screen transition
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "screenTransition"
        ) { screen ->
            when (screen) {
                AppScreen.DASHBOARD -> {
                    DashboardScreen(
                        isOnline = isOnline,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                }
                AppScreen.VIDEO_GEN -> {
                    VideoGenScreen(
                        videoState = videoState,
                        onStartGeneration = { viewModel.startVideoGeneration(it) },
                        onSetStyle = { viewModel.setVideoStyle(it) },
                        onSetAspectRatio = { viewModel.setVideoAspectRatio(it) },
                        onBack = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                    )
                }
                AppScreen.IMAGE_GEN -> {
                    ImageGenScreen(
                        imageState = imageState,
                        onStartGeneration = { viewModel.startImageGeneration(it) },
                        onSetStyle = { viewModel.setImageStyle(it) },
                        onBack = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                    )
                }
                AppScreen.FABRICATION -> {
                    FabricationScreen(
                        activeTool = activeFabTool,
                        onSelectTool = { viewModel.setFabricationTool(it) },
                        orientationData = orientationData,
                        tapeDistanceMm = tapeDistanceMm,
                        tapeUnit = tapeUnit,
                        isTapeCalibrating = isTapeCalibrating,
                        onToggleTapeUnit = { viewModel.toggleTapeUnit() },
                        onResetTape = { viewModel.resetTape() },
                        onSetTapeDistance = { viewModel.setTapeDistance(it) },
                        onSaveMeasurement = { tool, formatted, raw, unit ->
                            viewModel.saveCurrentMeasurement(tool, formatted, raw, unit)
                        },
                        countedItems = countedItems,
                        counterTotal = counterTotal,
                        onIncrementCounter = { viewModel.incrementCounter() },
                        onDecrementCounter = { viewModel.decrementCounter() },
                        onResetCounter = { viewModel.resetCounter() },
                        onAddCounterBox = { x, y -> viewModel.addCounterBox(x, y) },
                        onCalibrateLevel = { viewModel.calibrateLevel() },
                        onResetLevelCalibration = { viewModel.resetLevelCalibration() },
                        onSimulateTilt = { pitch, roll -> viewModel.simulateTilt(pitch, roll) },
                        savedMeasurements = savedMeasurements,
                        onDeleteMeasurement = { viewModel.deleteMeasurement(it) },
                        onBack = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                    )
                }
                AppScreen.CHAT -> {
                    ChatScreen(
                        messages = chatMessages,
                        onSendMessage = { viewModel.sendChatMessage(it) },
                        onClearChat = { viewModel.clearChatHistory() },
                        onBack = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                    )
                }
                AppScreen.PROMPT_STUDIO -> {
                    PromptStudioScreen(
                        promptsList = promptsList,
                        selectedCategory = selectedCategory,
                        onSelectCategory = { viewModel.setPromptCategory(it) },
                        onToggleFavorite = { id, fav -> viewModel.togglePromptFavorite(id, fav) },
                        onSaveCustomPrompt = { title, cat, text, tags ->
                            viewModel.saveCustomPrompt(title, cat, text, tags)
                        },
                        onLaunchInStudio = { targetScreen, promptText ->
                            if (targetScreen == AppScreen.VIDEO_GEN) {
                                viewModel.startVideoGeneration(promptText)
                            } else if (targetScreen == AppScreen.IMAGE_GEN) {
                                viewModel.startImageGeneration(promptText)
                            }
                            viewModel.navigateTo(targetScreen)
                        },
                        onBack = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                    )
                }
                AppScreen.SETTINGS -> {
                    SettingsScreen(
                        chatCount = chatMessages.size,
                        measurementCount = savedMeasurements.size,
                        promptCount = promptsList.size,
                        hapticEnabled = hapticEnabled,
                        onToggleHaptics = { viewModel.toggleHaptics() },
                        glowEnabled = glowEnabled,
                        onToggleGlow = { viewModel.toggleGlowEffects() },
                        onClearAllData = {
                            viewModel.clearChatHistory()
                            viewModel.clearAllMeasurements()
                        },
                        onBack = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                    )
                }
            }
        }
    }
}
