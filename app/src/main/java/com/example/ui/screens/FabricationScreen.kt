package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Measurement
import com.example.sensor.OrientationData
import com.example.ui.components.AdBannerSlot
import com.example.ui.components.CyberSubHeader
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.CyanSecondary
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DarkCardSecondary
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextPurple
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningGold
import com.example.ui.viewmodel.CounterBox
import com.example.ui.viewmodel.FabricationTool
import com.example.ui.viewmodel.MeasurementUnit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FabricationScreen(
    activeTool: FabricationTool,
    onSelectTool: (FabricationTool) -> Unit,
    orientationData: OrientationData,
    tapeDistanceMm: Float,
    tapeUnit: MeasurementUnit,
    isTapeCalibrating: Boolean,
    onToggleTapeUnit: () -> Unit,
    onResetTape: () -> Unit,
    onSetTapeDistance: (Float) -> Unit,
    onSaveMeasurement: (String, String, Double, String) -> Unit,
    countedItems: List<CounterBox>,
    counterTotal: Int,
    onIncrementCounter: () -> Unit,
    onDecrementCounter: () -> Unit,
    onResetCounter: () -> Unit,
    onAddCounterBox: (Float, Float) -> Unit,
    onCalibrateLevel: () -> Unit,
    onResetLevelCalibration: () -> Unit,
    onSimulateTilt: (Float, Float) -> Unit,
    savedMeasurements: List<Measurement>,
    onDeleteMeasurement: (Long) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showSavedSheet by remember { mutableStateOf(false) }

    val pageTitle = when (activeTool) {
        FabricationTool.AR_TAPE -> "12M AR TAPE MEASURE"
        FabricationTool.AI_COUNTER -> "AI MATERIAL COUNTER"
        FabricationTool.LEVEL_360 -> "360° SPIRIT LEVEL"
        FabricationTool.RIGHT_ANGLE -> "AR RIGHT ANGLE (90°)"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Sub Header
        CyberSubHeader(title = pageTitle, onBack = onBack)

        // Top Ad Banner
        AdBannerSlot(
            label = "--- ADVERTISEMENT SLOT ---",
            placeholderText = "[ TOP AD BANNER ]",
            heightDp = 20
        )

        // Main Center Tool Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, CyanPrimary.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            DarkCardSurface,
                            Color(0xFF020713)
                        )
                    )
                )
                .testTag("fabrication_viewport"),
            contentAlignment = Alignment.Center
        ) {
            when (activeTool) {
                FabricationTool.AR_TAPE -> {
                    ArTapeSubView(
                        tapeDistanceMm = tapeDistanceMm,
                        unit = tapeUnit,
                        isCalibrating = isTapeCalibrating,
                        onToggleUnit = onToggleTapeUnit,
                        onResetTape = onResetTape,
                        onSetDistance = onSetTapeDistance,
                        onSave = {
                            val formatted = formatDistance(tapeDistanceMm, tapeUnit)
                            onSaveMeasurement("AR Tape", formatted, tapeDistanceMm.toDouble(), tapeUnit.name)
                            Toast.makeText(context, "Saved: $formatted", Toast.LENGTH_SHORT).show()
                        },
                        onOpenHistory = { showSavedSheet = true }
                    )
                }
                FabricationTool.AI_COUNTER -> {
                    AiCounterSubView(
                        countedItems = countedItems,
                        totalCount = counterTotal,
                        onAddBox = onAddCounterBox,
                        onIncrement = onIncrementCounter,
                        onDecrement = onDecrementCounter,
                        onReset = onResetCounter,
                        onSave = {
                            val formatted = "$counterTotal Items"
                            onSaveMeasurement("AI Counter", formatted, counterTotal.toDouble(), "ITEMS")
                            Toast.makeText(context, "Saved Count: $formatted", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                FabricationTool.LEVEL_360 -> {
                    SpiritLevelSubView(
                        orientationData = orientationData,
                        onCalibrate = {
                            onCalibrateLevel()
                            Toast.makeText(context, "Level Zero-Calibrated!", Toast.LENGTH_SHORT).show()
                        },
                        onResetCalib = {
                            onResetLevelCalibration()
                            Toast.makeText(context, "Calibration Reset", Toast.LENGTH_SHORT).show()
                        },
                        onSimulateTilt = onSimulateTilt,
                        onSave = {
                            val status = if (orientationData.isLevel) "Balanced" else "Tilted"
                            val text = "Pitch: %.1f°, Roll: %.1f° (%s)".format(orientationData.pitch, orientationData.roll, status)
                            onSaveMeasurement("360 Level", text, orientationData.pitch.toDouble(), "DEG")
                            Toast.makeText(context, "Saved Angle: $text", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                FabricationTool.RIGHT_ANGLE -> {
                    RightAngleSubView(
                        onSave = {
                            onSaveMeasurement("90 Angle", "90.0° Precision Square", 90.0, "DEG")
                            Toast.makeText(context, "Saved 90° Square Angle", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        // Tool Mode Selection Panel (4 Grid Buttons)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyanPrimary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .background(DarkCardSurface.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                ToolButton(
                    title = "AR TAPE",
                    icon = Icons.Default.Straighten,
                    isActive = activeTool == FabricationTool.AR_TAPE,
                    onClick = { onSelectTool(FabricationTool.AR_TAPE) },
                    modifier = Modifier.weight(1f)
                )
                ToolButton(
                    title = "AI COUNTER",
                    icon = Icons.Default.GridOn,
                    isActive = activeTool == FabricationTool.AI_COUNTER,
                    onClick = { onSelectTool(FabricationTool.AI_COUNTER) },
                    modifier = Modifier.weight(1f)
                )
                ToolButton(
                    title = "360° LEVEL",
                    icon = Icons.Default.Navigation,
                    isActive = activeTool == FabricationTool.LEVEL_360,
                    onClick = { onSelectTool(FabricationTool.LEVEL_360) },
                    modifier = Modifier.weight(1f)
                )
                ToolButton(
                    title = "90° ANGLE",
                    icon = Icons.Default.SquareFoot,
                    isActive = activeTool == FabricationTool.RIGHT_ANGLE,
                    onClick = { onSelectTool(FabricationTool.RIGHT_ANGLE) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Bottom Ad Banner
            AdBannerSlot(
                label = "--- ADVERTISEMENT SLOT ---",
                placeholderText = "[ BOTTOM AD BANNER ]",
                heightDp = 20
            )
        }
    }

    // Saved Measurements Sheet
    if (showSavedSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSavedSheet = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = DarkSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SAVED MEASUREMENTS",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = CyanPrimary
                    )
                    IconButton(onClick = { showSavedSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                if (savedMeasurements.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No measurements saved yet.", color = TextSecondary, fontSize = 12.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(savedMeasurements) { item ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, CyanPrimary.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = item.toolType,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp,
                                            color = PurplePrimary
                                        )
                                        Text(
                                            text = item.formattedValue,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 13.sp,
                                            color = CyanPrimary
                                        )
                                        val dateStr = SimpleDateFormat("MMM dd, HH:mm:ss", Locale.getDefault()).format(Date(item.timestamp))
                                        Text(text = dateStr, fontSize = 9.sp, color = TextSecondary)
                                    }

                                    IconButton(onClick = { onDeleteMeasurement(item.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DangerRed)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 1. AR TAPE SUB-VIEW
// -------------------------------------------------------------
@Composable
fun ArTapeSubView(
    tapeDistanceMm: Float,
    unit: MeasurementUnit,
    isCalibrating: Boolean,
    onToggleUnit: () -> Unit,
    onResetTape: () -> Unit,
    onSetDistance: (Float) -> Unit,
    onSave: () -> Unit,
    onOpenHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formattedDist = if (isCalibrating) "0 mm (Calibrating...)" else formatDistance(tapeDistanceMm, unit)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .testTag("ar_tape_view")
    ) {
        val w = maxWidth
        val h = maxHeight

        // Camera Grid & Crosshair Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val step = 30.dp.toPx()
            var x = 0f
            while (x < size.width) {
                drawLine(
                    color = CyanPrimary.copy(alpha = 0.08f),
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = 1f
                )
                x += step
            }
            var y = 0f
            while (y < size.height) {
                drawLine(
                    color = CyanPrimary.copy(alpha = 0.08f),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1f
                )
                y += step
            }

            // Center Crosshair
            val cx = size.width / 2f
            val cy = size.height / 2f
            drawCircle(
                color = CyanPrimary.copy(alpha = 0.3f),
                radius = 24.dp.toPx(),
                center = Offset(cx, cy),
                style = Stroke(width = 1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f)))
            )
            drawCircle(
                color = CyanPrimary,
                radius = 3.dp.toPx(),
                center = Offset(cx, cy)
            )
        }

        // Laser Measurement Path in Center
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.85f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Distance Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(DarkSurface.copy(alpha = 0.9f))
                    .border(1.dp, CyanPrimary, RoundedCornerShape(4.dp))
                    .padding(horizontal = 10.dp, vertical = 3.dp)
                    .testTag("tape_distance_badge")
            ) {
                Text(
                    text = formattedDist,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    color = CyanPrimary,
                    letterSpacing = 0.8.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Glowing Line with Pins
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Pin
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(CyanPrimary)
                        .border(2.dp, Color.White, CircleShape)
                )

                // Laser Beam
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(3.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(CyanPrimary, PurplePrimary, CyanPrimary)
                            )
                        )
                )

                // Right Pin
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(CyanPrimary)
                        .border(2.dp, Color.White, CircleShape)
                )
            }
        }

        // Right Floating Action Tools
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FloatingToolButton(
                icon = Icons.Default.Straighten,
                onClick = onToggleUnit,
                tag = "btn_unit_toggle"
            )
            FloatingToolButton(
                icon = Icons.Default.Refresh,
                onClick = onResetTape,
                tag = "btn_tape_reset"
            )
            FloatingToolButton(
                icon = Icons.Default.Save,
                onClick = onSave,
                tag = "btn_tape_save"
            )
            FloatingToolButton(
                icon = Icons.Default.Bookmark,
                onClick = onOpenHistory,
                tag = "btn_tape_history"
            )
        }

        // Bottom Range Readout & Slider
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "MEASURED RANGE: ${tapeDistanceMm.roundToInt()}mm / 12,000mm (12m Max)",
                fontFamily = FontFamily.Monospace,
                fontSize = 7.5.sp,
                fontWeight = FontWeight.Bold,
                color = CyanPrimary,
                modifier = Modifier
                    .background(DarkSurface.copy(alpha = 0.85f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )

            Slider(
                value = tapeDistanceMm,
                onValueChange = onSetDistance,
                valueRange = 50f..12000f,
                colors = SliderDefaults.colors(
                    thumbColor = CyanPrimary,
                    activeTrackColor = CyanPrimary,
                    inactiveTrackColor = DarkCardSecondary
                ),
                modifier = Modifier.fillMaxWidth(0.75f)
            )
        }
    }
}

// -------------------------------------------------------------
// 2. AI MATERIAL COUNTER SUB-VIEW
// -------------------------------------------------------------
@Composable
fun AiCounterSubView(
    countedItems: List<CounterBox>,
    totalCount: Int,
    onAddBox: (Float, Float) -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onReset: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val xPercent = offset.x / size.width
                    val yPercent = offset.y / size.height
                    onAddBox(xPercent, yPercent)
                }
            }
            .testTag("ai_counter_view")
    ) {
        val w = maxWidth
        val h = maxHeight

        // Camera Grid Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val step = 30.dp.toPx()
            var x = 0f
            while (x < size.width) {
                drawLine(
                    color = PurplePrimary.copy(alpha = 0.08f),
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = 1f
                )
                x += step
            }
            var y = 0f
            while (y < size.height) {
                drawLine(
                    color = PurplePrimary.copy(alpha = 0.08f),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1f
                )
                y += step
            }
        }

        // Render Bounding Boxes
        countedItems.forEach { box ->
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (box.xPercent * constraints.maxWidth).roundToInt(),
                            (box.yPercent * constraints.maxHeight).roundToInt()
                        )
                    }
                    .size(
                        width = (box.widthPercent * w.value).dp,
                        height = (box.heightPercent * h.value).dp
                    )
                    .clip(RoundedCornerShape(4.dp))
                    .background(PurplePrimary.copy(alpha = 0.15f))
                    .border(1.5.dp, PurplePrimary, RoundedCornerShape(4.dp))
                    .padding(3.dp)
            ) {
                Text(
                    text = box.label,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 7.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .background(PurplePrimary, RoundedCornerShape(2.dp))
                        .padding(horizontal = 3.dp, vertical = 1.dp)
                )
            }
        }

        // Top Left Counter HUD
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(DarkSurface.copy(alpha = 0.95f))
                .border(1.dp, PurplePrimary, RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "AI MATERIAL COUNT",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 7.sp,
                    color = PurplePrimary
                )
                Text(
                    text = "TOTAL: $totalCount",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
            }
        }

        // Right Control Actions
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FloatingToolButton(icon = Icons.Default.Add, onClick = onIncrement, tag = "btn_count_inc")
            FloatingToolButton(icon = Icons.Default.Remove, onClick = onDecrement, tag = "btn_count_dec")
            FloatingToolButton(icon = Icons.Default.Refresh, onClick = onReset, tag = "btn_count_reset")
            FloatingToolButton(icon = Icons.Default.Save, onClick = onSave, tag = "btn_count_save")
        }

        // Bottom Instructions
        Text(
            text = "TAP ON SCREEN TO TAG MATERIALS / OBJECTS IN REAL TIME",
            fontFamily = FontFamily.Monospace,
            fontSize = 6.5.sp,
            fontWeight = FontWeight.Bold,
            color = PurplePrimary,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 6.dp)
                .background(DarkSurface.copy(alpha = 0.85f), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

// -------------------------------------------------------------
// 3. 360° SPIRIT LEVEL & DUAL TUBE SUB-VIEW
// -------------------------------------------------------------
@Composable
fun SpiritLevelSubView(
    orientationData: OrientationData,
    onCalibrate: () -> Unit,
    onResetCalib: () -> Unit,
    onSimulateTilt: (Float, Float) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSliders by remember { mutableStateOf(false) }
    var simPitch by remember { mutableStateOf(0f) }
    var simRoll by remember { mutableStateOf(0f) }

    val statusText = if (orientationData.isLevel) "PERFECTLY BALANCED" else "ADJUST ALIGNMENT"
    val statusColor = if (orientationData.isLevel) EmeraldAccent else WarningGold

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("spirit_level_view"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Vertical Hand Level Tube
            VerticalTubeLevel(
                pitch = orientationData.pitch,
                isVerticalLevel = orientationData.isVerticalLevel,
                modifier = Modifier
                    .width(34.dp)
                    .fillMaxHeight(0.85f)
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Right Stack: 360 Bullseye + Horizontal Tube
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                // 360 Bullseye Level
                BullseyeLevelView(
                    bubbleX = orientationData.bubbleX,
                    bubbleY = orientationData.bubbleY,
                    isLevel = orientationData.isLevel,
                    modifier = Modifier.size(130.dp)
                )

                // Horizontal Tube Level
                HorizontalTubeLevel(
                    roll = orientationData.roll,
                    isHorizontalLevel = orientationData.isHorizontalLevel,
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(24.dp)
                )
            }
        }

        // Right Action Buttons
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            FloatingToolButton(
                icon = Icons.Default.Navigation,
                onClick = onCalibrate,
                tag = "btn_calibrate_level"
            )
            FloatingToolButton(
                icon = Icons.Default.Refresh,
                onClick = onResetCalib,
                tag = "btn_reset_level"
            )
            FloatingToolButton(
                icon = Icons.Default.Save,
                onClick = onSave,
                tag = "btn_save_level"
            )
        }

        // Bottom Readout
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "PITCH: ${"%.1f".format(orientationData.pitch)}° | ROLL: ${"%.1f".format(orientationData.roll)}° | $statusText",
                fontFamily = FontFamily.Monospace,
                fontSize = 7.5.sp,
                fontWeight = FontWeight.Bold,
                color = statusColor,
                modifier = Modifier
                    .background(DarkSurface.copy(alpha = 0.9f), RoundedCornerShape(4.dp))
                    .border(1.dp, statusColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                    .testTag("level_angle_readout")
            )
        }
    }
}

@Composable
fun BullseyeLevelView(
    bubbleX: Float,
    bubbleY: Float,
    isLevel: Boolean,
    modifier: Modifier = Modifier
) {
    val animX by animateFloatAsState(targetValue = bubbleX, animationSpec = tween(100), label = "bX")
    val animY by animateFloatAsState(targetValue = bubbleY, animationSpec = tween(100), label = "bY")

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(
                        CyanPrimary.copy(alpha = 0.15f),
                        DarkCardSurface.copy(alpha = 0.95f)
                    )
                )
            )
            .border(1.5.dp, CyanPrimary.copy(alpha = 0.5f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f

            // Outer dashed ring
            drawCircle(
                color = CyanPrimary.copy(alpha = 0.6f),
                radius = size.width * 0.45f,
                center = Offset(cx, cy),
                style = Stroke(width = 1.5.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f)))
            )

            // Middle ring
            drawCircle(
                color = CyanPrimary.copy(alpha = 0.4f),
                radius = size.width * 0.30f,
                center = Offset(cx, cy),
                style = Stroke(width = 1.dp.toPx())
            )

            // Center target ring
            drawCircle(
                color = if (isLevel) EmeraldAccent else CyanPrimary.copy(alpha = 0.8f),
                radius = size.width * 0.15f,
                center = Offset(cx, cy),
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Crosshair lines
            drawLine(
                color = CyanPrimary.copy(alpha = 0.35f),
                start = Offset(0f, cy),
                end = Offset(size.width, cy),
                strokeWidth = 1f
            )
            drawLine(
                color = CyanPrimary.copy(alpha = 0.35f),
                start = Offset(cx, 0f),
                end = Offset(cx, size.height),
                strokeWidth = 1f
            )

            // Fluid Bubble Dot
            val maxTravel = size.width * 0.35f
            val bx = cx + (animX * maxTravel)
            val by = cy + (animY * maxTravel)

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White,
                        if (isLevel) EmeraldAccent else CyanPrimary,
                        CyanSecondary
                    ),
                    center = Offset(bx, by),
                    radius = 9.dp.toPx()
                ),
                radius = 9.dp.toPx(),
                center = Offset(bx, by)
            )
        }
    }
}

@Composable
fun VerticalTubeLevel(
    pitch: Float,
    isVerticalLevel: Boolean,
    modifier: Modifier = Modifier
) {
    val normPitch = (pitch / 45f).coerceIn(-1f, 1f)
    val animPitch by animateFloatAsState(targetValue = normPitch, animationSpec = tween(100), label = "vPitch")

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface.copy(alpha = 0.9f))
            .border(2.dp, CyanPrimary.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f

            // Inner dark tube
            drawRoundRect(
                color = Color(0xFF020713),
                size = Size(size.width, size.height),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )

            // Calibration tick marks
            drawLine(CyanPrimary.copy(alpha = 0.8f), Offset(0f, cy), Offset(size.width, cy), strokeWidth = 2.dp.toPx())
            drawLine(CyanPrimary.copy(alpha = 0.4f), Offset(0f, cy - 18.dp.toPx()), Offset(size.width, cy - 18.dp.toPx()), strokeWidth = 1.dp.toPx())
            drawLine(CyanPrimary.copy(alpha = 0.4f), Offset(0f, cy + 18.dp.toPx()), Offset(size.width, cy + 18.dp.toPx()), strokeWidth = 1.dp.toPx())

            // Fluid Bubble
            val maxTravel = size.height * 0.4f
            val by = cy + (animPitch * maxTravel)

            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(Color.White, CyanPrimary, CyanSecondary)
                ),
                topLeft = Offset(cx - 6.dp.toPx(), by - 12.dp.toPx()),
                size = Size(12.dp.toPx(), 24.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )
        }
    }
}

@Composable
fun HorizontalTubeLevel(
    roll: Float,
    isHorizontalLevel: Boolean,
    modifier: Modifier = Modifier
) {
    val normRoll = (roll / 45f).coerceIn(-1f, 1f)
    val animRoll by animateFloatAsState(targetValue = normRoll, animationSpec = tween(100), label = "hRoll")

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface.copy(alpha = 0.9f))
            .border(2.dp, CyanPrimary.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f

            // Inner tube
            drawRoundRect(
                color = Color(0xFF020713),
                size = Size(size.width, size.height),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )

            // Calibration tick marks
            drawLine(CyanPrimary.copy(alpha = 0.8f), Offset(cx, 0f), Offset(cx, size.height), strokeWidth = 2.dp.toPx())
            drawLine(CyanPrimary.copy(alpha = 0.4f), Offset(cx - 20.dp.toPx(), 0f), Offset(cx - 20.dp.toPx(), size.height), strokeWidth = 1.dp.toPx())
            drawLine(CyanPrimary.copy(alpha = 0.4f), Offset(cx + 20.dp.toPx(), 0f), Offset(cx + 20.dp.toPx(), size.height), strokeWidth = 1.dp.toPx())

            // Fluid Bubble
            val maxTravel = size.width * 0.4f
            val bx = cx + (animRoll * maxTravel)

            drawRoundRect(
                brush = Brush.horizontalGradient(
                    listOf(Color.White, CyanPrimary, CyanSecondary)
                ),
                topLeft = Offset(bx - 12.dp.toPx(), cy - 5.dp.toPx()),
                size = Size(24.dp.toPx(), 10.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
        }
    }
}

// -------------------------------------------------------------
// 4. 90° AR RIGHT ANGLE SUB-VIEW
// -------------------------------------------------------------
@Composable
fun RightAngleSubView(
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
            .testTag("right_angle_view")
    ) {
        // Square Frame Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeW = 4.dp.toPx()

            // Left vertical leg
            drawLine(
                color = CyanPrimary,
                start = Offset(0f, 0f),
                end = Offset(0f, size.height),
                strokeWidth = strokeW
            )

            // Bottom horizontal leg
            drawLine(
                color = CyanPrimary,
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = strokeW
            )

            // Angle arc
            val arcRadius = 60.dp.toPx()
            drawArc(
                color = CyanPrimary.copy(alpha = 0.8f),
                startAngle = 270f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(-arcRadius, size.height - arcRadius),
                size = Size(arcRadius * 2, arcRadius * 2),
                style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)))
            )
        }

        // Millimeter Scales
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight(0.85f)
                .padding(start = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text("1000mm", fontFamily = FontFamily.Monospace, fontSize = 8.sp, color = CyanPrimary)
            Text("500mm", fontFamily = FontFamily.Monospace, fontSize = 8.sp, color = CyanPrimary)
            Text("0mm", fontFamily = FontFamily.Monospace, fontSize = 8.sp, color = CyanPrimary)
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(0.85f)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("0mm", fontFamily = FontFamily.Monospace, fontSize = 8.sp, color = CyanPrimary)
            Text("500mm", fontFamily = FontFamily.Monospace, fontSize = 8.sp, color = CyanPrimary)
            Text("1000mm", fontFamily = FontFamily.Monospace, fontSize = 8.sp, color = CyanPrimary)
        }

        // Angle Badge
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, bottom = 24.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(CyanPrimary)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "90.0° SQUARE",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                fontSize = 10.sp,
                color = Color.Black
            )
        }

        // Save Button
        FloatingToolButton(
            icon = Icons.Default.Save,
            onClick = onSave,
            tag = "btn_save_angle",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        )
    }
}

// -------------------------------------------------------------
// Helper UI Components
// -------------------------------------------------------------
@Composable
fun ToolButton(
    title: String,
    icon: ImageVector,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (isActive) CyanPrimary.copy(alpha = 0.25f) else Color(0xFF020713).copy(alpha = 0.8f)
            )
            .border(
                width = if (isActive) 1.5.dp else 1.dp,
                color = if (isActive) CyanPrimary else CyanPrimary.copy(alpha = 0.35f),
                shape = RoundedCornerShape(6.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 2.dp, vertical = 2.dp)
            .testTag("tool_btn_${title.lowercase().replace(' ', '_')}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isActive) CyanPrimary else TextSecondary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = title,
                fontFamily = FontFamily.Monospace,
                fontWeight = if (isActive) FontWeight.Black else FontWeight.Bold,
                fontSize = 6.5.sp,
                color = if (isActive) TextPrimary else TextSecondary,
                letterSpacing = 0.2.sp
            )
        }
    }
}

@Composable
fun FloatingToolButton(
    icon: ImageVector,
    onClick: () -> Unit,
    tag: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(DarkSurface.copy(alpha = 0.9f))
            .border(1.dp, CyanPrimary.copy(alpha = 0.6f), CircleShape)
            .clickable { onClick() }
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = tag,
            tint = CyanPrimary,
            modifier = Modifier.size(15.dp)
        )
    }
}

fun formatDistance(distMm: Float, unit: MeasurementUnit): String {
    return when (unit) {
        MeasurementUnit.MM -> "${distMm.roundToInt()} mm (${"%.2f".format(distMm / 304.8f)} ft)"
        MeasurementUnit.INCHES -> "${"%.2f".format(distMm / 25.4f)} inches"
        MeasurementUnit.FEET -> "${"%.2f".format(distMm / 304.8f)} ft"
    }
}
