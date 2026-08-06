package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Dao
interface WorkspaceDao {
    @Query("SELECT * FROM workspaces")
    fun getAllWorkspaces(): Flow<List<Workspace>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkspace(workspace: Workspace)

    @Query("DELETE FROM workspaces WHERE id = :id")
    suspend fun deleteWorkspaceById(id: String)
}

@Dao
interface MacroButtonDao {
    @Query("SELECT * FROM macro_buttons WHERE workspaceId = :workspaceId ORDER BY page, row, `column` ASC")
    fun getButtonsForWorkspace(workspaceId: String): Flow<List<MacroButton>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertButton(button: MacroButton)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertButtons(buttons: List<MacroButton>)

    @Query("DELETE FROM macro_buttons WHERE id = :id")
    suspend fun deleteButtonById(id: Int)
}

@Dao
interface RecentColorDao {
    @Query("SELECT * FROM recent_colors ORDER BY timestamp DESC LIMIT 20")
    fun getRecentColors(): Flow<List<RecentColor>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertColor(color: RecentColor)

    @Query("DELETE FROM recent_colors WHERE id NOT IN (SELECT id FROM recent_colors ORDER BY timestamp DESC LIMIT 20)")
    suspend fun pruneRecentColors()
}

@Dao
interface BrushFavoriteDao {
    @Query("SELECT * FROM brush_favorites WHERE workspaceId = :workspaceId")
    fun getFavoritesForWorkspace(workspaceId: String): Flow<List<BrushFavorite>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: BrushFavorite)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorites(favorites: List<BrushFavorite>)

    @Query("DELETE FROM brush_favorites WHERE id = :id AND workspaceId = :workspaceId")
    suspend fun deleteFavoriteById(id: String, workspaceId: String)
}

@Database(
    entities = [Workspace::class, MacroButton::class, RecentColor::class, BrushFavorite::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workspaceDao(): WorkspaceDao
    abstract fun macroButtonDao(): MacroButtonDao
    abstract fun recentColorDao(): RecentColorDao
    abstract fun brushFavoriteDao(): BrushFavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "onicanvas.db"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDb(database)
                }
            }
        }

        suspend fun populateDb(db: AppDatabase) {
            val workspaces = listOf(
                Workspace("illustration", "Illustration", "The ultimate setup for digital drawing and painting.", "Brush", isDefault = true),
                Workspace("manga", "Manga", "Speed-focused paneling, speech balloons, and screentones.", "Dashboard"),
                Workspace("animation", "Animation", "Keyframes, timeline control, and onion skinning.", "Movie"),
                Workspace("coloring", "Coloring", "Palettes, bucket fills, and gradient tools.", "Palette")
            )

            for (w in workspaces) {
                db.workspaceDao().insertWorkspace(w)
            }

            // Seed illustration buttons
            val illButtons = listOf(
                MacroButton(workspaceId = "illustration", label = "Undo", iconName = "Undo", colorHex = "#6366F1", actionShortcut = "Ctrl+Z", page = 0, row = 0, column = 0),
                MacroButton(workspaceId = "illustration", label = "Redo", iconName = "Redo", colorHex = "#8B5CF6", actionShortcut = "Ctrl+Y", page = 0, row = 0, column = 1),
                MacroButton(workspaceId = "illustration", label = "Save", iconName = "Save", colorHex = "#06B6D4", actionShortcut = "Ctrl+S", page = 0, row = 0, column = 2),

                MacroButton(workspaceId = "illustration", label = "Brush", iconName = "Brush", colorHex = "#6366F1", actionShortcut = "B", page = 0, row = 1, column = 0),
                MacroButton(workspaceId = "illustration", label = "Eraser", iconName = "Delete", colorHex = "#8B5CF6", actionShortcut = "E", page = 0, row = 1, column = 1),
                MacroButton(workspaceId = "illustration", label = "Color Picker", iconName = "Colorize", colorHex = "#06B6D4", actionShortcut = "I", page = 0, row = 1, column = 2),

                MacroButton(workspaceId = "illustration", label = "Flip H", iconName = "Flip", colorHex = "#6366F1", actionShortcut = "Alt+H", page = 0, row = 2, column = 0),
                MacroButton(workspaceId = "illustration", label = "Rotate", iconName = "Refresh", colorHex = "#8B5CF6", actionShortcut = "R", page = 0, row = 2, column = 1),
                MacroButton(workspaceId = "illustration", label = "Hand", iconName = "PanTool", colorHex = "#06B6D4", actionShortcut = "Space", page = 0, row = 2, column = 2),

                MacroButton(workspaceId = "illustration", label = "Zoom In", iconName = "ZoomIn", colorHex = "#6366F1", actionShortcut = "Ctrl++", page = 0, row = 3, column = 0),
                MacroButton(workspaceId = "illustration", label = "Zoom Out", iconName = "ZoomOut", colorHex = "#8B5CF6", actionShortcut = "Ctrl+-", page = 0, row = 3, column = 1),
                MacroButton(workspaceId = "illustration", label = "Export", iconName = "Share", colorHex = "#06B6D4", actionShortcut = "Ctrl+Alt+E", page = 0, row = 3, column = 2)
            )
            db.macroButtonDao().insertButtons(illButtons)

            // Seed coloring buttons
            val colButtons = listOf(
                MacroButton(workspaceId = "coloring", label = "Undo", iconName = "Undo", colorHex = "#8B5CF6", actionShortcut = "Ctrl+Z", row = 0, column = 0),
                MacroButton(workspaceId = "coloring", label = "Redo", iconName = "Redo", colorHex = "#8B5CF6", actionShortcut = "Ctrl+Y", row = 0, column = 1),
                MacroButton(workspaceId = "coloring", label = "Save", iconName = "Save", colorHex = "#06B6D4", actionShortcut = "Ctrl+S", row = 0, column = 2),
                MacroButton(workspaceId = "coloring", label = "Fill", iconName = "FormatPaint", colorHex = "#6366F1", actionShortcut = "G", row = 1, column = 0),
                MacroButton(workspaceId = "coloring", label = "Lasso", iconName = "Category", colorHex = "#6366F1", actionShortcut = "L", row = 1, column = 1),
                MacroButton(workspaceId = "coloring", label = "Select", iconName = "CropSquare", colorHex = "#6366F1", actionShortcut = "M", row = 1, column = 2)
            )
            db.macroButtonDao().insertButtons(colButtons)

            // Seed standard favorite brushes
            val defaultBrushes = listOf(
                BrushFavorite("1", "illustration", "Pencil", "Real Pencil", "N"),
                BrushFavorite("2", "illustration", "G-Pen", "Mapping pen", "P"),
                BrushFavorite("3", "illustration", "Marker", "Milli pen", "M"),
                BrushFavorite("4", "illustration", "Watercolor", "Opaque watercolor", "W"),
                BrushFavorite("5", "illustration", "Airbrush", "Soft airbrush", "A"),
                BrushFavorite("6", "illustration", "Texture Brush", "Rough pattern", "T")
            )
            db.brushFavoriteDao().insertFavorites(defaultBrushes)

            // Seed default colors
            val defaultColors = listOf("#6366F1", "#8B5CF6", "#06B6D4", "#F59E0B", "#10B981", "#EF4444")
            for (color in defaultColors) {
                db.recentColorDao().insertColor(RecentColor(hex = color))
            }
        }
    }
}
