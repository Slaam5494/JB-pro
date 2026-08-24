package com.example.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

data class OrientationData(
    val pitch: Float = 0f,       // X-axis tilt in degrees (-90 to 90)
    val roll: Float = 0f,        // Y-axis tilt in degrees (-180 to 180)
    val azimuth: Float = 0f,     // Compass heading (0 to 360)
    val bubbleX: Float = 0f,     // Normalized -1.0 to 1.0 for 2D bullseye
    val bubbleY: Float = 0f,     // Normalized -1.0 to 1.0 for 2D bullseye
    val isLevel: Boolean = true, // Both pitch and roll within tolerance (< 1.2 deg)
    val isHorizontalLevel: Boolean = true,
    val isVerticalLevel: Boolean = false,
    val isHardwareSensorActive: Boolean = false
)

class SensorDataProvider(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    private val gravitySensor = sensorManager?.getDefaultSensor(Sensor.TYPE_GRAVITY)

    private val _orientationData = MutableStateFlow(OrientationData())
    val orientationData: StateFlow<OrientationData> = _orientationData.asStateFlow()

    private val gravityMatrix = FloatArray(3)
    private val geomagneticMatrix = FloatArray(3)
    private var hasGravity = false
    private var hasGeomagnetic = false

    // Calibration offsets
    var pitchOffset: Float = 0f
    var rollOffset: Float = 0f

    fun startListening() {
        val targetSensor = gravitySensor ?: accelerometer
        targetSensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        magnetometer?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stopListening() {
        sensorManager?.unregisterListener(this)
    }

    fun calibrateCurrentPosition() {
        val current = _orientationData.value
        pitchOffset = current.pitch + pitchOffset
        rollOffset = current.roll + rollOffset
    }

    fun resetCalibration() {
        pitchOffset = 0f
        rollOffset = 0f
    }

    fun updateManualSimulation(pitch: Float, roll: Float) {
        val calPitch = pitch - pitchOffset
        val calRoll = roll - rollOffset
        val normX = (calRoll / 45f).coerceIn(-1f, 1f)
        val normY = (calPitch / 45f).coerceIn(-1f, 1f)
        val isLvl = abs(calPitch) < 1.2f && abs(calRoll) < 1.2f

        _orientationData.value = OrientationData(
            pitch = calPitch,
            roll = calRoll,
            azimuth = 0f,
            bubbleX = normX,
            bubbleY = normY,
            isLevel = isLvl,
            isHorizontalLevel = abs(calRoll) < 1.2f,
            isVerticalLevel = abs(calPitch) < 1.2f,
            isHardwareSensorActive = false
        )
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER || event.sensor.type == Sensor.TYPE_GRAVITY) {
            // Low-pass filter for smooth readout
            val alpha = 0.8f
            gravityMatrix[0] = alpha * gravityMatrix[0] + (1 - alpha) * event.values[0]
            gravityMatrix[1] = alpha * gravityMatrix[1] + (1 - alpha) * event.values[1]
            gravityMatrix[2] = alpha * gravityMatrix[2] + (1 - alpha) * event.values[2]
            hasGravity = true
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            val alpha = 0.8f
            geomagneticMatrix[0] = alpha * geomagneticMatrix[0] + (1 - alpha) * event.values[0]
            geomagneticMatrix[1] = alpha * geomagneticMatrix[1] + (1 - alpha) * event.values[1]
            geomagneticMatrix[2] = alpha * geomagneticMatrix[2] + (1 - alpha) * event.values[2]
            hasGeomagnetic = true
        }

        if (hasGravity) {
            val ax = gravityMatrix[0]
            val ay = gravityMatrix[1]
            val az = gravityMatrix[2]

            // Calculate pitch and roll directly from gravity vector
            val rawPitch = (atan2(ay.toDouble(), sqrt((ax * ax + az * az).toDouble())) * (180.0 / Math.PI)).toFloat()
            val rawRoll = (atan2(-ax.toDouble(), az.toDouble()) * (180.0 / Math.PI)).toFloat()

            var rawAzimuth = 0f
            if (hasGeomagnetic) {
                val rMatrix = FloatArray(9)
                val iMatrix = FloatArray(9)
                if (SensorManager.getRotationMatrix(rMatrix, iMatrix, gravityMatrix, geomagneticMatrix)) {
                    val orientation = FloatArray(3)
                    SensorManager.getOrientation(rMatrix, orientation)
                    rawAzimuth = (Math.toDegrees(orientation[0].toDouble()).toFloat() + 360f) % 360f
                }
            }

            val calPitch = rawPitch - pitchOffset
            val calRoll = rawRoll - rollOffset

            val normX = (calRoll / 45f).coerceIn(-1f, 1f)
            val normY = (calPitch / 45f).coerceIn(-1f, 1f)
            val isLvl = abs(calPitch) < 1.2f && abs(calRoll) < 1.2f

            _orientationData.value = OrientationData(
                pitch = calPitch,
                roll = calRoll,
                azimuth = rawAzimuth,
                bubbleX = normX,
                bubbleY = normY,
                isLevel = isLvl,
                isHorizontalLevel = abs(calRoll) < 1.2f,
                isVerticalLevel = abs(calPitch) < 1.2f,
                isHardwareSensorActive = true
            )
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
