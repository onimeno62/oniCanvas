package com.onimeno.onicanvas.feature.controls.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.onimeno.onicanvas.feature.connection.data.ConnectionRepository
import com.onimeno.onicanvas.feature.controls.state.ControlsUiState
import com.onimeno.onicanvas.feature.settings.data.SettingsRepository
import com.onimeno.onicanvas.feature.workspace.data.FakeWorkspaceDao
import com.onimeno.onicanvas.feature.workspace.data.WorkspaceRepository
import com.onimeno.onicanvas.feature.workspace.data.toEntity
import com.onimeno.onicanvas.feature.workspace.state.MacroAction
import com.onimeno.onicanvas.feature.workspace.state.MacroButton
import com.onimeno.onicanvas.feature.workspace.state.MacroPage
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

private class FakeDataStore : DataStore<Preferences> {
    private val state = MutableStateFlow<Preferences>(emptyPreferences())
    override val data: Flow<Preferences> = state
    override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences {
        val updated = transform(state.value)
        state.value = updated
        return updated
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ControlsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeDao: FakeWorkspaceDao
    private lateinit var workspaceRepository: WorkspaceRepository
    private lateinit var connectionRepository: ConnectionRepository
    private lateinit var viewModel: ControlsViewModel

    private val testWorkspace = WorkspaceItem(
        id = "controls_ws_test",
        name = "Clip Studio Paint Master",
        description = "Illustration controls",
        targetApp = "Clip Studio Paint",
        buttonCount = 4,
        iconName = "brush",
        lastUsed = "Just now",
        gridSize = 2,
        macroPages = listOf(
            MacroPage(
                id = "page_1",
                name = "Main Page",
                orderIndex = 0,
                buttons = listOf(
                    MacroButton("btn_1", 0, "Undo", "undo", MacroAction.Undo),
                    MacroButton("btn_2", 1, "Redo", "redo", MacroAction.Redo),
                    MacroButton("btn_3", 2, "Brush", "brush", MacroAction.Brush),
                    MacroButton("btn_4", 3, "Eraser", "eraser", MacroAction.Eraser)
                )
            )
        )
    )

    @Before
    fun setUp() = runTest {
        Dispatchers.setMain(testDispatcher)
        fakeDao = FakeWorkspaceDao()
        fakeDao.upsert(testWorkspace.toEntity())

        val settingsRepo = SettingsRepository(FakeDataStore())
        workspaceRepository = WorkspaceRepository(fakeDao, settingsRepo)
        connectionRepository = ConnectionRepository()
        viewModel = ControlsViewModel(workspaceRepository, connectionRepository)
    }

    @After
    fun tearDown() {
        connectionRepository.close()
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_initializesWithActiveWorkspace() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value as ControlsUiState.Success
        assertEquals("controls_ws_test", state.activeWorkspace.id)
        assertEquals("Clip Studio Paint Master", state.activeWorkspace.name)
        assertEquals(2, state.activeWorkspace.gridSize)
        assertEquals("page_1", state.activePageId)
    }

    @Test
    fun updateGridSize_updatesCapacityAndPositions() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.updateGridSize(3)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value as ControlsUiState.Success
        assertEquals(3, state.activeWorkspace.gridSize)
        val activePage = state.activeWorkspace.macroPages.first()
        assertEquals(9, activePage.buttons.size)
    }

    @Test
    fun addPage_and_deletePage_managesPages() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.addPage("Inking Page")
        testDispatcher.scheduler.advanceUntilIdle()

        var state = viewModel.uiState.value as ControlsUiState.Success
        assertEquals(2, state.activeWorkspace.macroPages.size)
        assertEquals("Inking Page", state.activeWorkspace.macroPages[1].name)

        val newPageId = state.activeWorkspace.macroPages[1].id
        viewModel.deletePage(newPageId)
        testDispatcher.scheduler.advanceUntilIdle()

        state = viewModel.uiState.value as ControlsUiState.Success
        assertEquals(1, state.activeWorkspace.macroPages.size)
    }

    @Test
    fun renamePage_updatesPageName() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.renamePage("page_1", "Quick Actions")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value as ControlsUiState.Success
        assertEquals("Quick Actions", state.activeWorkspace.macroPages.first().name)
    }

    @Test
    fun updateButton_modifiesButtonProperties() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        testDispatcher.scheduler.advanceUntilIdle()

        val updatedButton = MacroButton(
            id = "btn_1",
            position = 0,
            label = "Save Project",
            iconName = "save",
            action = MacroAction.Save,
            longPressAction = MacroAction.Redo,
            repeatEnabled = true,
            enabled = true,
            hidden = false
        )

        viewModel.updateButton("page_1", updatedButton)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value as ControlsUiState.Success
        val btn = state.activeWorkspace.macroPages.first().buttons.first()
        assertEquals("Save Project", btn.label)
        assertEquals("save", btn.iconName)
        assertEquals(MacroAction.Save, btn.action)
        assertEquals(MacroAction.Redo, btn.longPressAction)
        assertTrue(btn.repeatEnabled)
    }
}

