# 05 - Navigation & Screens

Version: 1.0
Status: Planning

---

# Purpose

This document defines every screen in OniCanvas, its purpose, navigation flow, and the responsibilities of each screen.

Each screen should have a single responsibility and communicate with the appropriate ViewModel.

---

# Navigation Structure

Splash

↓

Onboarding (First Launch Only)

↓

Home

↓

Bottom Navigation

• Dashboard
• Workspace
• Controls
• Connection
• Settings

---

# Navigation Type

Navigation Compose

Single Activity Architecture

Each feature owns its own navigation graph when necessary.

---

# Screen Hierarchy

Main

├── Dashboard
├── Workspace
├── Controls
├── Connection
└── Settings

---

# Splash Screen

Purpose

Display app logo while initializing the application.

Responsibilities

- Initialize DataStore
- Load settings
- Check onboarding status
- Load saved workspace
- Navigate automatically

Possible destinations

Dashboard

or

Onboarding

---

# Onboarding

Purpose

Introduce new users to OniCanvas.

Pages

1.

Welcome

2.

Choose Theme

3.

Connection Methods

4.

Permissions

5.

Finish

Buttons

Next

Skip

Finish

---

# Dashboard

Purpose

Main landing page.

Displays

- Connection Status
- Active Software
- Active Workspace
- Quick Actions
- Recent Workspaces
- Device Battery
- Companion Status

Quick Actions

- Connect
- Macro Pad
- Gesture Pad
- Radial Menu
- Touchpad

---

# Workspace Screen

Purpose

Manage drawing workspaces.

Displays

- Workspace List
- Current Workspace
- Create
- Duplicate
- Rename
- Delete
- Import
- Export

Future

Cloud Sync

---

# Workspace Editor

Purpose

Customize a workspace.

Editable

- Buttons
- Layout
- Colors
- Icons
- Gestures
- Radial Menu
- Shortcuts

---

# Controls Screen

Purpose

Entry point for all control modules.

Modules

Macro Pad

Gesture Pad

Touchpad

Zoom Controller

Layer Controls

Canvas Controls

Color Center

Brush Favorites

Radial Menu

---

# Macro Pad

Purpose

Shortcut buttons.

Supports

- Pages
- Custom icons
- Labels
- Long press
- Custom colors

Actions

Undo

Redo

Save

Brush

Eraser

Transform

Selection

Fill

---

# Gesture Pad

Purpose

Touch gestures.

Supported

Pan

Zoom

Rotate

Undo

Redo

Custom gestures

---

# Touchpad

Purpose

Wireless mouse.

Features

- Left Click
- Right Click
- Scroll
- Middle Click
- Precision Mode

---

# Radial Menu

Purpose

Thumb-friendly menu.

Supported

4

8

12

16

buttons

Each item

- Icon
- Label
- Macro

---

# Zoom Controller

Purpose

Smooth canvas zoom.

Modes

Slider

Wheel

Buttons

---

# Canvas Controls

Purpose

Canvas navigation.

Actions

Rotate

Flip Horizontal

Flip Vertical

Reset Rotation

Fit Screen

100%

Mirror View

---

# Layer Controls

Purpose

Quick layer management.

Functions

New Layer

Duplicate

Merge

Delete

Lock

Opacity

Visibility

Mask

---

# Brush Favorites

Purpose

Fast brush switching.

Displays

- Favorites
- Recent
- Categories

---

# Color Center

Purpose

Color workflow.

Features

Color Wheel

HSV

RGB

HEX

Recent Colors

Palettes

Harmony

---

# Connection Screen

Purpose

Manage connections.

Displays

Current Status

Connected Device

Latency

Signal Strength

Connection Type

Buttons

Connect

Disconnect

Reconnect

Scan Devices

---

# Device Discovery

Purpose

Search for Windows Companion.

Methods

Wi-Fi

USB

Bluetooth

---

# Settings

Sections

General

Appearance

Connection

Controls

Gestures

Workspace

Performance

Accessibility

About

Developer

---

# Appearance Settings

Options

Theme

Accent Color

Animations

Corner Radius

Blur

Glass Effect

---

# Connection Settings

Options

Default Connection

Auto Connect

Retry Interval

Port

Timeout

Encryption (Future)

---

# Controls Settings

Configure

Button Size

Icon Size

Grid Density

Gesture Sensitivity

Touchpad Speed

Haptics

---

# About Screen

Displays

Version

Developer

License

Privacy Policy

Open Source Libraries

GitHub

---

# Developer Options

Future

Protocol Monitor

Command Log

FPS Counter

Latency Graph

Debug Mode

---

# Dialogs

Used For

Delete Confirmation

Rename Workspace

Import Profile

Export Profile

Disconnect Warning

---

# Bottom Navigation

Dashboard

Workspace

Controls

Connection

Settings

Always visible except during onboarding.

---

# Future Screens

Plugin Manager

Cloud Sync

Marketplace

Reference Viewer

Voice Commands

Statistics

---

# Navigation Principles

- Never exceed three taps to reach common actions.
- Preserve screen state when switching tabs.
- Avoid unnecessary navigation levels.
- Provide clear back navigation.
- Use dialogs for quick actions instead of full screens when appropriate.

---

# Summary

The navigation structure is designed to keep the most frequently used tools within immediate reach while allowing advanced customization without cluttering the primary workflow.