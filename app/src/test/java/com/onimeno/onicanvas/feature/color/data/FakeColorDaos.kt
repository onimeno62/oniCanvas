package com.onimeno.onicanvas.feature.color.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeColorPaletteDao : ColorPaletteDao {
    private val palettes = MutableStateFlow<Map<String, ColorPaletteEntity>>(emptyMap())

    override fun observeAll(): Flow<List<ColorPaletteEntity>> =
        palettes.map { it.values.sortedWith(compareBy<ColorPaletteEntity> { it.sortOrder }.thenBy { it.createdAt }) }

    override suspend fun getById(id: String): ColorPaletteEntity? = palettes.value[id]

    override suspend fun upsert(palette: ColorPaletteEntity) {
        val current = palettes.value.toMutableMap()
        current[palette.id] = palette
        palettes.value = current
    }

    override suspend fun upsertAll(palettesList: List<ColorPaletteEntity>) {
        val current = palettes.value.toMutableMap()
        palettesList.forEach { current[it.id] = it }
        palettes.value = current
    }

    override suspend fun delete(palette: ColorPaletteEntity) {
        val current = palettes.value.toMutableMap()
        current.remove(palette.id)
        palettes.value = current
    }

    override suspend fun deleteById(id: String) {
        val current = palettes.value.toMutableMap()
        current.remove(id)
        palettes.value = current
    }

    override suspend fun count(): Int = palettes.value.size
}

class FakeRecentColorDao : RecentColorDao {
    private val recents = MutableStateFlow<Map<String, RecentColorEntity>>(emptyMap())

    override fun observeRecent(limit: Int): Flow<List<RecentColorEntity>> =
        recents.map {
            it.values.sortedByDescending { entity -> entity.lastUsedTimestamp }.take(limit)
        }

    override suspend fun upsert(recent: RecentColorEntity) {
        val current = recents.value.toMutableMap()
        current[recent.hex] = recent
        recents.value = current
    }

    override suspend fun delete(hex: String) {
        val current = recents.value.toMutableMap()
        current.remove(hex)
        recents.value = current
    }

    override suspend fun clearAll() {
        recents.value = emptyMap()
    }

    override suspend fun count(): Int = recents.value.size
}
