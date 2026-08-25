package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.ChatDao
import com.example.data.dao.MeasurementDao
import com.example.data.dao.PromptDao
import com.example.data.model.ChatMessage
import com.example.data.model.MeasurementRecord
import com.example.data.model.PromptItem

@Database(
    entities = [ChatMessage::class, MeasurementRecord::class, PromptItem::class],
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

        fun getDatabase(context: Context): LuminaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LuminaDatabase::class.java,
                    "lumina_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
