# OniCanvas Architecture & Project Structure

This document outlines the software design, patterns, and technologies used to build the **OniCanvas** Android client.

---

## 1. Architectural Patterns
The application utilizes modern Android design patterns:
- **Architecture Pattern**: MVVM (Model-View-ViewModel) with Unidirectional Data Flow (UDF).
- **Core Frameworks**: Jetpack Compose (UI), Room (Database Cache), Jetpack DataStore (Settings Preferences), and Kotlin Coroutines/Flows (Concurrency).
- **Clean Separation**: Low-level network socket protocols and database actions are separated into clean Repository and Service layers.

---

## 2. Component Diagram & Data Flow

```
          [ Jetpack Compose Views (UI Screens) ]
                            │  ▲
           User Interaction │  │ State Flow / UI States
                            ▼  │
               [ ViewModel (OniViewModel) ]
                  │                  │
                  ▼                  ▼
     [ Repository Layer ]     [ Connection Service ]
              │                      │
       Local Persistence        TCP Sockets Protocol
      (Room DB / DataStore)   (Windows Companion Server)
```

- **UDF Pattern**: UI components observe state changes via Kotlin `StateFlow` streams. Users trigger events (such as pressing shortcuts or swiping gestures) which travel down to the `ViewModel` and are dispatched to the socket service.

---

## 3. Directory & Package Layout

The application's code resides in the `com.example` namespace:

```text
com.example/
│
├── data/
│   ├── database/       # Room Database (AppDatabase, entities, DAOs)
│   ├── datastore/      # Preferences DataStore (stores server IP, ports, preferences)
│   └── repository/     # OniRepository (bridges DB cache and workspace customization)
│
├── service/            # ConnectionService (TCP handshake, heartbeat, telemetry simulator)
│
└── ui/
    ├── components/     # Custom Glassmorphic, HSL color, and grid canvas widgets
    ├── screens/        # Compose Screen definitions (Home, Macros, Palette, Settings, Touchpad)
    ├── theme/          # Material Design 3 Styling (Colors, Typographies, Shapes, centralized Theme)
    └── viewmodel/      # OniViewModel & ViewModel Factory
```

---

## 4. Key Implementation Features

### 4.1 Room Database Persistence
The application stores configured macro buttons, preferred workspaces, and active color history locally. If the user disconnects, their visual workspace layout persists. See `/app/src/main/java/com/example/data/database` for entity models.

### 4.2 Connection Service
- **Real-Time Log Stream**: Maintains an internal circular log of up to 100 sent/received protocol messages, exposed as a Flow to the visual status dashboard.
- **Heartbeat Daemon**: Active telemetry monitors low-latency pings, simulating battery drain, CPU, and RAM load of the companion.

### 4.3 Custom Composable Canvases
- **Glass Components**: Beautiful semi-transparent frosted-glass surfaces utilizing Material 3 design colors and layout densities to mimic high-end desktop hardware.
- **HSL Palette Layout**: Complex rendering of HSL color spectrum grids on a canvas to provide professional color selection features.
