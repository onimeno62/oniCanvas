package com.onimeno.onicanvas.feature.color.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ColorPaletteDao {
    @Query("SELECT * FROM color_palettes ORDER BY sortOrder ASC, createdAt ASC")
    fun observeAll(): Flow<List<ColorPaletteEntity>>

    @Query("SELECT * FROM color_palettes WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ColorPaletteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(palette: ColorPaletteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(palettes: List<ColorPaletteEntity>)

    @Delete
    suspend fun delete(palette: ColorPaletteEntity)

    @Query("DELETE FROM color_palettes WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM color_palettes")
    suspend fun count(): Int
}

@Dao
interface RecentColorDao {
    @Query("SELECT * FROM recent_colors ORDER BY lastUsedTimestamp DESC LIMIT :limit")
    fun observeRecent(limit: Int = 24): Flow<List<RecentColorEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(recent: RecentColorEntity)

    @Query("DELETE FROM recent_colors WHERE hex = :hex")
    suspend fun delete(hex: String)

    @Query("DELETE FROM recent_colors")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM recent_colors")
    suspend fun count(): Int
}
