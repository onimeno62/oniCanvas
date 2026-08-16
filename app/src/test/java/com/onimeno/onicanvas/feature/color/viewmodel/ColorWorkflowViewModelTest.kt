package com.onimeno.onicanvas.feature.color.viewmodel

import com.onimeno.onicanvas.feature.color.data.ColorWorkflowRepository
import com.onimeno.onicanvas.feature.color.data.FakeColorPaletteDao
import com.onimeno.onicanvas.feature.color.data.FakeRecentColorDao
import com.onimeno.onicanvas.feature.color.model.ColorModel
import com.onimeno.onicanvas.feature.color.state.ColorPickerMode
import com.onimeno.onicanvas.feature.color.state.PaletteDialogMode
import com.onimeno.onicanvas.feature.connection.data.ConnectionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ColorWorkflowViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakePaletteDao: FakeColorPaletteDao
    private lateinit var fakeRecentDao: FakeRecentColorDao
    private lateinit var colorWorkflowRepository: ColorWorkflowRepository
    private lateinit var connectionRepository: ConnectionRepository
    private lateinit var viewModel: ColorWorkflowViewModel

    @Before
    fun setUp() = runTest {
        Dispatchers.setMain(testDispatcher)
        fakePaletteDao = FakeColorPaletteDao()
        fakeRecentDao = FakeRecentColorDao()
        colorWorkflowRepository = ColorWorkflowRepository(
            colorPaletteDao = fakePaletteDao,
            recentColorDao = fakeRecentDao,
            ioDispatcher = testDispatcher
        )
        connectionRepository = ConnectionRepository()
        viewModel = ColorWorkflowViewModel(colorWorkflowRepository, connectionRepository)
    }

    @After
    fun tearDown() {
        connectionRepository.close()
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState initializes with default state and palettes`() = runTest(testDispatcher) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.selectedColor)
        assertEquals("#80CBC4", state.selectedColor.hex)
        assertTrue(state.palettes.isNotEmpty())
        assertNotNull(state.selectedPalette)
    }

    @Test
    fun `updateHsv updates selectedColor and hexInputText accurately`() = runTest(testDispatcher) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.updateHsv(0f, 100f, 100f) // Pure Red
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("#FF0000", state.selectedColor.hex)
        assertEquals("#FF0000", state.hexInputText)
        assertEquals(255, state.selectedColor.r)
        assertEquals(0, state.selectedColor.g)
        assertEquals(0, state.selectedColor.b)
    }

    @Test
    fun `updateRgb updates selectedColor and HSV accurately`() = runTest(testDispatcher) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.updateRed(0)
        viewModel.updateGreen(0)
        viewModel.updateBlue(255) // Pure Blue
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("#0000FF", state.selectedColor.hex)
        assertEquals(240f, state.selectedColor.hsv.hue, 0.1f)
        assertEquals(100f, state.selectedColor.hsv.saturation, 0.1f)
        assertEquals(100f, state.selectedColor.hsv.value, 0.1f)
    }

    @Test
    fun `updateHexInput and applyHexInput updates color`() = runTest(testDispatcher) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.updateHexInput("#FF5500")
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isHexInputValid)

        viewModel.applyHexInput()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("#FF5500", state.selectedColor.hex)
    }

    @Test
    fun `updateHexInput with invalid hex marks invalid and preserves previous color`() = runTest(testDispatcher) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        testDispatcher.scheduler.advanceUntilIdle()

        val origColor = viewModel.uiState.value.selectedColor
        viewModel.updateHexInput("INVALID_HEX")
        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isHexInputValid)

        viewModel.applyHexInput()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(origColor.hex, viewModel.uiState.value.selectedColor.hex)
    }

    @Test
    fun `swapWithPreviousColor swaps active and previous colors`() = runTest(testDispatcher) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.selectColor(ColorModel.fromRgb(255, 0, 0), recordRecent = false)
        testDispatcher.scheduler.advanceUntilIdle()

        val prevHex = viewModel.uiState.value.previousColor.hex
        val curHex = viewModel.uiState.value.selectedColor.hex

        viewModel.swapWithPreviousColor()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(prevHex, viewModel.uiState.value.selectedColor.hex)
        assertEquals(curHex, viewModel.uiState.value.previousColor.hex)
    }

    @Test
    fun `commitColor records to recent colors`() = runTest(testDispatcher) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.selectColor(ColorModel.fromRgb(255, 128, 0), recordRecent = false)
        viewModel.commitColor()
        testDispatcher.scheduler.advanceUntilIdle()

        val recents = viewModel.uiState.value.recentColors
        assertTrue(recents.contains("#FF8000"))
    }

    @Test
    fun `palette dialogs create and delete palettes`() = runTest(testDispatcher) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        testDispatcher.scheduler.advanceUntilIdle()

        // 1. Create dialog
        viewModel.openCreatePaletteDialog()
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(PaletteDialogMode.CREATE, viewModel.uiState.value.dialogMode)

        viewModel.updateDialogInput("Studio Concept")
        viewModel.confirmDialog()
        testDispatcher.scheduler.advanceUntilIdle()

        val palettes = viewModel.uiState.value.palettes
        val created = palettes.find { it.name == "Studio Concept" }
        assertNotNull(created)

        // 2. Rename dialog
        viewModel.openRenamePaletteDialog(created!!.id)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(PaletteDialogMode.RENAME, viewModel.uiState.value.dialogMode)
        viewModel.updateDialogInput("Studio Concept V2")
        viewModel.confirmDialog()
        testDispatcher.scheduler.advanceUntilIdle()

        val renamed = viewModel.uiState.value.palettes.find { it.id == created.id }
        assertEquals("Studio Concept V2", renamed?.name)

        // 3. Delete dialog
        viewModel.openDeletePaletteDialog(created.id)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(PaletteDialogMode.DELETE_CONFIRM, viewModel.uiState.value.dialogMode)
        viewModel.confirmDialog()
        testDispatcher.scheduler.advanceUntilIdle()

        val remaining = viewModel.uiState.value.palettes.find { it.id == created.id }
        assertEquals(null, remaining)
    }

    @Test
    fun `setActiveMode updates mode`() = runTest(testDispatcher) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.setActiveMode(ColorPickerMode.RGB)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(ColorPickerMode.RGB, viewModel.uiState.value.activeMode)

        viewModel.setActiveMode(ColorPickerMode.PALETTES)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(ColorPickerMode.PALETTES, viewModel.uiState.value.activeMode)
    }
}
