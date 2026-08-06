# OniCanvas User & Setup Guide

Welcome to **OniCanvas**—the ultimate wireless tactile companion controller for Clip Studio Paint. This guide explains how to install the desktop host software, configure your Android app, and optimize your workspace for digital illustration.

---

## 1. Prerequisites
Before using OniCanvas, ensure you have:
1. An Android tablet or phone running **Android 8.0+** connected to your local network.
2. A Windows PC running **Clip Studio Paint Pro or EX**.
3. Both your PC and Android device connected to the **same Wi-Fi router** (or bridged via USB tethering).

---

## 2. Desktop Companion Setup (Windows)

1. **Download the Server**: Download the companion desktop host utility from the OniCanvas download portal.
2. **Install & Run**: Extract the package and run `OniCanvasServer.exe`. 
3. **Firewall Access**: If prompted by Windows Defender Firewall, allow the program access to **Private Networks**.
4. **Identify IP & Port**:
   - The server UI will show your PC's active IP address (e.g., `192.168.1.100`).
   - The default communication port is `8000`.

---

## 3. Configuring the Android Companion App

1. Launch **OniCanvas** on your Android device.
2. Go to the **Settings** tab (the gear icon on the far right of the navigation bar).
3. Under **Connection Status**:
   - Enter your PC's IP address into the **Windows Companion IP** field.
   - Enter `8000` (or your custom port) in the **Communication Port** field.
4. Click **Save Profile**, then tap the primary **Connect** button.
5. Once connected:
   - The connection status dot in the Home tab will turn **Green (Connected)**.
   - The telemetry panel will display live latency metric stream (e.g., `8ms`).

---

## 4. Feature Explanations & Gestures

### 4.1 Home Screen Layout
- **Current Workspace Profile**: Toggle between predefined templates (Illustration, Sketching, Comic, and Vector) depending on your workflow.
- **Favorite Actions Matrix**: Rapid one-tap shortcut buttons for the most common operations:
  - **Undo**: Trigger Ctrl+Z
  - **Redo**: Trigger Ctrl+Y
  - **Save**: Save your drawing session immediately
  - **Brush / Eraser**: Toggle drawing tools
- **Live Metrics**: Monitor your client performance statistics (CPU, RAM, Battery, Latency).

### 4.2 Gesture Touchpad
- **Single Tap**: Simulates left-click.
- **Double Tap**: Fits the active canvas to screen zoom.
- **Long Press**: Simulates right-click (toggles context menus in CSP).
- **Two-Finger Zoom/Pinch**: Adjusts Clip Studio Paint zoom levels dynamically.
- **Swipe Gestures**:
  - Swipe **Up**: Trigger Undo
  - Swipe **Down**: Trigger Redo

### 4.3 Tactile HSL Color Wheel
- Located inside the **Palette** tab.
- Slide your finger around the circular HSV spectrum ring to dynamically change your active brush color in Clip Studio Paint.
- Recent colors are automatically cached in the local SQLite database for quick reuse.
- Access **Radial Menu**, **Zoom Wheel**, **Brushes**, **Layers**, and **Canvas** options directly from the tab bar.
