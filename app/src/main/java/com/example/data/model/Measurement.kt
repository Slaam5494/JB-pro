package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "measurements")
data class Measurement(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val toolType: String, // "AR Tape", "360 Level", "AI Counter", "90 Angle"
    val formattedValue: String,
    val rawValue: Double,
    val unit: String,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)
