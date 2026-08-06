# 11 - Windows Companion

Version: 1.0
Status: Planning

---

# Purpose

The Windows Companion is a lightweight desktop application that receives commands from OniCanvas and converts them into keyboard, mouse, and application-specific actions.

It acts as the bridge between the Android device and creative software.

The Android app never communicates directly with Clip Studio Paint or other desktop applications.

---

# Responsibilities

The Windows Companion is responsible for:

- Managing device connections
- Receiving commands
- Executing keyboard shortcuts
- Executing mouse input
- Detecting supported software
- Managing software profiles
- Returning status information
- Logging protocol events

---

# Supported Connections

## MVP

- Wi-Fi
- USB (ADB)

## Future

- Bluetooth

---

# Startup Flow

Application Starts

↓

Load Settings

↓

Start Connection Server

↓

Listen for Android Devices

↓

Accept Connection

↓

Begin Heartbeat

↓

Ready

---

# Main Components

Windows Companion

├── Connection Server

├── Protocol Parser

├── Command Dispatcher

├── Input Simulator

├── Application Detector

├── Profile Manager

├── Settings

└── Logging

---

# Connection Server

Responsibilities

- Listen for incoming connections
- Accept multiple connection attempts
- Validate protocol version
- Manage heartbeat
- Handle reconnects

---

# Protocol Parser

Responsibilities

- Parse JSON messages
- Validate required fields
- Handle malformed packets
- Dispatch commands

Protocol reference

08_Communication_Protocol.md

---

# Command Dispatcher

Receives validated commands and forwards them to the appropriate module.

Examples

Undo

↓

Keyboard Module

Mouse Move

↓

Mouse Module

Plugin Command

↓

Plugin Manager

---

# Input Simulator

Responsible for desktop input.

Supported actions

- Keyboard shortcuts
- Mouse movement
- Mouse clicks
- Mouse wheel
- Drag operations
- Macro execution

Future

- Pen input
- Pressure simulation

---

# Application Detector

Detects the active foreground application.

Initially supported

- Clip Studio Paint

Future

- Adobe Photoshop
- Krita
- Blender
- Paint Tool SAI
- Illustrator
- OBS Studio
- DaVinci Resolve

Future enhancement

Automatically switch profiles when the active application changes.

---

# Profile Manager

Stores software-specific mappings.

Example

Clip Studio Paint

Undo

CTRL + Z

Photoshop

Undo

CTRL + Z

Blender

Undo

CTRL + Z

Each application may define different shortcuts for the same logical action.

---

# Settings

General

- Start with Windows
- Minimize to tray
- Auto-connect
- Preferred connection

Connection

- Port
- Timeout
- Retry interval

Developer

- Protocol log
- Debug mode
- Latency monitor

---

# Logging

Log

- Connections
- Disconnections
- Commands
- Errors
- Protocol messages
- Latency
- Application changes

Log levels

- Debug
- Info
- Warning
- Error

---

# Heartbeat

Android sends a heartbeat every 5 seconds.

The companion responds with an acknowledgement.

If three consecutive heartbeats are missed:

- Mark connection as disconnected
- Stop processing commands
- Wait for reconnection

---

# Error Handling

Examples

- Invalid protocol version
- Unknown command
- Unsupported action
- Missing payload
- Lost connection

Errors should be reported back to the Android application whenever possible.

---

# Performance Goals

Connection latency

< 20 ms (USB)

< 50 ms (Wi-Fi)

CPU usage

Minimal while idle

Memory usage

Low and stable

The companion should remain responsive even during rapid command sequences.

---

# Security

MVP

- Local network only
- No cloud communication
- No external telemetry

Future

- TLS encryption
- Trusted devices
- Pairing code
- Optional authentication

---

# Plugin Architecture (Future)

Plugins may provide:

- Custom commands
- Software integrations
- New profiles
- Advanced automation

Plugins should not modify the core protocol.

---

# Auto-Update (Future)

The companion may support:

- Version checking
- Automatic updates
- Release notes
- Rollback support

---

# Development Principles

- Lightweight
- Reliable
- Fast startup
- Stable protocol
- Modular architecture
- Easy debugging
- Low latency

---

# Summary

The Windows Companion is the execution layer of OniCanvas.

Its responsibility is to translate standardized protocol messages into desktop input while remaining lightweight, reliable, and extensible for future creative software integrations.