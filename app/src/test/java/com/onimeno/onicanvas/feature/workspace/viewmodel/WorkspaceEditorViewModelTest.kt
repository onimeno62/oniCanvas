package com.onimeno.onicanvas.feature.workspace.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.onimeno.onicanvas.feature.settings.data.SettingsRepository
import com.onimeno.onicanvas.feature.workspace.data.FakeWorkspaceDao
import com.onimeno.onicanvas.feature.workspace.data.WorkspaceRepository
import com.onimeno.onicanvas.feature.workspace.data.toEntity
import com.onimeno.onicanvas.feature.workspace.state.MacroAction
import com.onimeno.onicanvas.feature.workspace.state.MacroButton
import com.onimeno.onicanvas.feature.workspace.state.MacroPage
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceEditorUiState
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceItem
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
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

class FakeDataStore : DataStore<Preferences> {
    private val state = MutableStateFlow<Preferences>(emptyPreferences())
    override val data: Flow<Preferences> = state
    override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences {
        val updated = transform(state.value)
        state.value = updated
        return updated
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class WorkspaceEditorViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeDao: FakeWorkspaceDao
    private lateinit var repository: WorkspaceRepository
    private lateinit var viewModel: WorkspaceEditorViewModel

    private val sampleWorkspace = WorkspaceItem(
        id = "ws_editor_test",
        name = "Photoshop Pro",
        description = "Digital painting",
        targetApp = "Photoshop",
        buttonCount = 4,
        iconName = "brush",
        lastUsed = "2026-08-11",
        gridSize = 2, // 2x2 grid = max 4 buttons
        macroPages = listOf(
            MacroPage(
                id = "page_1",
                name = "Main",
                orderIndex = 0,
                buttons = listOf(
                    MacroButton("btn_1", 0, "Undo", "undo", MacroAction.Undo),
                    MacroButton("btn_2", 1, "Redo", "redo", MacroAction.Redo),
                    MacroButton("btn_3", 2, "Save", "save", MacroAction.Save),
                    MacroButton("btn_4", 3, "Brush", "brush", MacroAction.Brush)
                )
            )
        )
    )

    @Before
    fun setUp() = runTest {
        Dispatchers.setMain(testDispatcher)
        fakeDao = FakeWorkspaceDao()
        fakeDao.upsert(sampleWorkspace.toEntity())

        val fakeSettingsRepo = SettingsRepository(FakeDataStore())
        repository = WorkspaceRepository(fakeDao, fakeSettingsRepo)
        viewModel = WorkspaceEditorViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadWorkspace_loadsInitialWorkspaceState() = runTest {
        viewModel.loadWorkspace("ws_editor_test")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value as WorkspaceEditorUiState.Success
        assertEquals("Photoshop Pro", state.editingWorkspace.name)
        assertFalse(state.isDirty)
        assertFalse(state.canUndo)
        assertFalse(state.canRedo)
    }

    @Test
    fun setGridSize_resizesGridAndSupportsUndoRedo() = runTest {
        viewModel.loadWorkspace("ws_editor_test")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.setGridSize(3)
        var state = viewModel.uiState.value as WorkspaceEditorUiState.Success
        assertEquals(3, state.editingWorkspace.gridSize)
        assertTrue(state.isDirty)
        assertTrue(state.canUndo)

        viewModel.undo()
        state = viewModel.uiState.value as WorkspaceEditorUiState.Success
        assertEquals(2, state.editingWorkspace.gridSize)
        assertFalse(state.isDirty)

        viewModel.redo()
        state = viewModel.uiState.value as WorkspaceEditorUiState.Success
        assertEquals(3, state.editingWorkspace.gridSize)
    }

    @Test
    fun addButton_enforcesGridCapacity() = runTest {
        viewModel.loadWorkspace("ws_editor_test")
        testDispatcher.scheduler.advanceUntilIdle()

        // sampleWorkspace is 2x2 grid (capacity 4), already has 4 buttons
        viewModel.addButton("Extra", "add", MacroAction.Fill)
        val state = viewModel.uiState.value as WorkspaceEditorUiState.Success
        assertEquals(4, state.editingWorkspace.macroPages.single().buttons.size)
        assertFalse(state.isDirty)
    }

    @Test
    fun buttonOperations_addRemoveMoveUpdate_workWithUndoRedo() = runTest {
        viewModel.loadWorkspace("ws_editor_test")
        testDispatcher.scheduler.advanceUntilIdle()

        // 1. Remove button 4
        viewModel.removeButton("page_1", "btn_4")
        var state = viewModel.uiState.value as WorkspaceEditorUiState.Success
        assertEquals(3, state.editingWorkspace.macroPages.single().buttons.size)

        // 2. Add new button (capacity now 3 < 4)
        viewModel.addButton("Fill", "fill", MacroAction.Fill)
        state = viewModel.uiState.value as WorkspaceEditorUiState.Success
        assertEquals(4, state.editingWorkspace.macroPages.single().buttons.size)
        val newBtn = state.editingWorkspace.macroPages.single().buttons.last()
        assertEquals("Fill", newBtn.label)

        // 3. Update button
        viewModel.updateButton("page_1", newBtn.id, "Bucket Fill", "fill", MacroAction.Fill)
        state = viewModel.uiState.value as WorkspaceEditorUiState.Success
        assertEquals("Bucket Fill", state.editingWorkspace.macroPages.single().buttons.last().label)

        // 4. Move button
        viewModel.moveButton("page_1", 3, 0)
        state = viewModel.uiState.value as WorkspaceEditorUiState.Success
        assertEquals("Bucket Fill", state.editingWorkspace.macroPages.single().buttons.first().label)

        // Undo move
        viewModel.undo()
        state = viewModel.uiState.value as WorkspaceEditorUiState.Success
        assertEquals("Bucket Fill", state.editingWorkspace.macroPages.single().buttons.last().label)
    }

    @Test
    fun themeAndIconCustomization_updatesWorkspaceState() = runTest {
        viewModel.loadWorkspace("ws_editor_test")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.setWorkspaceTheme(WorkspaceTheme.AMBER.key)
        viewModel.setWorkspaceIcon("palette")

        var state = viewModel.uiState.value as WorkspaceEditorUiState.Success
        assertEquals(WorkspaceTheme.AMBER.key, state.editingWorkspace.customization.themeKey)
        assertEquals("palette", state.editingWorkspace.iconName)
        assertTrue(state.isDirty)

        viewModel.undo()
        state = viewModel.uiState.value as WorkspaceEditorUiState.Success
        assertEquals("brush", state.editingWorkspace.iconName)
        assertEquals(WorkspaceTheme.AMBER.key, state.editingWorkspace.customization.themeKey)

        viewModel.undo()
        state = viewModel.uiState.value as WorkspaceEditorUiState.Success
        assertEquals(WorkspaceTheme.DEFAULT.key, state.editingWorkspace.customization.themeKey)
    }

    @Test
    fun buttonColorAndAccentColor_updatesWithUndoRedo() = runTest {
        viewModel.loadWorkspace("ws_editor_test")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.setWorkspaceAccentColor("#EC4899")
        var state = viewModel.uiState.value as WorkspaceEditorUiState.Success
        assertEquals("#EC4899", state.editingWorkspace.customization.accentColorHex)
        assertTrue(state.isDirty)

        viewModel.setButtonColor("page_1", "btn_1", "#EF4444")
        state = viewModel.uiState.value as WorkspaceEditorUiState.Success
        val btn1 = state.editingWorkspace.macroPages.single().buttons.first { it.id == "btn_1" }
        assertEquals("#EF4444", btn1.colorHex)

        viewModel.undo()
        state = viewModel.uiState.value as WorkspaceEditorUiState.Success
        val btn1AfterUndo = state.editingWorkspace.macroPages.single().buttons.first { it.id == "btn_1" }
        assertEquals(null, btn1AfterUndo.colorHex)

        viewModel.undo()
        state = viewModel.uiState.value as WorkspaceEditorUiState.Success
        assertEquals(null, state.editingWorkspace.customization.accentColorHex)
    }

    @Test
    fun gestureConfiguration_updatesWithUndoRedo() = runTest {
        viewModel.loadWorkspace("ws_editor_test")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.updateGestureSensitivities(
            zoom = 2.5f,
            pan = 1.5f,
            rotation = 0.8f,
            invertZoom = true,
            invertPanX = false,
            invertPanY = true,
            hapticsEnabled = false
        )

        var state = viewModel.uiState.value as WorkspaceEditorUiState.Success
        assertEquals(2.5f, state.editingWorkspace.creativeControlsConfig.zoomSensitivity, 0.01f)
        assertEquals(1.5f, state.editingWorkspace.creativeControlsConfig.panSensitivity, 0.01f)
        assertTrue(state.editingWorkspace.creativeControlsConfig.invertZoom)
        assertFalse(state.editingWorkspace.creativeControlsConfig.hapticsEnabled)
        assertTrue(state.isDirty)

        viewModel.undo()
        state = viewModel.uiState.value as WorkspaceEditorUiState.Success
        assertEquals(1.0f, state.editingWorkspace.creativeControlsConfig.zoomSensitivity, 0.01f)
        assertFalse(state.editingWorkspace.creativeControlsConfig.invertZoom)
        assertTrue(state.editingWorkspace.creativeControlsConfig.hapticsEnabled)
    }

    @Test
    fun duplicate_createsDuplicatedWorkspaceInRepository() = runTest {
        viewModel.loadWorkspace("ws_editor_test")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.duplicate()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value as WorkspaceEditorUiState.Success
        assertEquals("Photoshop Pro (Copy)", state.editingWorkspace.name)
        assertFalse(state.isDirty)
    }
}
