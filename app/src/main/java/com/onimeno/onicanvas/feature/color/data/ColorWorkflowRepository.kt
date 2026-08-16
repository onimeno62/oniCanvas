package com.onimeno.onicanvas.feature.color.data

import com.onimeno.onicanvas.feature.color.model.ColorConversion
import com.onimeno.onicanvas.feature.color.model.ColorPalette
import com.onimeno.onicanvas.feature.color.model.RecentColors
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ColorWorkflowRepository(
    private val colorPaletteDao: ColorPaletteDao,
    private val recentColorDao: RecentColorDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val json = Json { ignoreUnknownKeys = true }

    val palettes: Flow<List<ColorPalette>> = colorPaletteDao.observeAll()
        .map { entities ->
            if (entities.isEmpty()) {
                // Initialize default palettes
                seedDefaultsInternal()
                ColorPalette.defaultPalettes()
            } else {
                entities.map { it.toDomain() }
            }
        }
        .flowOn(ioDispatcher)

    val recentColors: Flow<List<String>> = recentColorDao.observeRecent(RecentColors.DEFAULT_MAX_CAPACITY)
        .map { list ->
            if (list.isEmpty()) {
                RecentColors.INITIAL_DEFAULTS
            } else {
                list.map { it.hex }
            }
        }
        .flowOn(ioDispatcher)

    suspend fun savePalette(palette: ColorPalette) = withContext(ioDispatcher) {
        colorPaletteDao.upsert(palette.toEntity())
    }

    suspend fun createPalette(name: String, initialColors: List<String> = emptyList()): ColorPalette = withContext(ioDispatcher) {
        val newPalette = ColorPalette(
            id = "palette_${System.currentTimeMillis()}_${(100..999).random()}",
            name = name.trim().ifBlank { "New Palette" },
            colors = initialColors.mapNotNull { ColorConversion.normalizeHex(it) },
            isDefault = false,
            createdAt = System.currentTimeMillis()
        )
        colorPaletteDao.upsert(newPalette.toEntity())
        newPalette
    }

    suspend fun deletePalette(paletteId: String) = withContext(ioDispatcher) {
        colorPaletteDao.deleteById(paletteId)
    }

    private var lastRecordedTimestamp = 0L

    suspend fun recordRecentColor(hexInput: String) = withContext(ioDispatcher) {
        val normalized = ColorConversion.normalizeHex(hexInput) ?: return@withContext
        val now = System.currentTimeMillis()
        val timestamp = if (now <= lastRecordedTimestamp) lastRecordedTimestamp + 1 else now
        lastRecordedTimestamp = timestamp
        recentColorDao.upsert(RecentColorEntity(hex = normalized, lastUsedTimestamp = timestamp))
    }

    suspend fun removeRecentColor(hexInput: String) = withContext(ioDispatcher) {
        val normalized = ColorConversion.normalizeHex(hexInput) ?: hexInput.uppercase()
        recentColorDao.delete(normalized)
    }

    suspend fun clearRecentColors() = withContext(ioDispatcher) {
        recentColorDao.clearAll()
    }

    suspend fun addColorToPalette(paletteId: String, hexInput: String) = withContext(ioDispatcher) {
        val entity = colorPaletteDao.getById(paletteId) ?: return@withContext
        val domain = entity.toDomain()
        val updated = domain.addColor(hexInput)
        colorPaletteDao.upsert(updated.toEntity(entity.sortOrder))
    }

    suspend fun removeColorFromPalette(paletteId: String, hexInput: String) = withContext(ioDispatcher) {
        val entity = colorPaletteDao.getById(paletteId) ?: return@withContext
        val domain = entity.toDomain()
        val updated = domain.removeColor(hexInput)
        colorPaletteDao.upsert(updated.toEntity(entity.sortOrder))
    }

    private suspend fun seedDefaultsInternal() {
        if (colorPaletteDao.count() == 0) {
            val defaults = ColorPalette.defaultPalettes().mapIndexed { index, palette ->
                palette.toEntity(sortOrder = index)
            }
            colorPaletteDao.upsertAll(defaults)
        }
    }

    private fun ColorPaletteEntity.toDomain(): ColorPalette {
        val parsedColors = runCatching {
            json.decodeFromString<List<String>>(colorsJson)
        }.getOrDefault(emptyList())
        return ColorPalette(
            id = id,
            name = name,
            colors = parsedColors,
            isDefault = isDefault,
            createdAt = createdAt
        )
    }

    private fun ColorPalette.toEntity(sortOrder: Int = 0): ColorPaletteEntity {
        return ColorPaletteEntity(
            id = id,
            name = name,
            colorsJson = json.encodeToString(colors),
            isDefault = isDefault,
            sortOrder = sortOrder,
            createdAt = createdAt
        )
    }
}
