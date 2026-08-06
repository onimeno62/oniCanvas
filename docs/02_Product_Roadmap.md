# 02 – Product Roadmap

> Version: 1.0
> Status: Planning

---

# Purpose

This roadmap defines the planned development path for OniCanvas.

The project will be built incrementally, ensuring that every release is stable, functional, and provides value to users.

Core philosophy:

- Build small
- Release often
- Never break existing functionality
- Keep the architecture modular
- Make every feature reusable

---

# Development Phases

## Phase 1 — Foundation (v0.1)

### Goal

Create a solid Android application with a scalable architecture.

### Features

- Android project setup
- Material 3
- Light Theme
- Dark Theme
- Navigation
- Splash Screen
- Dashboard
- Settings
- About Screen
- DataStore
- Logging

### Deliverable

A production-quality Android application shell.

---

# Phase 2 — Connectivity (v0.2)

### Goal

Enable communication between Android and Windows.

### Features

- Windows Companion prototype
- Wi-Fi communication
- USB (ADB) communication
- Connection manager
- Device discovery
- Connection status
- Automatic reconnect

### Deliverable

Android can connect to the Windows Companion and exchange messages.

---

# Phase 3 — Command System (v0.3)

### Goal

Create a standardized communication protocol.

### Features

- JSON protocol
- Command dispatcher
- Event system
- Action routing

Example

```json
{
  "type": "command",
  "action": "undo"
}
```

### Deliverable

Android can send commands that the Windows Companion executes.

---

# Phase 4 — First Creative Workflow (v0.4)

### Goal

Deliver the first useful drawing workflow.

### Features

- Macro Pad
- Workspace selection
- Custom buttons
- Import/Export profiles

### Deliverable

Artists can control Clip Studio Paint with customizable shortcut buttons.

---

# Phase 5 — Creative Controls (v0.5)

### Features

- Gesture Pad
- Zoom Controller
- Canvas Controls
- Haptic feedback

---

# Phase 6 — Productivity (v0.6)

### Features

- Radial Menu
- Touchpad Mode
- Layer Controls
- Brush Favorites

---

# Phase 7 — Customization (v0.7)

### Features

- Workspace Editor
- Drag-and-drop layout editing
- Icon Library
- Theme customization

---

# Phase 8 — Color Workflow (v0.8)

### Features

- Color Wheel
- HSV
- RGB
- HEX
- Recent Colors
- Palette Manager

---

# Phase 9 — Performance & Tablet (v0.9)

### Features

- Tablet UI
- Landscape layouts
- Accessibility
- Performance optimization
- Battery optimization

---

# Version 1.0

## Release Goals

- Stable Android application
- Stable Windows Companion
- Complete documentation
- Professional UI
- Reliable low-latency communication
- Full customization
- Profile management

---

# Future Roadmap

## Planned Features

- Voice commands
- Reference image viewer
- Drawing timer
- Plugin architecture
- Context-aware controls
- Performance monitor
- Community profile sharing
- Cloud backup
- macOS companion
- Linux companion

---

# Development Principles

Every release must:

- Be fully functional
- Maintain backward compatibility where possible
- Improve user experience
- Keep code modular
- Include documentation updates
- Be testable