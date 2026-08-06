# 07 - Data Models

Version: 1.0
Status: Planning

---

# Purpose

This document defines every data model used by OniCanvas.

Goals

• Consistent architecture

• Reusable models

• JSON compatibility

• Easy Room persistence

• Easy synchronization

---

# Data Sources

The application stores information in three places.

DataStore

Stores

- Theme
- Preferences
- Last workspace
- Connection settings

Room Database

Stores

- Workspaces
- Buttons
- Macros
- Profiles
- Recent colors

Windows Companion

Stores

- Connected software
- Active profile
- Runtime state

---

# Core Models

## Workspace

Represents an entire drawing workspace.

Properties

id

name

description

icon

theme

createdAt

updatedAt

isDefault

layoutId

macroPadId

gestureProfileId

radialMenuId

touchpadProfileId

colorPaletteId

brushProfileId

---

## Macro Button

Represents one shortcut button.

Properties

id

workspaceId

label

icon

color

page

row

column

enabled

visible

actionId

longPressActionId

repeatEnabled

repeatDelay

hapticEnabled

---

## Action

Represents an executable command.

Properties

id

type

name

description

command

shortcut

delay

repeat

---

Action Types

Keyboard Shortcut

Mouse Click

Mouse Move

Macro Sequence

Plugin Command

Future AI Action

---

## Macro Sequence

Collection of actions.

Properties

id

name

actions[]

delayBetweenActions

repeat

---

Example

Undo

↓

Delay

↓

Save

↓

Delay

↓

Brush

---

## Gesture Profile

Properties

id

name

tap

doubleTap

longPress

swipeLeft

swipeRight

swipeUp

swipeDown

pinch

rotate

twoFingerTap

threeFingerSwipe

---

## Radial Menu

Properties

id

name

numberOfItems

animation

backgroundColor

blurEnabled

---

## Radial Item

Properties

id

menuId

position

icon

label

color

actionId

enabled

---

## Touchpad Profile

Properties

id

name

sensitivity

scrollSpeed

precisionMode

invertScroll

tapToClick

---

## Brush Favorite

Properties

id

workspaceId

name

category

icon

shortcut

order

---

## Color Palette

Properties

id

workspaceId

name

colors[]

favorite

---

## Color

Properties

id

hex

red

green

blue

alpha

favorite

lastUsed

---

## Layer Action

Properties

id

name

shortcut

icon

order

---

## Canvas Action

Properties

id

name

shortcut

icon

---

## Profile

Represents an exportable workspace.

Properties

id

version

name

author

description

createdAt

modifiedAt

workspaces[]

---

# Settings Model

Stores application preferences.

General

Theme

Accent Color

Animations

Haptics

Language

Auto Save

---

Connection

Default Method

Auto Connect

Timeout

Retry Interval

Port

Encryption

---

Controls

Button Size

Icon Size

Grid Density

Gesture Sensitivity

Touchpad Speed

---

Accessibility

Large Text

High Contrast

Reduce Motion

Screen Reader

---

Developer

Debug Logging

FPS Counter

Protocol Monitor

Command History

---

# Connection Model

Represents the current connection.

Properties

status

type

host

port

latency

signalStrength

connectedSoftware

version

---

Connection Status

Disconnected

Searching

Connecting

Connected

Error

---

Connection Types

Wi-Fi

USB

Bluetooth

---

# Command Model

Every action is converted into a command.

Properties

id

type

action

timestamp

payload

---

Example

{
  "type": "command",
  "action": "undo"
}

Example

{
  "type": "command",
  "action": "zoom",
  "payload": {
      "value": 1.25
  }
}

---

# Event Model

Events are returned from the Windows Companion.

Examples

Connected

Disconnected

WorkspaceChanged

ProfileLoaded

LatencyUpdated

SoftwareChanged

---

# Error Model

Properties

code

message

severity

timestamp

---

Severity

Info

Warning

Error

Critical

---

# Import/Export

Supported Format

JSON

Contains

Settings

Workspace

Macros

Buttons

Themes

Layouts

Future

Cloud synchronization

---

# Database Relationships

Workspace

↓

Macro Pad

↓

Macro Buttons

↓

Actions

Workspace

↓

Gesture Profile

Workspace

↓

Color Palette

Workspace

↓

Brush Favorites

Workspace

↓

Touchpad Profile

---

# Versioning

Every exported profile should contain

Profile Version

Application Version

Minimum Compatible Version

This ensures future releases remain backward compatible.

---

# Design Principles

• Immutable models where possible

• Stable identifiers

• JSON serialization

• Room compatibility

• Easy migration

• Future-proof structure

---

# Summary

The data model layer provides a consistent structure for every feature in OniCanvas.

All future development should reuse these models instead of introducing duplicate or feature-specific data structures.