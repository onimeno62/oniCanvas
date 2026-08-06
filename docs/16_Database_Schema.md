# 16 - Database Schema

Version: 1.0
Status: Active

---

# Purpose

This document defines the local data storage architecture for OniCanvas.

The application uses:

• Room Database → Structured application data

• DataStore → User preferences

No cloud storage is required for the MVP.

---

# Storage Overview

DataStore

↓

Application Settings

Theme

Language

Last Workspace

Connection Preferences

Developer Options

---

Room Database

↓

Workspaces

Macros

Buttons

Layouts

Profiles

Palettes

Brush Favorites

Recent Colors

History

---

# Database

Database Name

onicanvas.db

Version

1

Future migrations should preserve user data whenever possible.

---

# Entity Overview

Workspace

↓

MacroPad

↓

MacroButton

↓

Action

Workspace

↓

GestureProfile

Workspace

↓

RadialMenu

Workspace

↓

TouchpadProfile

Workspace

↓

ColorPalette

Workspace

↓

BrushFavorites

---

# Workspace Entity

Table

workspaces

Columns

id

name

description

icon

theme

createdAt

updatedAt

isDefault

layoutId

gestureProfileId

macroPadId

radialMenuId

touchpadProfileId

colorPaletteId

brushProfileId

Indexes

id

name

---

# Macro Pad Entity

Table

macro_pads

Columns

id

workspaceId

name

rows

columns

pageCount

createdAt

updatedAt

---

# Macro Button Entity

Table

macro_buttons

Columns

id

macroPadId

page

row

column

label

icon

color

actionId

enabled

visible

repeatEnabled

repeatDelay

longPressActionId

hapticEnabled

Indexes

macroPadId

page

---

# Action Entity

Table

actions

Columns

id

type

name

description

shortcut

command

delay

repeat

createdAt

---

# Gesture Profile Entity

Table

gesture_profiles

Columns

id

workspaceId

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

# Radial Menu Entity

Table

radial_menus

Columns

id

workspaceId

name

itemCount

backgroundColor

blurEnabled

animation

---

# Radial Item Entity

Table

radial_items

Columns

id

menuId

position

label

icon

color

actionId

enabled

---

# Touchpad Profile Entity

Table

touchpad_profiles

Columns

id

workspaceId

name

sensitivity

scrollSpeed

precisionMode

invertScroll

tapToClick

---

# Brush Favorites Entity

Table

brush_favorites

Columns

id

workspaceId

name

icon

shortcut

category

sortOrder

---

# Color Palette Entity

Table

color_palettes

Columns

id

workspaceId

name

favorite

createdAt

---

# Palette Color Entity

Table

palette_colors

Columns

id

paletteId

hex

red

green

blue

alpha

sortOrder

---

# Recent Color Entity

Table

recent_colors

Columns

id

hex

usedAt

---

# Profile Entity

Table

profiles

Columns

id

name

author

description

version

createdAt

modifiedAt

jsonData

---

# Connection History Entity

Table

connection_history

Columns

id

connectionType

connectedAt

disconnectedAt

latency

status

---

# Relationships

Workspace

1

↓

Many

Macro Pads

---

Macro Pad

1

↓

Many

Macro Buttons

---

Macro Button

1

↓

1

Action

---

Workspace

1

↓

1

Gesture Profile

---

Workspace

1

↓

1

Touchpad Profile

---

Workspace

1

↓

1

Radial Menu

↓

Many

Radial Items

---

Workspace

1

↓

Many

Brush Favorites

---

Workspace

1

↓

Many

Color Palettes

↓

Many

Palette Colors

---

# DataStore

Stores

Theme

Accent Color

Language

Auto Connect

Connection Type

Developer Mode

Animations

Haptics

Last Workspace

Last Connected Device

Accessibility Settings

---

# Database Access

Architecture

UI

↓

ViewModel

↓

Repository

↓

DAO

↓

Room

No screen should access the database directly.

---

# Migration Strategy

Each schema update must include:

Migration Script

Database Version

Compatibility Notes

Existing user data should always be preserved whenever possible.

---

# Backup

Future

Export

↓

JSON

↓

Import

↓

Restore

Entire workspaces should be portable between devices.

---

# Performance

Indexes

Workspace ID

Macro Pad ID

Palette ID

Action ID

Use lazy loading for large datasets.

Avoid duplicate queries.

---

# Future Tables

plugin_data

cloud_sync

workspace_history

analytics

user_templates

reference_images

voice_commands

---

# Design Principles

Normalize data where practical.

Keep tables focused.

Avoid duplicate storage.

Use stable identifiers.

Support future migrations.

Keep export format independent of database schema.

---

# Summary

The OniCanvas database is designed to support a modular, scalable application while keeping user data organized and easy to migrate.

Room is used for structured project data, while DataStore stores lightweight application preferences. Together they provide a robust persistence layer that supports current features and future expansion.