# Phase 7 — Customization

Phase 7 completes the workspace customization foundation on top of the existing Workspace Editor and persistence stack.

## Implemented

- Workspace-level customization model with persisted theme key.
- Workspace icon library and macro action library.
- Customization-aware workspace entity mapping.
- Backward-compatible decoding of legacy `creativeControlsJson` records.
- Workspace Editor commands for:
  - workspace icon selection
  - workspace theme selection
  - grid resizing from 2×2 through 6×6
  - macro button insertion/removal
  - macro button reordering
  - macro button label/icon/action updates
- Undo/redo coverage for customization mutations, including destructive grid resizing.
- Stateless `WorkspaceCustomizationPanel` UI component.
- Unit tests covering persistence, legacy records, grid capacity, and icon library availability.

## Persistence strategy

No Room schema migration is required. Existing `creativeControlsJson` storage now uses a small serialized payload containing the existing creative-controls configuration plus workspace customization. Older rows that contain only `CreativeControlsConfig` continue to decode through the legacy fallback path.

## UI architecture

`WorkspaceCustomizationPanel` is intentionally stateless. It receives a `WorkspaceItem` and event callbacks. All mutations belong to `WorkspaceEditorViewModel`, preserving the project's Compose architecture rules.

The workspace theme is a workspace preference, not a replacement for the global Material 3 theme. This keeps application-wide design tokens centralized while allowing each workspace to carry a visual identity for future connected-device rendering.
