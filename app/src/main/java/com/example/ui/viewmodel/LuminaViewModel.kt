package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.LuminaDatabase
import com.example.data.LuminaRepository
import com.example.data.model.ChatMessage
import com.example.data.model.Measurement
import com.example.data.model.PromptItem
import com.example.sensor.NetworkStatusTracker
import com.example.sensor.OrientationData
import com.example.sensor.SensorDataProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    DASHBOARD,
    VIDEO_GEN,
    IMAGE_GEN,
    FABRICATION,
    CHAT,
    PROMPT_STUDIO,
    SETTINGS
}

enum class FabricationTool {
    AR_TAPE,
    AI_COUNTER,
    LEVEL_360,
    RIGHT_ANGLE
}

enum class MeasurementUnit {
    MM,
    INCHES,
    FEET
}

data class CounterBox(
    val id: Int,
    val xPercent: Float,
    val yPercent: Float,
    val widthPercent: Float,
    val heightPercent: Float,
    val label: String
)

data class VideoGenerationState(
    val isGenerating: Boolean = false,
    val progress: Float = 0f,
    val statusText: String = "",
    val generatedVideoTitle: String? = null,
    val selectedStyle: String = "9D Hologram FX",
    val selectedAspectRatio: String = "16:9",
    val durationSeconds: Int = 10
)

data class ImageGenerationState(
    val isGenerating: Boolean = false,
    val progress: Float = 0f,
    val generatedPrompt: String? = null,
    val selectedStyle: String = "Cyberpunk 9D",
    val selectedAspectRatio: String = "1:1",
    val promptInput: String = ""
)

class LuminaViewModel(application: Application) : AndroidViewModel(application) {

    private val database = LuminaDatabase.getDatabase(application, viewModelScope)
    private val repository = LuminaRepository(
        chatDao = database.chatDao(),
        measurementDao = database.measurementDao(),
        promptDao = database.promptDao()
    )

    private val sensorProvider = SensorDataProvider(application)
    private val networkTracker = NetworkStatusTracker(application)

    // Vibrator
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    // Navigation & UI State
    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _activeFabTool = MutableStateFlow(FabricationTool.AR_TAPE)
    val activeFabTool: StateFlow<FabricationTool> = _activeFabTool.asStateFlow()

    // Sensor and Network
    val orientationData: StateFlow<OrientationData> = sensorProvider.orientationData
    val isOnline: StateFlow<Boolean> = networkTracker.isOnline

    // Settings
    private val _hapticEnabled = MutableStateFlow(true)
    val hapticEnabled: StateFlow<Boolean> = _hapticEnabled.asStateFlow()

    private val _glowEffectsEnabled = MutableStateFlow(true)
    val glowEffectsEnabled: StateFlow<Boolean> = _glowEffectsEnabled.asStateFlow()

    // AR Tape State
    private val _currentTapeUnit = MutableStateFlow(MeasurementUnit.MM)
    val currentTapeUnit: StateFlow<MeasurementUnit> = _currentTapeUnit.asStateFlow()

    private val _tapeDistanceMm = MutableStateFlow(1185f)
    val tapeDistanceMm: StateFlow<Float> = _tapeDistanceMm.asStateFlow()

    private val _isTapeCalibrating = MutableStateFlow(false)
    val isTapeCalibrating: StateFlow<Boolean> = _isTapeCalibrating.asStateFlow()

    // AI Counter State
    private val _countedItems = MutableStateFlow(
        listOf(
            CounterBox(1, 0.18f, 0.22f, 0.28f, 0.20f, "#01 BEAM"),
            CounterBox(2, 0.54f, 0.22f, 0.28f, 0.20f, "#02 BEAM"),
            CounterBox(3, 0.18f, 0.48f, 0.28f, 0.20f, "#03 BOLT"),
            CounterBox(4, 0.54f, 0.48f, 0.28f, 0.20f, "#04 BOLT")
        )
    )
    val countedItems: StateFlow<List<CounterBox>> = _countedItems.asStateFlow()

    private val _counterTotal = MutableStateFlow(24)
    val counterTotal: StateFlow<Int> = _counterTotal.asStateFlow()

    // Generation States
    private val _videoState = MutableStateFlow(VideoGenerationState())
    val videoState: StateFlow<VideoGenerationState> = _videoState.asStateFlow()

    private val _imageState = MutableStateFlow(ImageGenerationState())
    val imageState: StateFlow<ImageGenerationState> = _imageState.asStateFlow()

    // Prompt Studio State
    private val _selectedPromptCategory = MutableStateFlow("ALL")
    val selectedPromptCategory: StateFlow<String> = _selectedPromptCategory.asStateFlow()

    // Database flows
    val chatMessages: StateFlow<List<ChatMessage>> = repository.allMessages.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val savedMeasurements: StateFlow<List<Measurement>> = repository.allMeasurements.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val promptsList: StateFlow<List<PromptItem>> = repository.allPrompts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        sensorProvider.startListening()
        networkTracker.startTracking()
    }

    override fun onCleared() {
        super.onCleared()
        sensorProvider.stopListening()
        networkTracker.stopTracking()
    }

    // Navigation
    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
        triggerHaptic(20)
    }

    fun setFabricationTool(tool: FabricationTool) {
        _activeFabTool.value = tool
        triggerHaptic(25)
    }

    // Haptics
    fun triggerHaptic(durationMs: Long = 30) {
        if (!_hapticEnabled.value) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }

    // AR Tape Actions
    fun toggleTapeUnit() {
        _currentTapeUnit.value = when (_currentTapeUnit.value) {
            MeasurementUnit.MM -> MeasurementUnit.INCHES
            MeasurementUnit.INCHES -> MeasurementUnit.FEET
            MeasurementUnit.FEET -> MeasurementUnit.MM
        }
        triggerHaptic(30)
    }

    fun resetTape() {
        viewModelScope.launch {
            _isTapeCalibrating.value = true
            triggerHaptic(40)
            delay(500)
            _tapeDistanceMm.value = 1185f
            _isTapeCalibrating.value = false
        }
    }

    fun setTapeDistance(distMm: Float) {
        _tapeDistanceMm.value = distMm.coerceIn(50f, 12000f)
    }

    fun saveCurrentMeasurement(toolName: String, valueText: String, rawVal: Double, unit: String) {
        viewModelScope.launch {
            repository.saveMeasurement(
                toolType = toolName,
                formattedValue = valueText,
                rawValue = rawVal,
                unit = unit,
                notes = "Saved from $toolName"
            )
            triggerHaptic(50)
        }
    }

    fun deleteMeasurement(id: Long) {
        viewModelScope.launch {
            repository.deleteMeasurement(id)
        }
    }

    fun clearAllMeasurements() {
        viewModelScope.launch {
            repository.clearMeasurements()
        }
    }

    // AI Counter Actions
    fun incrementCounter() {
        _counterTotal.value = _counterTotal.value + 1
        triggerHaptic(30)
    }

    fun decrementCounter() {
        if (_counterTotal.value > 0) {
            _counterTotal.value = _counterTotal.value - 1
            triggerHaptic(30)
        }
    }

    fun resetCounter() {
        _counterTotal.value = 0
        triggerHaptic(40)
    }

    fun addCounterBox(xPercent: Float, yPercent: Float) {
        val nextId = (_countedItems.value.maxOfOrNull { it.id } ?: 0) + 1
        val newBox = CounterBox(
            id = nextId,
            xPercent = (xPercent - 0.12f).coerceIn(0.05f, 0.75f),
            yPercent = (yPercent - 0.1f).coerceIn(0.05f, 0.75f),
            widthPercent = 0.24f,
            heightPercent = 0.18f,
            label = "#%02d ITEM".format(nextId)
        )
        _countedItems.value = _countedItems.value + newBox
        _counterTotal.value = _counterTotal.value + 1
        triggerHaptic(35)
    }

    // 360 Level Actions
    fun calibrateLevel() {
        sensorProvider.calibrateCurrentPosition()
        triggerHaptic(60)
    }

    fun resetLevelCalibration() {
        sensorProvider.resetCalibration()
        triggerHaptic(40)
    }

    fun simulateTilt(pitch: Float, roll: Float) {
        sensorProvider.updateManualSimulation(pitch, roll)
    }

    // Chat Actions
    fun sendChatMessage(userText: String) {
        val query = userText.trim()
        if (query.isEmpty()) return

        viewModelScope.launch {
            repository.insertMessage(query, isUser = true)
            triggerHaptic(30)

            delay(400)
            val reply = generateCyberAssistantResponse(query)
            repository.insertMessage(reply, isUser = false)
            triggerHaptic(40)
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            repository.clearChat()
            triggerHaptic(40)
        }
    }

    private fun generateCyberAssistantResponse(query: String): String {
        val lower = query.lowercase()
        return when {
            lower.contains("hello") || lower.contains("hi") || lower.contains("hey") ->
                "Lumina AI online. Neural matrix running at nominal efficiency. Ready for Video Gen, Hologram synthesis, AR Fabrication, and Sensor Calibration."
            lower.contains("gemini") || lower.contains("multimodal") || lower.contains("reasoning") ->
                "Gemini Matrix connected! Processing multi-turn logical inference, code generation, and neural synthesis across all quantum nodes."
            lower.contains("shader") || lower.contains("code") || lower.contains("python") ->
                "Neural Shader Generator: Compiled quantum GLSL fragment pipeline with emissive neon bloom, raymarched volumetric SDF lattices, and real-time temporal dithering."
            lower.contains("fabrication") || lower.contains("measure") || lower.contains("tape") ->
                "Fabrication suite active! AR Tape provides real-time mm/inch/ft precision measurement up to 12m, with dual-axis 360° spirit level and live object counting HUD."
            lower.contains("video") || lower.contains("movie") || lower.contains("vfx") ->
                "Video Generator ready. Quantum 9D neural synth is primed for cinematic particle render, volumetric mist, and anamorphic 2.39:1 aspect ratio generation."
            lower.contains("image") || lower.contains("draw") || lower.contains("art") ->
                "Image Generation studio available. Holographic canvas renderer supports Cyberpunk, Hologram neon, Sci-Fi blueprint, and Quantum matrix styles."
            lower.contains("prompt") ->
                "Prompt Studio contains curated 9D VFX, 3D Architecture, Fabrication Schematics, and Neural Shaders. Check the Prompt Studio tab to copy or launch!"
            lower.contains("level") || lower.contains("pitch") || lower.contains("roll") ->
                "360° Spirit Level utilizes device hardware accelerometers and gravity vectors for sub-degree accuracy. Tolerance threshold is calibrated at ±1.2°."
            lower.contains("offline") || lower.contains("storage") ->
                "All chat logs, measurements, and custom prompts are encrypted and stored locally in on-device Room SQLite storage."
            else ->
                "Lumina Neural Engine processed: \"$query\". All local subsystems ready. You can launch generation tasks or fabrication tools anytime."
        }
    }

    // Video Generation
    fun setVideoStyle(style: String) {
        _videoState.value = _videoState.value.copy(selectedStyle = style)
    }

    fun setVideoAspectRatio(aspectRatio: String) {
        _videoState.value = _videoState.value.copy(selectedAspectRatio = aspectRatio)
    }

    fun startVideoGeneration(prompt: String) {
        val p = prompt.ifBlank { "9D Cyberpunk Volumetric Portal with Neon Particle Flow" }
        viewModelScope.launch {
            _videoState.value = _videoState.value.copy(
                isGenerating = true,
                progress = 0.05f,
                statusText = "Compiling Neural Quantum Lattice...",
                generatedVideoTitle = null
            )
            triggerHaptic(30)

            delay(600)
            _videoState.value = _videoState.value.copy(progress = 0.35f, statusText = "Synthesizing 9D Raytraced Volumetric Frames...")
            delay(700)
            _videoState.value = _videoState.value.copy(progress = 0.70f, statusText = "Applying Holographic Bloom & Temporal FX...")
            delay(600)
            _videoState.value = _videoState.value.copy(progress = 0.95f, statusText = "Finalizing 60FPS Neural Video...")
            delay(400)
            _videoState.value = _videoState.value.copy(
                isGenerating = false,
                progress = 1.0f,
                statusText = "Render Complete!",
                generatedVideoTitle = p
            )
            triggerHaptic(60)
        }
    }

    // Image Generation
    fun setImageStyle(style: String) {
        _imageState.value = _imageState.value.copy(selectedStyle = style)
    }

    fun setImagePromptInput(text: String) {
        _imageState.value = _imageState.value.copy(promptInput = text)
    }

    fun startImageGeneration(prompt: String) {
        val p = prompt.ifBlank { "Holographic Cyber Lotus Crystal with Neon Cyan Light Rays" }
        viewModelScope.launch {
            _imageState.value = _imageState.value.copy(
                isGenerating = true,
                progress = 0.1f,
                generatedPrompt = null
            )
            triggerHaptic(30)

            delay(500)
            _imageState.value = _imageState.value.copy(progress = 0.5f)
            delay(500)
            _imageState.value = _imageState.value.copy(progress = 0.85f)
            delay(400)
            _imageState.value = _imageState.value.copy(
                isGenerating = false,
                progress = 1.0f,
                generatedPrompt = p
            )
            triggerHaptic(60)
        }
    }

    // Prompt Studio Actions
    fun setPromptCategory(cat: String) {
        _selectedPromptCategory.value = cat
        triggerHaptic(20)
    }

    fun togglePromptFavorite(id: Long, isFav: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(id, isFav)
            triggerHaptic(25)
        }
    }

    fun saveCustomPrompt(title: String, category: String, text: String, tags: String) {
        viewModelScope.launch {
            repository.insertPrompt(
                PromptItem(
                    title = title.ifBlank { "Custom Cyber Prompt" },
                    category = category,
                    promptText = text,
                    tags = tags
                )
            )
            triggerHaptic(40)
        }
    }

    // Settings
    fun toggleHaptics() {
        _hapticEnabled.value = !_hapticEnabled.value
    }

    fun toggleGlowEffects() {
        _glowEffectsEnabled.value = !_glowEffectsEnabled.value
    }
}
