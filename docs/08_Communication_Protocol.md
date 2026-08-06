# 08 - Communication Protocol

Version: 1.0
Status: Planning

---

# Purpose

This document defines the communication protocol between the OniCanvas Android application and the Windows Companion.

Every message exchanged between devices must follow this protocol.

The protocol is designed to be

- Lightweight
- Fast
- Versioned
- Extensible
- Backward compatible

---

# Supported Connections

Current

• Wi-Fi

• USB (ADB)

Future

• Bluetooth

---

# Protocol Format

All messages use UTF-8 JSON.

Example

{
    "version": 1,
    "type": "command",
    "action": "undo"
}

---

# Protocol Version

Current Version

1

Every request contains

version

Future versions must remain backward compatible whenever possible.

---

# Message Structure

Every message contains

version

id

timestamp

type

payload

Example

{
    "version": 1,
    "id": "3d92fa",
    "timestamp": 1732410200,
    "type": "command",
    "payload": {}
}

---

# Message Types

command

response

event

heartbeat

error

discovery

authentication

future

plugin

---

# Command Messages

Sent from Android.

Purpose

Execute actions.

Example

{
    "type": "command",
    "action": "undo"
}

Example

{
    "type": "command",
    "action": "redo"
}

Example

{
    "type": "command",
    "action": "save"
}

---

# Zoom Command

{
    "type":"command",
    "action":"zoom",
    "payload":{
        "amount":1.20
    }
}

---

# Mouse Command

{
    "type":"command",
    "action":"mouse_move",
    "payload":{
        "x":15,
        "y":-8
    }
}

---

# Keyboard Shortcut

{
    "type":"command",
    "action":"shortcut",
    "payload":{
        "keys":[
            "CTRL",
            "Z"
        ]
    }
}

---

# Macro Command

{
    "type":"command",
    "action":"macro",
    "payload":{
        "macroId":"undo_save"
    }
}

---

# Response Messages

Sent by Windows Companion.

Example

{
    "type":"response",
    "status":"success"
}

Possible Status

success

failed

unsupported

busy

---

# Event Messages

Used to notify Android.

Examples

Connected

Disconnected

Workspace Loaded

Software Changed

Profile Imported

Update Available

Latency Updated

---

Example

{
    "type":"event",
    "event":"connected"
}

---

# Error Messages

Example

{
    "type":"error",
    "code":1003,
    "message":"Unsupported command"
}

---

# Error Codes

1000

Unknown Error

1001

Invalid JSON

1002

Missing Field

1003

Unsupported Command

1004

Connection Lost

1005

Authentication Failed

1006

Version Mismatch

---

# Heartbeat

Purpose

Detect broken connections.

Android sends

heartbeat

every

5 seconds.

Windows replies

heartbeat_ack

If three consecutive heartbeats fail,

mark the connection as disconnected and begin automatic reconnection.

---

# Device Discovery

Android broadcasts

discovery_request

Windows responds

discovery_response

Example

{
    "type":"discovery_response",
    "device":"DESKTOP-PC",
    "version":"1.0.0"
}

---

# Authentication

MVP

No authentication.

Future

Pairing code

Device approval

Encrypted communication

Trusted devices

---

# Version Compatibility

Android

1.0

↓

Windows

1.0

Compatible

Android

1.1

↓

Windows

1.0

Compatible if no breaking protocol changes exist.

Major version differences require user notification.

---

# Latency Reporting

Windows periodically sends

{
    "type":"event",
    "event":"latency",
    "payload":{
        "latency":14
    }
}

Measured in milliseconds.

---

# Connection States

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

---

# Retry Strategy

Attempt

1

Immediately

Attempt

2

After 2 seconds

Attempt

3

After 5 seconds

Attempt

4

After 10 seconds

Maximum

5 attempts

---

# Security

MVP

Local network only.

Future

TLS

Device pairing

Message signing

Encrypted payloads

---

# Logging

Both Android and Windows Companion should log

Connection

Disconnection

Commands

Errors

Latency

Reconnects

Logs should be available in Developer Mode.

---

# Future Extensions

Plugin Commands

Cloud Messages

Remote Updates

Companion Notifications

Voice Commands

Context-Aware Events

---

# Design Principles

- JSON only
- Human readable
- Low latency
- Stateless messages
- Versioned protocol
- Easy to debug
- Backward compatible

---

# Summary

The Communication Protocol serves as the single contract between the Android application and the Windows Companion.

Changes to this document should be made carefully, as both applications depend on it.