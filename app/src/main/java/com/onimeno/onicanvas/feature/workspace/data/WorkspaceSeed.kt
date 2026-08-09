package com.onimeno.onicanvas.feature.workspace.data

import com.onimeno.onicanvas.feature.workspace.state.ControlModule
import com.onimeno.onicanvas.feature.workspace.state.WorkspaceItem

internal fun defaultWorkspaces(): List<WorkspaceItem> = listOf(
    WorkspaceItem("illust_layout", "Illustration Master", "Optimized for painting, blending, and detailing brush settings", "Clip Studio Paint", 16, "brush", true, "10 mins ago", listOf(ControlModule.BRUSH_CONTROLS, ControlModule.MACRO_PAD, ControlModule.RADIAL_MENU, ControlModule.GESTURE_PAD)),
    WorkspaceItem("manga_layout", "Manga Page Setup", "Fast paneling, line-art macros, and halftone layers", "Clip Studio Paint", 12, "book", true, "2 hours ago", listOf(ControlModule.MACRO_PAD, ControlModule.SHORTCUT_GRID, ControlModule.GESTURE_PAD)),
    WorkspaceItem("sculpt_3d", "3D Sculpt Companion", "Camera rotation, brush sizing, and viewport settings", "Blender", 20, "cube", false, "Yesterday", listOf(ControlModule.RADIAL_MENU, ControlModule.MACRO_PAD, ControlModule.GESTURE_PAD, ControlModule.BRUSH_CONTROLS)),
    WorkspaceItem("photoshop_concept", "Concept Speed-painter", "Quick layer opacity toggles, custom lasso, and brush flow presets", "Photoshop", 14, "palette", false, "3 days ago", listOf(ControlModule.BRUSH_CONTROLS, ControlModule.SHORTCUT_GRID, ControlModule.MACRO_PAD)),
    WorkspaceItem("krita_sketch", "Krita Sketcher", "Minimalist layout mapping core shortcuts, canvas stabilizer, and zoom wheel", "Krita", 8, "edit", true, "Last week", listOf(ControlModule.MACRO_PAD, ControlModule.GESTURE_PAD))
)
