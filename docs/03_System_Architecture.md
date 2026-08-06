# 03 - System Architecture

Version: 1.0
Status: Planning

---

# Purpose

This document describes the overall architecture of OniCanvas, including how the Android application communicates with the Windows Companion and supported creative software.

The architecture is designed to be modular, scalable, and software-agnostic so additional creative applications can be supported in future releases.

---

# High-Level Architecture

+--------------------+
|   Android Device   |
|     OniCanvas      |
+----------+---------+
           |
           | Wi-Fi / USB / Bluetooth
           |
+----------v---------+
| Windows Companion  |
+----------+---------+
           |
           | Keyboard / Mouse / Macros
           |
+----------v---------+
| Creative Software  |
| Clip Studio Paint  |
| Photoshop          |
| Krita              |
| Blender            |
+--------------------+

---

# Components

## Android Application

Responsibilities

- User Interface
- Workspace Management
- Macro Editor
- Gesture Recognition
- Touchpad
- Color Picker
- Profile Management
- Settings
- Connection Management

The Android application never communicates directly with Clip Studio Paint.

Instead, it sends commands to the Windows Companion.

---

## Windows Companion

Responsibilities

- Receive commands
- Parse JSON messages
- Execute keyboard shortcuts
- Execute mouse events
- Handle macros
- Detect active software
- Manage plugins
- Return status information

The Windows Companion acts as the bridge between Android and desktop software.

---

## Creative Software

Initially supported

- Clip Studio Paint

Future

- Photoshop
- Krita
- Paint Tool SAI
- Blender
- Illustrator
- OBS Studio
- DaVinci Resolve

---

# Communication Layers

Android

↓

Connection Layer

↓

Message Protocol

↓

Windows Companion

↓

Input Execution

↓

Creative Software

---

# Communication Types

## USB (ADB)

Advantages

- Lowest latency
- Most reliable
- No Wi-Fi required

Recommended for professional artists.

---

## Wi-Fi

Advantages

- Wireless
- Easy setup
- Fast enough for daily use

Recommended for most users.

---

## Bluetooth

Advantages

- Low power
- Simple pairing

Limitations

- Lower bandwidth
- Higher latency

Used mainly for lightweight commands.

---

# Message Flow

Example

User presses Undo

↓

Android creates command

↓

JSON message

↓

Windows Companion receives message

↓

Shortcut executed

↓

Clip Studio Paint performs Undo

---

# JSON Protocol

Example

{
    "type": "command",
    "action": "undo"
}

Example

{
    "type": "command",
    "action": "zoom",
    "value": 1.25
}

---

# Android Modules

MainActivity

↓

Navigation

↓

Features

Home

Connection

Macro Pad

Workspace

Gesture Pad

Settings

About

Each feature owns its own ViewModel.

Business logic is never placed inside Composables.

---

# Windows Companion Modules

Connection Server

↓

Protocol Parser

↓

Command Dispatcher

↓

Input Controller

↓

Plugin Manager

↓

Application Profiles

---

# Data Storage

Android

DataStore

- Settings
- Theme
- Preferences

Room Database

- Workspaces
- Macros
- Profiles
- Button Layouts

---

# Error Handling

If connection is lost

↓

Attempt reconnect

↓

Notify user

↓

Restore previous workspace

No commands should be silently discarded.

---

# Security

No internet services required.

Communication occurs only between Android and the user's computer.

Future versions may support encrypted communication.

---

# Scalability

Future additions should not require architectural changes.

Examples

- Plugin support
- New software profiles
- Cloud backup
- Multi-device synchronization
- AI-assisted workflows

---

# Design Principles

- Single Responsibility
- Modular Features
- Offline First
- Touch First
- Responsive UI
- Reusable Components
- Stable APIs
- Low Latency
- Maintainable Code

---

# Architecture Summary

Android App

↓

Connection Layer

↓

Windows Companion

↓

Input Simulation

↓

Creative Software

This layered approach keeps OniCanvas flexible, maintainable, and ready for future expansion without requiring major architectural changes.