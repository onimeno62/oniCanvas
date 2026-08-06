# 06 - Feature Specification

Version: 1.0
Status: Planning

---

# Purpose

This document defines every major feature of OniCanvas, including functionality, UI behavior, user interactions, edge cases, and future improvements.

Each feature should remain modular and independent whenever possible.

---

# Feature Categories

Core

- Dashboard
- Connection
- Workspace

Controls

- Macro Pad
- Gesture Pad
- Touchpad
- Radial Menu
- Zoom Controller

Creative

- Brush Favorites
- Color Center
- Layer Controls
- Canvas Controls

Customization

- Workspace Editor
- Themes
- Macro Builder

System

- Settings
- Companion
- Profile Management

---

# Dashboard

## Purpose

Provide an overview of the current session.

## Displays

Current Workspace

Current Software

Connection Status

Battery

Latency

Quick Actions

Recent Workspaces

---

## Actions

Open Workspace

Connect

Disconnect

Open Controls

Settings

---

## Future

Recent files

Usage statistics

Notifications

---

# Connection

## Purpose

Manage communication with the Windows Companion.

---

## Supported Methods

Wi-Fi

USB (ADB)

Bluetooth

---

## Status

Disconnected

Searching

Connecting

Connected

Error

---

## User Actions

Connect

Disconnect

Reconnect

Refresh

---

## Requirements

Reconnect automatically after unexpected disconnects.

Display latency.

Show connection quality.

---

# Workspace

## Purpose

Store layouts and user preferences.

---

## Workspace Contains

Macro layouts

Gesture settings

Color palettes

Radial menus

Theme overrides

Favorite brushes

---

## Actions

Create

Rename

Duplicate

Delete

Export

Import

Reset

---

# Macro Pad

## Purpose

Quick keyboard shortcuts.

---

## Layout

Grid

Supports

2x2

3x3

4x4

5x5

Scrollable pages

---

## Button Properties

Label

Icon

Color

Shortcut

Long Press Action

Repeat Action

Enabled

Hidden

---

## Supported Actions

Undo

Redo

Save

Brush

Eraser

Fill

Selection

Transform

Copy

Paste

Custom Shortcut

---

## Future

Folders

Nested pages

Animated icons

---

# Gesture Pad

## Purpose

Touch-based control surface.

---

## Gestures

Tap

Double Tap

Long Press

Swipe

Pinch

Rotate

Two Finger Swipe

Three Finger Swipe

---

## Mapping

Every gesture can trigger

Shortcut

Macro

Mouse event

Future plugin command

---

## User Settings

Sensitivity

Dead Zone

Gesture Delay

Haptics

---

# Touchpad

## Purpose

Use Android as a wireless mouse.

---

## Functions

Move

Left Click

Right Click

Middle Click

Scroll

Drag

Precision Mode

---

## Future

Pen Mode

Pressure simulation

---

# Radial Menu

## Purpose

Fast thumb-access controls.

---

## Layout Options

4

6

8

12

16

---

## Item Properties

Icon

Color

Label

Command

Long Press

---

## Future

Nested radial menus

Dynamic radial menus

---

# Zoom Controller

## Purpose

Smooth zoom adjustments.

---

## Modes

Slider

Wheel

Buttons

Gesture

---

## Settings

Sensitivity

Minimum Zoom

Maximum Zoom

Animation

---

# Brush Favorites

## Purpose

Quick access to commonly used brushes.

---

## Functions

Favorite

Unfavorite

Recent Brushes

Categories

Search

---

## Future

Brush previews

Brush import

---

# Color Center

## Purpose

Color workflow.

---

## Components

Color Wheel

HSV

RGB

HEX

Opacity

Recent Colors

Saved Palettes

Harmony

Gradient Generator

---

## Future

Eyedropper integration

Shared palettes

---

# Layer Controls

## Functions

New Layer

Delete

Duplicate

Merge

Rename

Visibility

Opacity

Lock

Mask

Clipping

Blend Mode

---

## Future

Layer thumbnails

Layer tree

---

# Canvas Controls

## Functions

Rotate Left

Rotate Right

Flip Horizontal

Flip Vertical

Reset Rotation

Fit Screen

100%

Mirror View

Navigator

---

# Workspace Editor

## Purpose

Customize workspaces.

---

## Editable

Grid size

Buttons

Colors

Icons

Gestures

Themes

Layouts

---

## Future

Drag & Drop editor

Preview mode

---

# Macro Builder

## Purpose

Create custom commands.

---

## Supports

Keyboard shortcuts

Mouse clicks

Mouse movement

Delays

Repeats

Sequences

Conditions (Future)

---

## Example

Ctrl + Z

↓

Delay

↓

Ctrl + Shift + S

---

# Themes

## Built-in

Dark

Light

OLED

Minimal

Glass

---

## Customization

Accent

Corner Radius

Blur

Animations

Typography

---

# Profile Management

## Functions

Import

Export

Duplicate

Backup

Restore

---

## Formats

JSON

Future

Cloud

---

# Settings

General

Appearance

Connection

Controls

Gestures

Accessibility

Developer

About

---

# Notifications

Show

Connection Lost

Battery Low

Update Available

Import Complete

Export Complete

---

# Companion Communication

Android

↓

JSON

↓

Windows Companion

↓

Keyboard

↓

Creative Software

---

# Error Handling

Invalid Profile

Connection Timeout

Macro Failure

Missing Companion

Unsupported Version

---

# Accessibility

Dynamic Text

Large Buttons

High Contrast

Reduced Motion

Screen Reader Support

---

# Future Features

Plugin System

Voice Commands

Reference Viewer

AI Workspace Suggestions

Cloud Sync

Cross-device Sync

Statistics Dashboard

Community Profiles

---

# Acceptance Criteria

Every feature should

✓ Have clear UI feedback

✓ Handle errors gracefully

✓ Support light & dark themes

✓ Preserve user settings

✓ Be modular

✓ Be testable

✓ Be documented

---

# Summary

The Feature Specification is the primary implementation guide for OniCanvas.

Every new feature added to the project should first be documented here before implementation begins.