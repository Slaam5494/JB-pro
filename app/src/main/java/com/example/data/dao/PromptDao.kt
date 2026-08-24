package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PromptItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PromptDao {
    @Query("SELECT * FROM prompts ORDER BY id ASC")
    fun getAllPrompts(): Flow<List<PromptItem>>

    @Query("SELECT * FROM prompts WHERE category = :category ORDER BY id ASC")
    fun getPromptsByCategory(category: String): Flow<List<PromptItem>>

    @Query("SELECT * FROM prompts WHERE isFavorite = 1 ORDER BY id ASC")
    fun getFavoritePrompts(): Flow<List<PromptItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrompt(prompt: PromptItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(prompts: List<PromptItem>)

    @Update
    suspend fun updatePrompt(prompt: PromptItem)

    @Query("UPDATE prompts SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean)

    @Query("DELETE FROM prompts WHERE id = :id")
    suspend fun deletePromptById(id: Long)

    @Query("SELECT COUNT(*) FROM prompts")
    suspend fun getCount(): Int
}
