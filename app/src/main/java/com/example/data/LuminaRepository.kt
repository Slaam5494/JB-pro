package com.example.data

import com.example.data.dao.ChatDao
import com.example.data.dao.MeasurementDao
import com.example.data.dao.PromptDao
import com.example.data.model.ChatMessage
import com.example.data.model.Measurement
import com.example.data.model.PromptItem
import kotlinx.coroutines.flow.Flow

class LuminaRepository(
    private val chatDao: ChatDao,
    private val measurementDao: MeasurementDao,
    private val promptDao: PromptDao
) {
    // Chat operations
    val allMessages: Flow<List<ChatMessage>> = chatDao.getAllMessages()

    suspend fun insertMessage(text: String, isUser: Boolean): Long {
        return chatDao.insertMessage(ChatMessage(text = text, isUser = isUser))
    }

    suspend fun clearChat() {
        chatDao.clearAllMessages()
    }

    // Measurement operations
    val allMeasurements: Flow<List<Measurement>> = measurementDao.getAllMeasurements()

    suspend fun saveMeasurement(
        toolType: String,
        formattedValue: String,
        rawValue: Double,
        unit: String,
        notes: String = ""
    ): Long {
        return measurementDao.insertMeasurement(
            Measurement(
                toolType = toolType,
                formattedValue = formattedValue,
                rawValue = rawValue,
                unit = unit,
                notes = notes
            )
        )
    }

    suspend fun deleteMeasurement(id: Long) {
        measurementDao.deleteMeasurementById(id)
    }

    suspend fun clearMeasurements() {
        measurementDao.clearAllMeasurements()
    }

    // Prompt operations
    val allPrompts: Flow<List<PromptItem>> = promptDao.getAllPrompts()
    val favoritePrompts: Flow<List<PromptItem>> = promptDao.getFavoritePrompts()

    fun getPromptsByCategory(category: String): Flow<List<PromptItem>> {
        return if (category == "ALL") promptDao.getAllPrompts() else promptDao.getPromptsByCategory(category)
    }

    suspend fun insertPrompt(prompt: PromptItem): Long {
        return promptDao.insertPrompt(prompt)
    }

    suspend fun toggleFavorite(id: Long, currentFavorite: Boolean) {
        promptDao.setFavorite(id, !currentFavorite)
    }

    suspend fun deletePrompt(id: Long) {
        promptDao.deletePromptById(id)
    }

    suspend fun getPromptCount(): Int {
        return promptDao.getCount()
    }
}
