package com.example.data.repository

import com.example.data.database.AppDatabase
import com.example.data.database.Workspace
import com.example.data.database.MacroButton
import com.example.data.database.RecentColor
import com.example.data.database.BrushFavorite
import kotlinx.coroutines.flow.Flow

class OniRepository(private val database: AppDatabase) {

    fun getWorkspaces(): Flow<List<Workspace>> {
        return database.workspaceDao().getAllWorkspaces()
    }

    suspend fun addWorkspace(workspace: Workspace) {
        database.workspaceDao().insertWorkspace(workspace)
    }

    fun getButtons(workspaceId: String): Flow<List<MacroButton>> {
        return database.macroButtonDao().getButtonsForWorkspace(workspaceId)
    }

    suspend fun addButton(button: MacroButton) {
        database.macroButtonDao().insertButton(button)
    }

    suspend fun deleteButton(id: Int) {
        database.macroButtonDao().deleteButtonById(id)
    }

    fun getRecentColors(): Flow<List<RecentColor>> {
        return database.recentColorDao().getRecentColors()
    }

    suspend fun addRecentColor(hex: String) {
        database.recentColorDao().insertColor(RecentColor(hex = hex))
        database.recentColorDao().pruneRecentColors()
    }

    fun getFavorites(workspaceId: String): Flow<List<BrushFavorite>> {
        return database.brushFavoriteDao().getFavoritesForWorkspace(workspaceId)
    }

    suspend fun addFavoriteBrush(brush: BrushFavorite) {
        database.brushFavoriteDao().insertFavorite(brush)
    }

    suspend fun removeFavoriteBrush(id: String, workspaceId: String) {
        database.brushFavoriteDao().deleteFavoriteById(id, workspaceId)
    }
}
