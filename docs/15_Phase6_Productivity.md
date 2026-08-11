# Phase 6 — Productivity (v0.6)

Status: Implemented on `agent/phase6-productivity`.

## Scope

Phase 6 adds the productivity control surface described by the roadmap:

- Radial Menu
- Touchpad Mode
- Layer Controls
- Brush Favorites

## Architecture

`ProductivityScreen` is a state-driven Compose surface. `ProductivityViewModel` owns command routing and connection gating. `OniCanvasCommandService` is the only application-facing command API used by the feature.

Commands are disabled while the connection is not in the success state.

## Radial Menu

The radial surface exposes eight high-frequency actions:

- Undo
- Redo
- Brush
- Eraser
- Save
- Transform
- Selection
- Copy

## Touchpad Mode

The touchpad forwards relative pointer movement through `mouse_move`, supports primary/secondary click gestures, and exposes explicit vertical scroll controls through `mouse_scroll`. A local sensitivity multiplier is applied before transmission.

## Layer Controls

The UI exposes direct commands for new, duplicate, merge, lock, mask, and opacity adjustments. The Windows Companion remains responsible for applying those operations to the active creative application.

## Brush Favorites

Eight preset slots provide one-tap brush selection. The selected slot is sent as a `brush_preset` command so the companion can map the index to the user's configured CSP brush.

## Protocol additions

Phase 6 introduces these command actions:

- `mouse_move`: `{ "x": number, "y": number }`
- `mouse_button`: `{ "button": string, "pressed": boolean }`
- `mouse_scroll`: `{ "x": number, "y": number }`
- `brush_preset`: `{ "index": number }`
- `layer_new`
- `layer_duplicate`
- `layer_merge`
- `layer_lock`
- `layer_mask`
- `layer_opacity_down`
- `layer_opacity_up`

The command envelope remains the existing versioned JSON protocol; unsupported commands are safely rejected by the companion.

## Safety and compatibility

The Android client does not assume that a productivity command succeeded. It sends through the existing connection stack, which owns transport failures, reconnect behavior, and connection state. Existing Phase 1–5 features remain on their existing routes.
