# 17 - Complete Connection System

Version: 1.0
Status: Implemented in Phase 3

---

## Scope

The Android connection layer now owns the complete connection lifecycle between oniCanvas and the Windows Companion.

Implemented capabilities:

- Real TCP connection on port 8085
- Versioned newline-delimited JSON messages
- Legacy Phase 2 frame decoding during migration
- Local-network UDP discovery on port 8086
- Connection lifecycle state
- Heartbeat every 5 seconds
- Three missed heartbeats trigger connection loss
- Automatic reconnect with bounded retry delays
- Measured connection/heartbeat latency
- Typed command service
- Structured connection logging

---

## Connection Lifecycle

```text
Disconnected
    ↓
Searching
    ↓
Connecting
    ↓
Connected
    ↓
Reconnecting
    ↓
Connected

or

Error
```

User-initiated disconnect cancels all heartbeat and reconnect work.

Unexpected socket closure starts the reconnect policy for the last successful host.

---

## Reconnect Policy

The Android client performs a maximum of five attempts:

1. Immediately
2. After 2 seconds
3. After 5 seconds
4. After 10 seconds
5. After 10 seconds

After the fifth failure the connection enters `Error` and waits for explicit user action.

---

## Heartbeat

A heartbeat is sent every 5 seconds while connected.

Each heartbeat has a unique message ID. The companion responds with a `heartbeat_ack` carrying the same ID.

The elapsed time between the heartbeat and acknowledgement is used as the live latency measurement.

Three consecutive missed acknowledgements cause the Android client to close the socket and start automatic reconnection.

---

## Discovery

Android broadcasts a JSON `discovery_request` to `255.255.255.255:8086` and listens for `discovery_response` packets.

A response may contain:

```json
{
  "version": 1,
  "id": "...",
  "timestamp": 0,
  "type": "discovery_response",
  "payload": {
    "device": "DESKTOP-PC",
    "host": "DESKTOP-PC",
    "port": 8085
  }
}
```

Discovered endpoints are merged with paired hosts and become selectable by the Connection screen.

---

## Message Envelope

All newly transmitted protocol messages use:

```json
{
  "version": 1,
  "id": "unique-message-id",
  "timestamp": 0,
  "type": "command",
  "payload": {}
}
```

Supported message types:

- `command`
- `response`
- `event`
- `heartbeat`
- `heartbeat_ack`
- `discovery_request`
- `discovery_response`
- `error`

The decoder still accepts the short `CMD|...` / `EVT|...` Phase 2 frames so migration to the JSON contract can be performed without immediately breaking older peers.

---

## Command API

UI code should not construct protocol frames directly.

`OniCanvasCommandService` provides the application-facing API for commands such as:

- undo
- redo
- save
- zoom
- shortcut

The service creates typed protocol messages and the connection repository owns delivery.

---

## Architecture

```text
Compose UI
   ↓
ConnectionViewModel
   ↓
ConnectionRepository
   ├── ConnectionProbe
   ├── ConnectionDiscovery
   ├── ConnectionTransport
   └── OniCanvasCommandService
          ↓
       TCP socket
          ↓
Windows Companion
```

The repository remains the single source of truth for connection state. Transport and discovery do not expose UI state directly.
