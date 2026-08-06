# OniCanvas Companion Protocol Specification (v1.0.0)

This document describes the JSON-based TCP/UDP socket messaging protocol used to sync the **OniCanvas** Android companion client with the **Windows Clip Studio Paint (CSP) Companion Server**.

---

## 1. Network Architecture
- **Protocol**: JSON-serialized UTF-8 packets sent over TCP sockets.
- **Default Port**: `8000` (configurable in app settings).
- **Discovery**: Optional UDP broadcast on local network segment for automatic device pairing.

---

## 2. Session Lifecycle Packets

### 2.1 Connection & Discovery Request
Sent by the Android client to initiate handshaking and verify server compatibility.
```json
{
  "version": 1,
  "type": "discovery_request"
}
```

### 2.2 Discovery Response
Returned by the Windows server to acknowledge the client, declaring its environment info.
```json
{
  "version": 1,
  "type": "discovery_response",
  "payload": {
    "deviceName": "Studio-PC",
    "version": "1.0.0",
    "supportedConnections": ["wifi", "usb"]
  }
}
```

### 2.3 Status Events
Asynchronous status notification packets broadcasted during connection changes.
- **Connected**: `{"type":"event","event":"connected"}`
- **Disconnected**: `{"type":"event","event":"disconnected"}`

### 2.4 Heartbeat Check
Periodic packet sent every 5 seconds to ensure low latency and detect sudden disconnects.
- **Client ping**: `{"type":"heartbeat"}`
- **Server pong**: `{"type":"heartbeat_ack"}`

---

## 3. Creative Tool Command Messages

All standard remote commands conform to a uniform envelope structured as:
```json
{
  "version": 1,
  "id": "abc123_random_id",
  "timestamp": 1722891234,
  "type": "command",
  "action": "<action_name>",
  "payload": {}
}
```

### 3.1 Trigger Keyboard Shortcuts
Triggers keypresses inside Clip Studio Paint (e.g., Undo, Brush, Eraser).
- **Action**: `"shortcut"`
- **Example Payload**:
```json
{
  "version": 1,
  "id": "e98fa2",
  "timestamp": 1722891280,
  "type": "command",
  "action": "shortcut",
  "payload": {
    "keys": ["Ctrl+Z"]
  }
}
```

### 3.2 Change Workspace Profile
Informs the server when the active workspace (Illustration, Animation, Comics) changes on the mobile layout.
- **Action**: `"switch_workspace"`
- **Example Payload**:
```json
{
  "version": 1,
  "id": "fbc321",
  "timestamp": 1722891300,
  "type": "command",
  "action": "switch_workspace",
  "payload": {
    "workspaceId": "illustration"
  }
}
```

### 3.3 Active Color Sync
Transfers primary paint color changes made in the circular HSL wheel directly to CSP.
- **Action**: `"color_picker"`
- **Example Payload**:
```json
{
  "version": 1,
  "id": "c138da",
  "timestamp": 1722891320,
  "type": "command",
  "action": "color_picker",
  "payload": {
    "hex": "#6366F1"
  }
}
```

### 3.4 Sync Preferred Theme
Propagates UI visual theme updates to keep the companion app and desktop app visually aligned.
- **Action**: `"set_theme"`
- **Example Payload**:
```json
{
  "version": 1,
  "id": "d135ea",
  "timestamp": 1722891340,
  "type": "command",
  "action": "set_theme",
  "payload": {
    "theme": "dark"
  }
}
```

---

## 4. Canvas Manipulation & Touchpad Operations

### 4.1 Mouse Pointer Tracking
Simulates drag-based cursor movement directly from the digital touchpad trackpad.
- **Action**: `"Mouse Move"`
- **Payload**: `"MoveCursor"`

### 4.2 Click Actions
Simulates primary left/right physical button mouse inputs.
- **Action**: `"Click"`
- **Payload**: `"LeftClick"` or `"RightClick"`

### 4.3 Canvas Gestures
Supports complex zoom manipulations and gesture-driven actions:
- **Undo Swipe**: Action `"Undo"`, payload keys `["Ctrl+Z"]`
- **Redo Swipe**: Action `"Redo"`, payload keys `["Ctrl+Y"]`
- **Zoom Adjustments**: Action `"Zoom"`, payload keys `["Ctrl+[125%]"]`
