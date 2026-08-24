package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prompts")
data class PromptItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String, // "9D VFX", "Hologram Art", "3D Architecture", "Fabrication", "Neural Code", "Cinematography"
    val promptText: String,
    val negativePrompt: String = "",
    val isFavorite: Boolean = false,
    val tags: String = ""
)
