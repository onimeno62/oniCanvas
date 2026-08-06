# 15 - JSON API Reference

Version: 1.0
Status: Active

---

# Purpose

This document defines every JSON message used by OniCanvas.

It serves as the single source of truth for communication between:

• Android App
• Windows Companion

All protocol changes should be documented here before implementation.

---

# General Rules

Encoding

UTF-8

Format

JSON

Transport

Wi-Fi

USB (ADB)

Future

Bluetooth

---

# Common Fields

Every message contains

{
    "version": 1,
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "timestamp": 1732410200,
    "type": "command"
}

---

# Message Types

command

response

event

heartbeat

heartbeat_ack

error

discovery_request

discovery_response

authentication

plugin

---

# Command Messages

Execute an action.

Example

{
    "version":1,
    "type":"command",
    "action":"undo"
}

---

Redo

{
    "type":"command",
    "action":"redo"
}

---

Save

{
    "type":"command",
    "action":"save"
}

---

Zoom

{
    "type":"command",
    "action":"zoom",
    "payload":{
        "amount":1.15
    }
}

---

Rotate Canvas

{
    "type":"command",
    "action":"rotate_canvas",
    "payload":{
        "degrees":15
    }
}

---

Mouse Move

{
    "type":"command",
    "action":"mouse_move",
    "payload":{
        "deltaX":20,
        "deltaY":-10
    }
}

---

Mouse Click

{
    "type":"command",
    "action":"mouse_click",
    "payload":{
        "button":"left"
    }
}

Supported Buttons

left

right

middle

---

Mouse Scroll

{
    "type":"command",
    "action":"mouse_scroll",
    "payload":{
        "amount":4
    }
}

---

Keyboard Shortcut

{
    "type":"command",
    "action":"shortcut",
    "payload":{
        "keys":[
            "CTRL",
            "SHIFT",
            "S"
        ]
    }
}

---

Macro

{
    "type":"command",
    "action":"macro",
    "payload":{
        "macroId":"workspace_save"
    }
}

---

Workspace

{
    "type":"command",
    "action":"switch_workspace",
    "payload":{
        "workspaceId":"illustration"
    }
}

---

Theme

{
    "type":"command",
    "action":"set_theme",
    "payload":{
        "theme":"dark"
    }
}

---

# Response Messages

Successful

{
    "type":"response",
    "status":"success"
}

Failure

{
    "type":"response",
    "status":"failed",
    "reason":"Unknown macro"
}

Possible Status

success

failed

busy

unsupported

---

# Event Messages

Connected

{
    "type":"event",
    "event":"connected"
}

Disconnected

{
    "type":"event",
    "event":"disconnected"
}

Software Changed

{
    "type":"event",
    "event":"software_changed",
    "payload":{
        "software":"Clip Studio Paint"
    }
}

Workspace Loaded

{
    "type":"event",
    "event":"workspace_loaded"
}

Latency Updated

{
    "type":"event",
    "event":"latency",
    "payload":{
        "ms":18
    }
}

---

# Heartbeat

Android

{
    "type":"heartbeat"
}

Windows

{
    "type":"heartbeat_ack"
}

Interval

5 seconds

---

# Discovery

Android

{
    "type":"discovery_request"
}

Windows

{
    "type":"discovery_response",
    "payload":{
        "deviceName":"Studio-PC",
        "version":"1.0.0",
        "supportedConnections":[
            "wifi",
            "usb"
        ]
    }
}

---

# Error Messages

{
    "type":"error",
    "code":1004,
    "message":"Connection Lost"
}

---

# Error Codes

1000 Unknown Error

1001 Invalid JSON

1002 Missing Field

1003 Unsupported Command

1004 Connection Lost

1005 Authentication Failed

1006 Version Mismatch

1007 Invalid Payload

1008 Unsupported Software

1009 Timeout

---

# Authentication (Future)

Request

{
    "type":"authentication",
    "payload":{
        "pairingCode":"123456"
    }
}

Response

{
    "type":"response",
    "status":"success"
}

---

# Plugin Messages (Future)

{
    "type":"plugin",
    "plugin":"reference_viewer",
    "action":"open"
}

---

# Supported Actions

Keyboard

shortcut

Mouse

mouse_move

mouse_click

mouse_scroll

Canvas

zoom

rotate_canvas

fit_screen

reset_rotation

Layers

new_layer

delete_layer

duplicate_layer

merge_layer

Brush

brush

eraser

brush_size

opacity

Workspace

switch_workspace

save_workspace

Settings

set_theme

set_language

Connection

ping

disconnect

reconnect

---

# Versioning Rules

Minor versions

May add new fields.

Major versions

May introduce breaking changes.

Unknown fields must be ignored by older clients whenever possible.

---

# Validation Rules

Required fields

version

type

id

timestamp

Optional fields

payload

reason

event

action

Unknown message types should generate an error response rather than terminating the connection.

---

# Best Practices

- Keep payloads small.
- Send only required data.
- Prefer logical actions over device-specific keycodes.
- Maintain backward compatibility.
- Log protocol errors for debugging.

---

# Summary

The JSON API Reference defines every message exchanged between OniCanvas and the Windows Companion.

This document should always be updated whenever new commands, events, or protocol features are introduced.