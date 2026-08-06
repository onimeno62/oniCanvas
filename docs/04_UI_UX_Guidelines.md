# 04 - UI & UX Guidelines

Version: 1.0
Status: Planning

---

# Purpose

This document defines the visual language, interaction patterns, and design principles for OniCanvas.

Every screen should feel like part of the same application, regardless of when it was developed.

---

# Design Goals

OniCanvas should feel

- Professional
- Modern
- Minimal
- Fast
- Comfortable during long drawing sessions
- Optimized for touch
- Easy to customize

The interface should reduce cognitive load while drawing.

---

# Design Principles

## Touch First

Every control should be designed for fingers.

Avoid tiny buttons.

Minimum touch target

48dp

Preferred

56dp

---

## One-Handed Use

Frequently used controls should be reachable using one thumb.

Examples

- Undo
- Redo
- Zoom
- Brush
- Eraser

---

## Minimize Distractions

The UI should never compete with the drawing application.

Animations should be subtle.

Avoid unnecessary popups.

---

## Consistency

Buttons with the same purpose should always look and behave the same.

Example

Primary action

Filled button

Secondary action

Outlined button

Danger

Red filled button

---

# Design Style

Material 3

Combined with

- Glassmorphism
- Soft shadows
- Rounded corners
- Blur effects
- Smooth animations

Avoid excessive visual effects that reduce readability.

---

# Color Palette

## Primary

Deep Indigo

## Secondary

Electric Purple

## Accent

Cyan

## Success

Green

## Warning

Orange

## Error

Red

---

# Themes

## Dark (Default)

Designed for artists working in low-light environments.

Characteristics

- Dark surfaces
- Bright accents
- Reduced eye strain

---

## Light

Clean professional appearance.

High contrast.

---

# Typography

Primary

Roboto Flex

Alternative

Inter

Rules

- Maximum of three font sizes per screen
- Avoid excessive bold text
- Maintain high contrast

---

# Iconography

Use Material Symbols Rounded.

Icons should be simple and recognizable.

Examples

Undo

Redo

Brush

Layers

Palette

Zoom

Settings

Connection

---

# Spacing

Use an 8dp spacing system.

Examples

4dp

Small separation

8dp

Standard spacing

16dp

Section spacing

24dp

Large groups

32dp

Screen margins

---

# Corner Radius

Small

8dp

Medium

16dp

Large

24dp

Floating Panels

28dp

---

# Elevation

Use subtle elevation.

Avoid heavy shadows.

Glass panels should rely more on blur than shadow.

---

# Animations

Duration

150–250ms

Use

- Fade
- Scale
- Slide

Avoid

- Bounce
- Flashing
- Oversized transitions

---

# Haptic Feedback

Provide haptic feedback for

- Button presses
- Radial menu selection
- Gesture completion
- Successful connection

Avoid continuous vibration.

---

# Layout Rules

Every screen should contain

Top App Bar

↓

Content

↓

Optional Floating Action Button

↓

Bottom Navigation

---

# Tablet Layout

Use two-pane layouts when possible.

Example

Navigation | Content

Landscape mode should expose more controls without cluttering the interface.

---

# Dashboard

Display

- Current workspace
- Connection status
- Active software
- Battery
- Recent workspaces
- Quick actions

---

# Macro Buttons

Each button contains

- Icon
- Label
- Optional color
- Press animation

Support multiple sizes

- Small
- Medium
- Large

---

# Gesture Pad

Large interaction area.

Minimal UI.

Only show essential controls.

---

# Radial Menu

Support

- 4 items
- 8 items
- 12 items
- 16 items

Highlight the selected item clearly.

---

# Accessibility

Support

- Dynamic text
- Screen readers
- High contrast
- Reduced motion
- Colorblind-friendly accents

---

# Performance

Maintain smooth animations.

Target

60 FPS

Avoid unnecessary recomposition.

Optimize for both phones and tablets.

---

# Responsive Design

Supported orientations

- Portrait
- Landscape

Supported devices

- Phones
- Foldables
- Tablets

Layouts should adapt automatically.

---

# UX Principles

Every action should

- Be predictable
- Provide immediate feedback
- Be reversible where possible
- Minimize the number of taps
- Keep important actions within reach

---

# Visual Identity

OniCanvas should feel like a premium creative tool rather than a generic utility.

The UI should inspire confidence while staying lightweight, clean, and focused on helping artists work faster.
