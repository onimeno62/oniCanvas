# 00 - Project Brief

Version: 1.0

Status: Active

---

# Project

**OniCanvas**

Professional Android Companion for Clip Studio Paint and Creative Software.

---

# Vision

OniCanvas transforms an Android phone or tablet into a customizable wireless control surface for artists.

Instead of constantly reaching for a keyboard, artists can perform shortcuts, gestures, zooming, color selection, layer management, and other creative actions directly from their Android device.

The application is designed to improve workflow speed, comfort, and productivity while remaining lightweight and highly customizable.

---

# Primary Goal

Create the best companion application for digital artists.

The application should feel like a professional creative tool rather than a generic remote-control app.

---

# Target Platform

Android

Minimum SDK

26

Target SDK

Latest Stable

Windows Companion

Windows 10+

Future

macOS

Linux

---

# Technology Stack

Android

- Kotlin
- Jetpack Compose
- Material 3
- MVVM
- Navigation Compose
- Coroutines
- StateFlow
- Room
- DataStore
- Kotlin Serialization

Windows

- Kotlin JVM (preferred) or .NET if required for OS integration
- JSON Protocol
- Keyboard / Mouse Input Simulation

---

# Architecture

Android

↓

Connection Layer

↓

Windows Companion

↓

Creative Software

Android never communicates directly with Clip Studio Paint.

The Windows Companion translates protocol messages into desktop input.

---

# Supported Software

Current

- Clip Studio Paint

Planned

- Adobe Photoshop
- Krita
- Paint Tool SAI
- Blender
- Illustrator
- OBS Studio
- DaVinci Resolve
- Premiere Pro

---

# Design Language

Material 3

Glassmorphism

Soft Neumorphism

Dark Theme First

Tablet Optimized

Touch First

Responsive Layouts

Reusable Design System

---

# Core Features

Dashboard

Workspace Management

Macro Pad

Gesture Pad

Touchpad

Radial Menu

Zoom Controller

Layer Controls

Canvas Controls

Brush Favorites

Color Center

Settings

Connection Manager

Windows Companion

---

# Project Principles

Build incrementally.

Every version should compile.

Every release should be usable.

Never rewrite existing architecture without approval.

Prefer reusable components.

Keep business logic out of Composables.

Use immutable UI state.

Maintain a modular architecture.

---

# Current Development Phase

Version

0.1 Foundation

Current Goals

- Create Android project
- Material 3
- Navigation
- Theme
- Dashboard
- Settings
- DataStore
- Project structure

Do not implement advanced drawing controls yet.

---

# AI Development Rules

When generating code:

- Inspect the existing project first.
- Preserve architecture.
- Use MVVM.
- Follow the Design System.
- Follow the Communication Protocol.
- Reuse components.
- Explain changes before code.
- Generate production-quality Kotlin.
- Avoid placeholder implementations unless requested.

---

# Documentation Index

00_Project_Brief.md

01_Project_Overview.md

02_Product_Roadmap.md

03_System_Architecture.md

04_UI_UX_Guidelines.md

05_Navigation_and_Screens.md

06_Feature_Specification.md

07_Data_Models.md

08_Communication_Protocol.md

09_Project_Structure.md

10_AI_Studio_Instructions.md

11_Windows_Companion.md

12_Development_Guidelines.md

13_Backlog.md

14_Design_System.md

15_JSON_API_Reference.md

16_Database_Schema.md

---

# Instructions for Google AI Studio

Before implementing any feature:

1. Read this document.

2. Follow the relevant specification documents.

3. Maintain consistency with the existing architecture.

4. Build only the requested milestone.

5. Do not add unrelated features.

6. Keep all code modular, documented, and production-ready.

---

# Success Criteria

The project should become a reliable, customizable, and extensible companion application that helps artists work faster while minimizing keyboard usage.

Every feature should improve workflow without increasing complexity.

---

# Motto

**Create faster. Draw smarter. Stay focused.**