# Phase 4 — First Creative Workflow

Phase 4 delivers the primary end-to-end creative workflow enabling digital artists to control desktop software (e.g. Clip Studio Paint, Photoshop) via customizable Macro Pads, workspace selection, button customizations, and profile import/export.

## Implemented Features

### 1. Macro Pad
- Multi-grid sizing support: 2×2, 3×3, 4×4, and 5×5 layouts.
- Multi-page macro management: Add, rename, switch, and delete macro pages.
- Rich button capabilities:
  - Label, icon, and custom theme/color styling.
  - Primary actions: Undo, Redo, Save, Brush, Eraser, Fill, Selection, Transform, Copy, Paste, and Custom Shortcut (with key combinations).
  - Long-press secondary actions.
  - Repeat action mode for sustained operations (e.g., rapid undo/redo, brush resizing).
  - Enable/disable state and visibility toggling (hidden buttons maintain grid positioning).
- Direct command dispatch through the Phase 3 `OniCanvasCommandService` and `ConnectionRepository` TCP transport.
- Interactive UI feedback indicating command transmission and connection status.

### 2. Workspace Integration
- Macro Pad layout and button configuration are driven directly by the active `WorkspaceItem`.
- Switching workspaces immediately updates the active Macro Pad layout and pages.
- Active workspace persistence is managed through `WorkspaceRepository` and DataStore settings (`SettingsRepository`), avoiding dual sources of truth.

### 3. Custom Buttons
- Interactive button editing dialog and editor flow:
  - Label & icon customization.
  - Action picker mapping to standard artistic shortcuts or custom keystrokes.
  - Long-press action mapping.
  - Repeat behavior configuration.
  - Enable/disable and visibility controls.
- Real-time persistence via Room database (`WorkspaceDao` and `WorkspaceRepository`).
- Unidirectional data flow: ViewModels (`ControlsViewModel`, `WorkspaceEditorViewModel`) own business logic and state; Composables remain pure and presentational.

### 4. Profile & Workspace Import / Export
- Standard JSON export and import for workspace configurations and profiles.
- Strict schema validation and fallback handling for malformed or unsupported payloads.
- Backward compatibility with legacy serialized entities.
- Persistence integration with `ProfileRepository` and `WorkspaceRepository`.

## Testing & Verification
- Unit test suite covering:
  - Macro button action mapping and command dispatching (`OniCanvasCommandServiceTest`).
  - Reconnect retry scheduling and backoff policies (`ConnectionReconnectPolicyTest`).
  - Workspace entity mapping and JSON serialization/deserialization (`WorkspaceEntityMapperTest`).
  - Workspace customization and undo/redo stacks (`WorkspaceCustomizationTest`, `WorkspaceEditorViewModelTest`).
  - Macro Pad page/grid management and button editing in ViewModel (`ControlsViewModelTest`).
- Verification Commands:
  - `gradle :app:testDebugUnitTest`: Passed.
  - `gradle :app:assembleDebug`: Passed.
