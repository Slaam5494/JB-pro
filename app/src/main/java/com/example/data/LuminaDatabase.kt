package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.ChatDao
import com.example.data.dao.MeasurementDao
import com.example.data.dao.PromptDao
import com.example.data.model.ChatMessage
import com.example.data.model.Measurement
import com.example.data.model.PromptItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [ChatMessage::class, Measurement::class, PromptItem::class],
    version = 1,
    exportSchema = false
)
abstract class LuminaDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun measurementDao(): MeasurementDao
    abstract fun promptDao(): PromptDao

    companion object {
        @Volatile
        private var INSTANCE: LuminaDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): LuminaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LuminaDatabase::class.java,
                    "lumina_suite_db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Prepopulate default prompts
                        scope.launch(Dispatchers.IO) {
                            INSTANCE?.let { database ->
                                populateDefaultPrompts(database.promptDao())
                                populateInitialChat(database.chatDao())
                            }
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateInitialChat(chatDao: ChatDao) {
            chatDao.insertMessage(
                ChatMessage(
                    text = "Lumina AI Neural Engine JB_V2.5® initialized. All generation, fabrication, and AR toolsets active. How can I assist you?",
                    isUser = false
                )
            )
        }

        private suspend fun populateDefaultPrompts(promptDao: PromptDao) {
            val defaults = listOf(
                PromptItem(
                    title = "9D Holographic Portal",
                    category = "9D VFX",
                    promptText = "Ultra-detailed 9D volumetric holographic portal pulsing with electric cyan and ultraviolet particle flow, volumetric mist, octagonal quantum field, Ray-traced refraction, 8K HDR",
                    negativePrompt = "blurry, lowres, oversaturated, deformed",
                    tags = "VFX, Hologram, Portal, Quantum"
                ),
                PromptItem(
                    title = "Cybernetic Neural Core",
                    category = "9D VFX",
                    promptText = "Futuristic quantum neural CPU processor surrounded by liquid neon cyan conduits, glowing circuit boards, floating nanite swarm, cinematic studio lighting",
                    negativePrompt = "grainy, dark, flat",
                    tags = "Cyber, Core, Neural, Neon"
                ),
                PromptItem(
                    title = "Cyberpunk Neo-Tokyo Skyline",
                    category = "Hologram Art",
                    promptText = "Cinematic aerial view of futuristic Neo-Tokyo at midnight, glowing neon billboards in Japanese typography, rain-slicked chrome skybridges, flying VTOL vehicles",
                    negativePrompt = "blurry, pixelated, washed out",
                    tags = "Cyberpunk, Cityscape, Neon, Aerial"
                ),
                PromptItem(
                    title = "Floating Holographic Geode",
                    category = "Hologram Art",
                    promptText = "Luminescent crystalline obsidian geode levitating in zero gravity, glowing magenta and cyan amethyst veins, particle dust, iridescent refraction, Unreal Engine 5 render",
                    negativePrompt = "flat texture, bad lighting",
                    tags = "Crystal, Hologram, 3D, Iridescent"
                ),
                PromptItem(
                    title = "Modular Habitat Arcology",
                    category = "3D Architecture",
                    promptText = "Parametric architectural blueprint of a self-sustaining futuristic arcology dome, glass solar arrays, vertical hydroponic gardens, titanium frame, isometric architectural elevation",
                    negativePrompt = "distorted angles, unaligned grids",
                    tags = "Architecture, Arcology, Blueprint, Isometric"
                ),
                PromptItem(
                    title = "Precision Robotic Arm Assembly",
                    category = "Fabrication",
                    promptText = "Industrial blueprint schematic of high-precision multi-axis robotic laser welding arm, mechanical exploded view, exact mm measurement callouts, laser collimator",
                    negativePrompt = "hand drawn, imprecise lines",
                    tags = "Fabrication, Robotic, Schematic, Laser"
                ),
                PromptItem(
                    title = "Quantum State Matrix Shader",
                    category = "Neural Code",
                    promptText = "Write high-performance GLSL fragment shader for real-time 9D raymarched volumetric nebula with chromatic dispersion and audio-reactive pulsations",
                    tags = "Code, Shader, GLSL, Volumetric"
                ),
                PromptItem(
                    title = "Cinematic Anamorphic Blade Runner",
                    category = "Cinematography",
                    promptText = "Anamorphic 2.39:1 widescreen shot, cyber detective in holographic trench coat standing under torrential acid rain, teal and orange neon volumetric fog, ARRI Alexa LF 65mm",
                    negativePrompt = "soap opera effect, cartoonish",
                    tags = "Cinema, Anamorphic, Neon, Rain"
                )
            )
            promptDao.insertAll(defaults)
        }
    }
}
