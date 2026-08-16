package com.onimeno.onicanvas.feature.color.data

import com.onimeno.onicanvas.feature.color.model.ColorPalette
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ColorWorkflowRepositoryTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakePaletteDao: FakeColorPaletteDao
    private lateinit var fakeRecentDao: FakeRecentColorDao
    private lateinit var repository: ColorWorkflowRepository

    @Before
    fun setup() {
        fakePaletteDao = FakeColorPaletteDao()
        fakeRecentDao = FakeRecentColorDao()
        repository = ColorWorkflowRepository(
            colorPaletteDao = fakePaletteDao,
            recentColorDao = fakeRecentDao,
            ioDispatcher = testDispatcher
        )
    }

    @Test
    fun `palettes initializes default palettes when empty`() = runTest(testDispatcher) {
        val palettes = repository.palettes.first()
        assertTrue(palettes.isNotEmpty())
        assertTrue(palettes.any { it.name == "Digital Art Essentials" })
    }

    @Test
    fun `createPalette creates and persists new palette`() = runTest(testDispatcher) {
        val created = repository.createPalette("My Concept Art", listOf("#FF5500", "#00FF55"))
        assertNotNull(created)
        assertEquals("My Concept Art", created.name)
        assertEquals(2, created.colors.size)

        val all = repository.palettes.first()
        assertTrue(all.any { it.id == created.id })
    }

    @Test
    fun `deletePalette removes palette from persistence`() = runTest(testDispatcher) {
        val created = repository.createPalette("To Delete", listOf("#111111"))
        repository.deletePalette(created.id)

        val all = repository.palettes.first()
        assertFalse(all.any { it.id == created.id })
    }

    @Test
    fun `addColorToPalette appends color and preserves persistence`() = runTest(testDispatcher) {
        val created = repository.createPalette("Palette With Colors", listOf("#111111"))
        repository.addColorToPalette(created.id, "#222222")

        val all = repository.palettes.first()
        val found = all.first { it.id == created.id }
        assertEquals(listOf("#111111", "#222222"), found.colors)
    }

    @Test
    fun `removeColorFromPalette removes specific color`() = runTest(testDispatcher) {
        val created = repository.createPalette("Palette To Prune", listOf("#111111", "#222222", "#333333"))
        repository.removeColorFromPalette(created.id, "#222222")

        val all = repository.palettes.first()
        val found = all.first { it.id == created.id }
        assertEquals(listOf("#111111", "#333333"), found.colors)
    }

    @Test
    fun `recordRecentColor records and retrieves newest first`() = runTest(testDispatcher) {
        repository.recordRecentColor("#111111")
        repository.recordRecentColor("#222222")
        repository.recordRecentColor("#333333")

        val recents = repository.recentColors.first()
        assertEquals("#333333", recents.first())
    }

    @Test
    fun `clearRecentColors removes all recorded recents`() = runTest(testDispatcher) {
        repository.recordRecentColor("#111111")
        repository.clearRecentColors()

        val count = fakeRecentDao.count()
        assertEquals(0, count)
    }
}
